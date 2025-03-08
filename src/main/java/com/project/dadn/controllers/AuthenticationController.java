package com.project.dadn.controllers;

import com.nimbusds.jose.JOSEException;
import com.project.website.dtos.requests.AuthenticationRequest;
import com.project.website.dtos.requests.IntrospectRequest;
import com.project.website.dtos.requests.LogoutRequest;
import com.project.website.dtos.requests.RefreshRequest;
import com.project.website.dtos.responses.APIResponse;
import com.project.website.dtos.responses.AuthenticationResponse;
import com.project.website.dtos.responses.IntrospectResponse;
import com.project.website.services.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/introspect")
    APIResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        IntrospectResponse result = authenticationService.introspect(request);
        return APIResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/token")
    APIResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        AuthenticationResponse result = authenticationService.authenticate(request);
        return APIResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }


    @PostMapping("/logout")
    APIResponse<Void> logout(@RequestBody LogoutRequest request)
            throws ParseException, JOSEException {
        authenticationService.logout(request);
        return APIResponse.<Void>builder()
                .build();
    }

    @PostMapping("/refresh")
    APIResponse<AuthenticationResponse> refreshToken(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        AuthenticationResponse res = authenticationService.refreshToken(request);
        return APIResponse.<AuthenticationResponse>builder()
                .result(res)
                .build();
    }
}
