package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.validations;


import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.web.data.models.UserRegistrationData;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.Serializable;

@RequiredArgsConstructor
public class UserRegistrationMustHavePasswordAndConfirmationEqualsConstraintValidator implements ConstraintValidator<UserRegistrationMustHavePasswordAndConfirmationEquals, UserRegistrationData>, Serializable {
    @Override
    public void initialize(UserRegistrationMustHavePasswordAndConfirmationEquals constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserRegistrationData user, ConstraintValidatorContext context) {
        return user.getPassword().equals(user.getConfirmationPassword());
    }
}
