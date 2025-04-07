package com.project.dadn.controllers;

import com.nimbusds.jose.JOSEException;
import com.project.dadn.dtos.requests.*;
import com.project.dadn.dtos.responses.APIResponse;
import com.project.dadn.dtos.responses.AuthenticationResponse;
import com.project.dadn.services.AuthenticationService;
import com.project.dadn.services.EmailService;
import com.project.dadn.services.GoogleService;
import com.project.dadn.services.OtpService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    EmailService emailService;
    OtpService otpService;

    @PostMapping("/login")
    APIResponse<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request){
        AuthenticationResponse result = authenticationService.authenticate(request);
        return APIResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/logout")
    APIResponse<Void> logout(
            @RequestBody LogoutRequest request) {
        authenticationService.logout(request);
        return APIResponse.<Void>builder()
                .build();
    }

    @PostMapping("/refresh")
    APIResponse<AuthenticationResponse> refreshToken(
            @Valid @RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        AuthenticationResponse res = authenticationService.refreshToken(request);
        return APIResponse.<AuthenticationResponse>builder()
                .result(res)
                .build();
    }

//    GET OTP
    @PostMapping("/verifyEmail")
    APIResponse<AuthenticationResponse> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request) {
        emailService.sendEmailReset(request);
        return APIResponse.<AuthenticationResponse>builder()
                .build();
    }

//    Reset Password
    @PostMapping("/resetPassword")
    APIResponse<AuthenticationResponse> resetPassword(
            @Valid @RequestBody ChangePasswordRequest request) throws ParseException {
        authenticationService.changePassword(request);
        return APIResponse.<AuthenticationResponse>builder()
                .message("Password changed successfully. Please log in again.")
                .build();
    }

//    Change Password
    @PostMapping("/changePassword")
    APIResponse<AuthenticationResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) throws ParseException {
        AuthenticationResponse res = authenticationService.changePassword(request);
        return APIResponse.<AuthenticationResponse>builder()
                .result(res)
                .build();
    }

//    Check OTP
    @PostMapping("/verify-otp")
    APIResponse<AuthenticationResponse> verifyOTP(
            @RequestBody OtpVerificationRequest request) {
        AuthenticationResponse res = otpService.verifyOtp(request.getOtp(), request.getEmail());
        return APIResponse.<AuthenticationResponse>builder()
                .result(res)
                .build();
    }


}
