package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models.UserRegisteredModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
public class UserRegistrationServiceImplTests {
    @Autowired
    UserRegistrationService service;

    @DisplayName("Test if can create an user")
    @Test
    public void shouldCreateUser() {
        assertDoesNotThrow(() -> {
            String email = "johndoe@gmail.com";
            UserRegisteredModel userRegistration = service.createUserRegistration(email, "Hellbound#3090");
            assertEquals(email, userRegistration.email());
        });
    }
}