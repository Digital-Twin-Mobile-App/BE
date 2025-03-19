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
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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
    GoogleService googleService;


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

//    Check OTP
    @PostMapping("/verifyOTP")
    APIResponse<AuthenticationResponse> verifyOTP(
            @Valid @RequestBody VerifyOTPRequest request) {
        AuthenticationResponse res = otpService.verifyOtp(request);
        return APIResponse.<AuthenticationResponse>builder()
                .result(res)
                .build();
    }

//    Reset Password
    @PostMapping("/resetPassword")
    APIResponse<AuthenticationResponse> resetPassword(
            @Valid @RequestBody ChangePasswordRequest request,
            HttpServletRequest req) throws ParseException {
        authenticationService.resetPassword(request, req);
        return APIResponse.<AuthenticationResponse>builder()
                .message("Password changed successfully. Please log in again.")
                .build();
    }


}
