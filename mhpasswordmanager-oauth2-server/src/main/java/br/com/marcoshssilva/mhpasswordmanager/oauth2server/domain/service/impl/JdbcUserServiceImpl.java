package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.configuration.AuthorizationConfigProperties;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.constants.UserRolesEnum;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.BusinessRuleException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.CannotRegisterUserException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.FailSendEmailException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RecoveryPasswordCodeRequest;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RegisteredUserData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RegisteredUserKeyVerificationMailMessage;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RegisteredUserMailMessage;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RequestedBrowserParams;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.AbstractUserService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.SendEmailService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.UserService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.mappers.RecoveryPasswordCodeRequestMapper;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.mappers.RegisteredUserDataMapper;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.utils.KeyGeneratorUtils;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.data.models.UserRegistrationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class JdbcUserServiceImpl extends AbstractUserService implements UserService {
    public static final String QUERY_SEARCHIFUSERNAMEOREMAILEXISTS = "SELECT ud.username, ud.email, ud.firstname, ud.lastName, u.enabled FROM users u INNER JOIN users_details ud ON u.username = ud.username WHERE ud.email = ? OR ud.username = ?";
    public static final String QUERY_UPDATEUSERPASSWORD = "UPDATE users u SET password = ? WHERE username = ?";
    public static final String QUERY_SAVERECOVERYCODEGENERATEDFORUSER = "INSERT INTO users_recovery_password_code(code, username, ip_client, user_agent_client, created_at, expires_at, completed) VALUES(?, ?, ?, ?, ?, ?, ?)";
    public static final String QUERY_GETRECOVERYCODEGENERATEDFORUSER = "SELECT code, username, ip_client, user_agent_client, created_at, expires_at, completed FROM users_recovery_password_code WHERE code = ? AND ip_client = ? AND user_agent_client = ? AND completed = false AND expires_at > ? AND completed = false";
    public static final String QUERY_GETCOUNTRECOVERYCODESACTIVEFORUSER = "SELECT count(code) FROM users_recovery_password_code WHERE username = ? AND completed = false AND expires_at > ?";
    private static final String QUERY_GETUSERNAMEBYUUIDCODE = "SELECT username FROM users_verify_codes WHERE uuid_code = ?";
    private static final String QUERY_UPDATEUSERDETAILSVERIFIED = "UPDATE users_details SET verified = true, verified_at = current_timestamp WHERE username = ?";
    private static final String QUERY_DELETEUSERVERIFYCODE = "DELETE FROM users_verify_codes WHERE uuid_code = ?";
    private static final String QUERY_SAVEUSERDETAILS = "INSERT INTO users_details(username, email, firstname, lastname, verified) values(?, ?, ?, ?, ?)";
    private static final String QUERY_SAVEVERIFYCODE = "INSERT INTO users_verify_codes(uuid_code, username) VALUES (?, ?)";

    private final JdbcTemplate jdbcTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final AuthorizationConfigProperties authorizationConfigProperties;
    private final SendEmailService sendEmailService;

    public JdbcUserServiceImpl(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder, JdbcTemplate jdbcTemplate, RabbitTemplate rabbitTemplate, AuthorizationConfigProperties authorizationConfigProperties, SendEmailService sendEmailService) {
        super(userDetailsManager, passwordEncoder);
        this.jdbcTemplate = jdbcTemplate;
        this.rabbitTemplate = rabbitTemplate;
        this.authorizationConfigProperties = authorizationConfigProperties;
        this.sendEmailService = sendEmailService;
    }

    @Override
    @Transactional
    public void registerNewUser(UserRegistrationData userRegistrationData, UserRolesEnum role) {
        // check if email already used by another account
        RegisteredUserData user = getUserByUsernameOrEmail(userRegistrationData.getUsername(), userRegistrationData.getEmail());
        if (Objects.nonNull(user)) {
            throw new CannotRegisterUserException(ERROR_MESSAGE_CANNOT_USE);
        }
        // save user and user details
        super.registerNewUser(userRegistrationData, role);
        jdbcTemplate.update(QUERY_SAVEUSERDETAILS, userRegistrationData.getUsername(), userRegistrationData.getEmail(), userRegistrationData.getFirstName(), userRegistrationData.getLastName(), Boolean.FALSE);

        // send email to user with link to verify account
        final String uuidRegistration = UUID.randomUUID().toString();
        jdbcTemplate.update(QUERY_SAVEVERIFYCODE, uuidRegistration, userRegistrationData.getUsername());
        String link = authorizationConfigProperties.getIssuerUri().concat("/verify/").concat(uuidRegistration);
        rabbitTemplate.convertAndSend(
                "email.send-confirmation-registered-user",
                RegisteredUserMailMessage.builder()
                        .email(userRegistrationData.getEmail())
                        .name(userRegistrationData.getFirstName().concat(" ").concat(userRegistrationData.getLastName()))
                        .link(link)
                        .build());
    }

    @Override
    public void generateAndSendConfirmationCodeToResetPassword(String email, RequestedBrowserParams requestedBrowserParams) throws FailSendEmailException {
        // check if email or username is registered
        RegisteredUserData registeredUserData = this.getUserByUsernameOrEmail(email, email);
        if (registeredUserData == null) {
            throw new FailSendEmailException("Cannot send email, user doesn't have registered account.");
        }

        // generate code
        String code = KeyGeneratorUtils.generateRecoveryCodeToResetPassword();
        // save and send by mail-service queue
        saveCodeEmailRecoveryPassword(registeredUserData, code, requestedBrowserParams);
        sendCodeEmailRecoveryPassword(registeredUserData, code);
    }

    @Override
    public void resetPasswordFromRecoveryPasswordCodeRequest(String code, String newPassword, RequestedBrowserParams requestedBrowserParams) throws BusinessRuleException {
        try {
            RecoveryPasswordCodeRequest codeRequest = this.jdbcTemplate.queryForObject(QUERY_GETRECOVERYCODEGENERATEDFORUSER, RecoveryPasswordCodeRequestMapper.getInstance(), code, requestedBrowserParams.getIpAddress(), requestedBrowserParams.getUserAgent(), LocalDateTime.now(Clock.systemUTC()));
            if (codeRequest == null) {
                throw new BusinessRuleException("Cannot reset password, codeRequest not found.");
            }
            if (codeRequest.getUsername() == null) {
                throw new BusinessRuleException("Cannot reset password, username not found.");
            }
            this.resetUserPassword(codeRequest.getUsername(), newPassword);
        } catch (Exception e) {
            throw new BusinessRuleException("Cannot reset password, please try again later.");
        }
    }

    @Override
    public void resetUserPassword(String username, String newPassword) throws BusinessRuleException {
        int rowsUpdated = this.jdbcTemplate.update(QUERY_UPDATEUSERPASSWORD, getPasswordEncoder().encode(newPassword), username);
        if (rowsUpdated == 0) {
            throw new BusinessRuleException("Cannot reset password, user not found.");
        }
    }

    @Override
    public void verifyUserAccount(String uuidCode, RequestedBrowserParams browserParams) throws BusinessRuleException {
        try {
            String username = this.jdbcTemplate.queryForObject(QUERY_GETUSERNAMEBYUUIDCODE, String.class, uuidCode);
            int rowsUpdated = this.jdbcTemplate.update(QUERY_UPDATEUSERDETAILSVERIFIED, username);
            if (rowsUpdated == 0) {
                throw new BusinessRuleException("Cannot verify user account, user details not found.");
            }
            this.jdbcTemplate.update(QUERY_DELETEUSERVERIFYCODE, uuidCode);
        } catch (EmptyResultDataAccessException | BusinessRuleException e) {
             throw new BusinessRuleException("Cannot verify user account, invalid or expired code.");
        }
    }

    private void sendCodeEmailRecoveryPassword(RegisteredUserData client, String code) throws FailSendEmailException {
        this.sendEmailService.sendEmailRecoveryPassword(RegisteredUserKeyVerificationMailMessage.builder()
                .code(code)
                .email(client.getEmail())
                .name(String.format("%s %s", client.getFirstName(), client.getLastName()))
                .build());
    }

    private void saveCodeEmailRecoveryPassword(RegisteredUserData client, String code, RequestedBrowserParams requestedBrowserParams) throws FailSendEmailException {
        Integer qtdOpenCodesToRecoveryPassword = this.jdbcTemplate.queryForObject(QUERY_GETCOUNTRECOVERYCODESACTIVEFORUSER, Integer.class, client.getUsername(), LocalDateTime.now(Clock.systemUTC()));
        if (qtdOpenCodesToRecoveryPassword == null) {
            throw new FailSendEmailException("Cannot send email, user has no recovery codes active. Please, check your email.");
        }
        if (qtdOpenCodesToRecoveryPassword > 2) {
            throw new FailSendEmailException("Cannot send email, user has to many recovery codes active. Please, check your email.");
        }
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        this.jdbcTemplate.update(QUERY_SAVERECOVERYCODEGENERATEDFORUSER, code, client.getUsername(), requestedBrowserParams.getIpAddress(), requestedBrowserParams.getUserAgent(), now, now.plusHours(3L), Boolean.FALSE);
    }

    private RegisteredUserData getUserByUsernameOrEmail(String username, String email) {
        try {
            return jdbcTemplate.queryForObject(QUERY_SEARCHIFUSERNAMEOREMAILEXISTS, new RegisteredUserDataMapper(), email, username);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }
}
