package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.impl;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.configuration.AuthorizationConfigProperties;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.constants.UserRolesEnum;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.CannotRegisterUserException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.FailSendEmailException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RegisteredUserData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RegisteredUserKeyVerificationMailMessage;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RegisteredUserMailMessage;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RequestedBrowserParams;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.SendEmailService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.UserService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.mappers.RegisteredUserDataMapper;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.utils.KeyGeneratorUtils;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.data.models.UserRegistrationData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class JdbcUserServiceImpl implements UserService {
    public static final String qrySearchIfUsernameOrEmailExists = "SELECT ud.username, ud.email, ud.firstname, ud.lastName, u.enabled FROM users u INNER JOIN users_details ud ON u.username = ud.username WHERE ud.email = ? OR ud.username = ?";
    public static final String qrySaveRecoveryCodeGeneratedForUser = "INSERT INTO users_recovery_password_code(code, username, ip_client, user_agent_client, created_at, expires_at, completed) VALUES(?, ?, ?, ?, ?, ?, ?)";
    public static final String qryGetCountRecoveryCodesActiveForUser = "SELECT count(code) FROM users_recovery_password_code WHERE username = ? AND completed = false AND expires_at > ?";

    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final AuthorizationConfigProperties authorizationConfigProperties;
    private final SendEmailService sendEmailService;

    @Override
    @Transactional
    public void registerNewUser(UserRegistrationData userRegistrationData, UserRolesEnum role) {
        final String errorMessageCannotUse = "Cannot use this username/email.";
        // check if email already used by another account
        if (userDetailsManager.userExists(userRegistrationData.getUsername())) {
            throw new CannotRegisterUserException(errorMessageCannotUse);
        }
        List<Map<String, Object>> queryResult = jdbcTemplate.queryForList("SELECT * FROM users_details WHERE email = ?", userRegistrationData.getEmail());

        if (!queryResult.isEmpty()) {
            throw new CannotRegisterUserException(errorMessageCannotUse);
        }

        userDetailsManager.createUser(
                User.builder()
                        .username(userRegistrationData.getUsername())
                        .password(passwordEncoder.encode(userRegistrationData.getPassword()))
                        .roles(role.name())
                        .build());

        final String querySaveAccountDetails = "INSERT INTO users_details(username, email, firstname, lastname, verified) values('?','?','?','?','?')";

        final String uuidRegistration = UUID.randomUUID().toString();
        final String querySaveAccountVerifyCode = "INSERT INTO users_verify_codes(uuid_code, username) VALUES ('?', '?')";

        jdbcTemplate.update(querySaveAccountDetails, userRegistrationData.getUsername(), userRegistrationData.getEmail(), userRegistrationData.getFirstName(), userRegistrationData.getLastName(), Boolean.FALSE);
        jdbcTemplate.update(querySaveAccountVerifyCode, uuidRegistration, userRegistrationData.getUsername());

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
    public Boolean isUserExistsAccount(String email) {
        return this.getUserByUsernameOrEmail(email, email) != null;
    }

    public RegisteredUserData getUserByUsernameOrEmail(String username, String email) {
        try {
            return jdbcTemplate.queryForObject(qrySearchIfUsernameOrEmailExists, new RegisteredUserDataMapper(), email, username);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

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

    private void sendCodeEmailRecoveryPassword(RegisteredUserData client, String code) throws FailSendEmailException {
        this.sendEmailService.sendEmailRecoveryPassword(RegisteredUserKeyVerificationMailMessage.builder()
                .code(code)
                .email(client.getEmail())
                .name(String.format("%s %s", client.getFirstName(), client.getLastName()))
                .build());
    }

    private void saveCodeEmailRecoveryPassword(RegisteredUserData client, String code, RequestedBrowserParams requestedBrowserParams) throws FailSendEmailException {
        Integer qtdOpenCodesToRecoveryPassword = this.jdbcTemplate.queryForObject(qryGetCountRecoveryCodesActiveForUser, Integer.class, client.getUsername(), LocalDateTime.now());

        log.info("Qtd: {}", qtdOpenCodesToRecoveryPassword);

        if (qtdOpenCodesToRecoveryPassword > 2) {
            throw new FailSendEmailException("Cannot send email, user has to many recovery codes active. Please, check your email.");
        }
        this.jdbcTemplate.update(qrySaveRecoveryCodeGeneratedForUser, code, client.getUsername(), requestedBrowserParams.getIpAddress(), requestedBrowserParams.getUserAgent(), LocalDateTime.now(), LocalDateTime.now().plusHours(3L), Boolean.FALSE);
    }
}
