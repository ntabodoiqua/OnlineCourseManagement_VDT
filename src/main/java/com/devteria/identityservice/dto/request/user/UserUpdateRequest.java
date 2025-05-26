package com.devteria.identityservice.dto.request.user;

import com.devteria.identityservice.validator.DobConstraint;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String password;
    String firstName;
    String lastName;
    // Custom Validation: Giới hạn tuổi nhập vào
    @DobConstraint(min = 2, message = "INVALID_DOB")
    LocalDate dob;
    List<String> roles;
}
