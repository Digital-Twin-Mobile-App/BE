package com.project.dadn.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class GoogleController {
    @GetMapping
    public String welcome() {
        return "Welcome to Google Login";
    }

    @GetMapping("/usergg")
    public Principal getUser(Principal principal) {
        if (principal != null) {
            System.out.println("Username: " + principal.getName());
        }
        return principal;
    }
}
