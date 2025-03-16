package com.project.dadn.dtos.requests;


import com.project.dadn.utlls.MessageKeys;
import com.project.dadn.validator.DobConstraint;
import com.project.dadn.validator.ValidEmail;
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
    @ValidEmail(message = MessageKeys.INVALID_EMAIL)
    String email;

    @Size(min = 8, message = MessageKeys.INVALID_PASSWORD)
    String password;
    String firstName;
    String lastName;

    @DobConstraint(min = 2, message = MessageKeys.INVALID_DOB)
    LocalDate dob;
}