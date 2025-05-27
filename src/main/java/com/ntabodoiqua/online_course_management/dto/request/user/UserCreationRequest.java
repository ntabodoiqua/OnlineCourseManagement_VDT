package com.ntabodoiqua.online_course_management.dto.request.user;

import com.ntabodoiqua.online_course_management.enums.Gender;
import com.ntabodoiqua.online_course_management.validator.DobConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @NotBlank(message = "USERNAME_REQUIRED")
    @Size(min = 3,message = "USERNAME_INVALID")
    String username;

    @NotBlank(message = "PASSWORD_REQUIRED")
    @Size(min = 8, message = "INVALID_PASSWORD")
    String password;

    String firstName;
    String lastName;

    // Ràng buộc độ tuổi tối thiểu
    @DobConstraint(min = 5, message = "INVALID_DOB")
    LocalDate dob;

    // Ràng buộc email
    @Email(message = "INVALID_EMAIL")
    String email;

    @Pattern(regexp = "^(0[0-9]{9})$", message = "INVALID_PHONE")
    String phone;
    String avatarUrl;
    String bio;

    Gender gender;

}
