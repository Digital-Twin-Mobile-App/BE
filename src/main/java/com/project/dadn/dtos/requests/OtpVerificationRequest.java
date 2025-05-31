package com.project.dadn.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OtpVerificationRequest {
    String otp;
    String email;
}
