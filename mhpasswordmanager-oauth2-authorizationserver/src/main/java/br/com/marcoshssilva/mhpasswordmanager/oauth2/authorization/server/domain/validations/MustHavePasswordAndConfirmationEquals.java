package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.validations;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = MustHavePasswordAndConfirmationEqualsConstraintValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MustHavePasswordAndConfirmationEquals {
    String message() default "Password and Confirmation Password doesn't are equals.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
