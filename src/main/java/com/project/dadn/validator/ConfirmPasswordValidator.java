package com.project.dadn.validator;

import com.project.dadn.dtos.requests.ChangePasswordRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfirmPasswordValidator implements ConstraintValidator<ConfirmPassword, ChangePasswordRequest> {

    @Override
    public void initialize(ConfirmPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(ChangePasswordRequest request, ConstraintValidatorContext constraintValidatorContext) {
        if (request == null || request.getNewPassword() == null || request.getConfirmPassword() == null) {
            return false;
        }


        boolean isValid = request.getNewPassword().equals(request.getConfirmPassword());

        if (!isValid) {
            log.warn("Validation failed: Passwords do not match!");
        }

        return isValid;
    }

}
