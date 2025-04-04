package com.project.dadn.dtos.requests;


import com.project.dadn.utlls.MessageKeys;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 3,message = MessageKeys.INVALID_USERNAME)
    String username;

    @Size(min = 8, message = MessageKeys.INVALID_PASSWORD)
    String password;
    String firstName;
    String lastName;
    LocalDate dob;
}