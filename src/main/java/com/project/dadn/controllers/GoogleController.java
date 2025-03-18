package com.project.dadn.controllers;

import com.project.dadn.dtos.responses.APIResponse;
import com.project.dadn.dtos.responses.AuthenticationResponse;
import com.project.dadn.services.GoogleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class GoogleController {

    private final GoogleService googleService;

    @GetMapping
    public String welcome() {
        return "heart";
    }

    @GetMapping("/google/user")
    public Principal getUser(Principal principal) {
        if (principal != null) {
            System.out.println("Username: " + principal.getName());
        }
        return principal;
    }

    @PostMapping("/login/oauth2/code/google")
    public APIResponse<AuthenticationResponse> login(OAuth2AuthenticationToken authenticationToken) {
        AuthenticationResponse res = googleService.authenticateWithGoogle(authenticationToken);
        return APIResponse.<AuthenticationResponse>builder()
                .result(res)
                .build();
    }
}
