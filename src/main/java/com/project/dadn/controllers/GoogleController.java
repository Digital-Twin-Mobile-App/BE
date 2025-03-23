package com.project.dadn.controllers;

import com.project.dadn.dtos.responses.APIResponse;
import com.project.dadn.dtos.responses.AuthenticationResponse;
import com.project.dadn.services.GoogleService;
import com.project.dadn.services.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class GoogleController {

    private final GoogleService googleService;
    private final OtpService otpService;

    @GetMapping
    public APIResponse<AuthenticationResponse> welcome(OAuth2AuthenticationToken authenticationToken) {
        AuthenticationResponse res = googleService.authenticateWithGoogle(authenticationToken);
        return APIResponse.<AuthenticationResponse>builder()
                .result(res)
                .build();
    }

    @GetMapping("/google/user")
    public Principal getUser(Principal principal) {
        if (principal != null) {
            System.out.println("Username: " + principal.getName());
        }
        return principal;
    }

    //    Check OTP
    @GetMapping("/verify")
    APIResponse<AuthenticationResponse> verifyOTP(
            @RequestParam("otp") String otp,
            @RequestParam("email") String email) {
        AuthenticationResponse res = otpService.verifyOtp(otp, email);
        return APIResponse.<AuthenticationResponse>builder()
                .result(res)
                .build();
    }


}
