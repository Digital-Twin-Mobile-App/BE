package com.project.dadn.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = { ConfirmPasswordValidator.class })
public @interface ConfirmPassword {
    String message() default "Password does not matched";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
