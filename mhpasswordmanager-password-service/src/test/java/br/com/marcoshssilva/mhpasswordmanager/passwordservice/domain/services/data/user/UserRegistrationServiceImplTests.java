package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories.UserRegistrationRepository;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.AESCryptServiceImpl;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models.UserRegisteredModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@DataJpaTest
public class UserRegistrationServiceImplTests {

    @Autowired
    UserRegistrationRepository userRegistrationRepository;
    UserRegistrationService userRegistrationService;

    @DisplayName("Test if can create an user")
    @Test
    public void shouldCreateUser() {
        assertDoesNotThrow(() -> {
            String email = "johndoe@gmail.com";
            userRegistrationService = new UserRegistrationServiceImpl(userRegistrationRepository, new AESCryptServiceImpl());
            UserRegisteredModel userRegistration = userRegistrationService.createUserRegistration(email, "Hellbound#3090");
            assertEquals(email, userRegistration.email());
        });
    }
}