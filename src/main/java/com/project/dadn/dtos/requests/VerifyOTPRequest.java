package com.project.dadn.dtos.requests;

import com.project.dadn.utlls.MessageKeys;
import com.project.dadn.validator.ValidEmail;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOTPRequest {

    @ValidEmail(message = MessageKeys.INVALID_EMAIL)
    private String email;

    @NotBlank
    private String otp;


}
