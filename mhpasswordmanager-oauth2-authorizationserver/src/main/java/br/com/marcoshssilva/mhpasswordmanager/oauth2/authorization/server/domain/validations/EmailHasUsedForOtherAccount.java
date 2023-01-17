package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.validations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EmailHasUsedForOtherAccountConstraintValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailHasUsedForOtherAccount {
    String message() default "Email already in use for another account.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
