package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.service.impl;

import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.configuration.AuthorizationConfigProperties;
import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.constants.UserRolesEnum;
import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.exceptions.CannotRegisterUserException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.models.RegisteredUserMailMessage;
import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.service.UserService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.web.data.models.UserRegistrationData;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class JdbcUserServiceImpl implements UserService {
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final AuthorizationConfigProperties authorizationConfigProperties;

    @Override
    @Transactional
    public void registerNewUser(UserRegistrationData userRegistrationData, UserRolesEnum role) {
        final String errorMessageCannotUse = "Cannot use this username/email.";
        // check if email already used by another account
        if (userDetailsManager.userExists(userRegistrationData.getUsername()))
            throw new CannotRegisterUserException(errorMessageCannotUse);
        List<Map<String, Object>> query = jdbcTemplate.queryForList("SELECT * FROM users_details WHERE email = '" + userRegistrationData.getEmail() + "'");

        if (!query.isEmpty()) {
            throw new CannotRegisterUserException(errorMessageCannotUse);
        }

        userDetailsManager.createUser(
                User.builder()
                        .username(userRegistrationData.getUsername())
                        .password(passwordEncoder.encode(userRegistrationData.getPassword()))
                        .roles(role.name())
                        .build());

        final String querySaveAccountDetails = "INSERT INTO users_details(username, email, firstname, lastname, verified) values(':1?',':2?',':3?',':4?','false')"
                .replace(":1?", userRegistrationData.getUsername())
                .replace(":2?", userRegistrationData.getEmail())
                .replace(":3?", userRegistrationData.getFirstName())
                .replace(":4?", userRegistrationData.getLastName());

        final String uuidRegistration = UUID.randomUUID().toString();
        final String querySaveAccountVerifyCode = "INSERT INTO users_verify_codes(uuid_code, username) VALUES (':1?', ':2?')"
                .replace(":1?", uuidRegistration)
                .replace(":2?", userRegistrationData.getUsername());

        jdbcTemplate.execute(querySaveAccountDetails);
        jdbcTemplate.execute(querySaveAccountVerifyCode);

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
        return userDetailsManager.userExists(email);
    }

}
