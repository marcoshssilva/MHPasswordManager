package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.exceptions.UserAuthorizationCannotBeLoadedException;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest
class UserRegistrationServiceImplTests {
    @Autowired
    UserRegistrationService service;

    @Test
    void shouldLoadAuthorizationsFromJwtSubject() throws UserAuthorizationCannotBeLoadedException {
        Jwt jwt = new Jwt("token", Instant.EPOCH, Instant.EPOCH.plusSeconds(60), Map.of("alg", "none"), Map.of("sub", "john"));

        UserAuthorizations authorizations = service.getUserAuthorizations(jwt);

        assertEquals("john", authorizations.getUsername());
        assertTrue(authorizations.getRoles().isEmpty());
        assertTrue(authorizations.getProfiles().isEmpty());
    }
}
