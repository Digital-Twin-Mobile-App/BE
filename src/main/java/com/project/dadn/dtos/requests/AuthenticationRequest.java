package com.project.dadn.dtos.requests;

import com.project.dadn.utlls.MessageKeys;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest {

    @Email(message = MessageKeys.INVALID_EMAIL)
    String email;

    @Size(min = 8, message = MessageKeys.INVALID_PASSWORD)
    String password;
}
