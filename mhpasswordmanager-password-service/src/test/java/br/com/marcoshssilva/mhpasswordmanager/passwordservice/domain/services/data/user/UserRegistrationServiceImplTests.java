package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models.NewUserRegisteredModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class UserRegistrationServiceImplTests {
    @Autowired
    UserRegistrationService service;

    @DisplayName("Test if can create an user")
    @Test
    void shouldCreateUser() {
        assertDoesNotThrow(() -> {
            String email = "johndoe@gmail.com";
            NewUserRegisteredModel userRegistration = service.createUserRegistration(email, "Hellbound#3090");
            assertEquals(email, userRegistration.email());
        });
    }

    @DisplayName("Test if throw UserRegistrationException for existent account")
    @Test
    void shouldThrowUserRegistrationException() throws Exception{
        String email = "johndoe@gmail.com";
        // must work
        service.createUserRegistration(email, "Hellbound#3090");
        // must not work
        assertThrows(UserRegistrationException.class, () -> service.createUserRegistration(email, "Hellbound#3090"));
    }
}