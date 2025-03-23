package com.project.dadn.dtos.responses;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationResponse {
    String token;
    boolean authenticated;

    public static AuthenticationResponse buildAuthenticationResponse(String token, boolean authenticated) {
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(authenticated)
                .build();
    }
}

