package com.ntabodoiqua.online_course_management.dto.request.user;

import com.ntabodoiqua.online_course_management.validator.DobConstraint;
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
    String firstName;
    String lastName;
    // Custom Validation: Giới hạn tuổi nhập vào
    @DobConstraint(min = 2, message = "INVALID_DOB")
    LocalDate dob;
    String email;
    String phone;
    String bio;
    List<String> roles;
}
