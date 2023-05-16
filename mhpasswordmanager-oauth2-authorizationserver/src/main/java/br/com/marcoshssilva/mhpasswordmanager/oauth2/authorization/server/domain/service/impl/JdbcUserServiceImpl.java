package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.service.impl;

import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.constants.UserRolesEnum;
import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.exceptions.CannotRegisterUserException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.service.UserService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.web.data.models.UserRegistrationData;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class JdbcUserServiceImpl implements UserService {
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void registerNewUser(UserRegistrationData userRegistrationData, UserRolesEnum role) {
        final String errorMessageCannotUse = "Cannot use this username/email.";
        // check if email already used by another account
        if (userDetailsManager.userExists(userRegistrationData.getUsername())) throw new CannotRegisterUserException(errorMessageCannotUse);
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

        final String querySaveAccountDetails = "INSERT INTO users_details(username, email, firstname, lastname) values('?','?','?','?')"
                .replace("?", userRegistrationData.getUsername())
                .replace("?", userRegistrationData.getEmail())
                .replace("?", userRegistrationData.getFirstName())
                .replace("?", userRegistrationData.getLastName());

        jdbcTemplate.execute(querySaveAccountDetails);
    }


    @Override
    public Boolean isUserExistsAccount(String email) {
        return userDetailsManager.userExists(email);
    }

}
