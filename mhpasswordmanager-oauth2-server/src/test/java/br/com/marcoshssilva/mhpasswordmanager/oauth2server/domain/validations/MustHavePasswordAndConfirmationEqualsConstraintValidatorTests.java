package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.validations;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.data.models.UserRegistrationData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MustHavePasswordAndConfirmationEqualsConstraintValidatorTests {

    @Mock
    private ConstraintValidatorContext context;

    private MustHavePasswordAndConfirmationEqualsConstraintValidator validator;

    @BeforeEach
    void setUp() {
        validator = new MustHavePasswordAndConfirmationEqualsConstraintValidator();
    }

    @DisplayName("Should return true when password and confirmationPassword are equal")
    @Test
    void shouldReturnTrueWhenPasswordsMatch() {
        UserRegistrationData user = UserRegistrationData.builder()
                .password("Password123")
                .confirmationPassword("Password123")
                .build();

        assertTrue(validator.isValid(user, context));
    }

    @DisplayName("Should return false when password and confirmationPassword do not match")
    @Test
    void shouldReturnFalseWhenPasswordsDoNotMatch() {
        UserRegistrationData user = UserRegistrationData.builder()
                .password("Password123")
                .confirmationPassword("OtherPassword456")
                .build();

        assertFalse(validator.isValid(user, context));
    }
}
