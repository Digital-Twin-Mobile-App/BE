package com.project.dadn.dtos.requests;


import com.project.dadn.validator.ConfirmPassword;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@ConfirmPassword
public class ChangePasswordRequest {

    private String email;

    @Size(min = 8, message = "Password min 8 length")
    private String newPassword;

    @Size(min = 8, message = "Password min 8 length")
    private String confirmPassword;
}
