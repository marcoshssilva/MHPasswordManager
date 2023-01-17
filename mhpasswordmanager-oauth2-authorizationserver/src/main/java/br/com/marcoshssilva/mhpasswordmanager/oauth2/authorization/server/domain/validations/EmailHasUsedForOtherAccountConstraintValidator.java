package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.validations;


import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.service.UserService;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.Serializable;

@RequiredArgsConstructor
public class EmailHasUsedForOtherAccountConstraintValidator implements ConstraintValidator<EmailHasUsedForOtherAccount, String>, Serializable {
    private final UserService userService;

    @Override
    public void initialize(EmailHasUsedForOtherAccount constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return this.userService.isUserExistsAccount(value);
    }
}
