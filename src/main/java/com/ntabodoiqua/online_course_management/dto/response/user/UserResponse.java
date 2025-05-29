package com.ntabodoiqua.online_course_management.dto.response.user;

import com.ntabodoiqua.online_course_management.dto.response.role.RoleResponse;
import com.ntabodoiqua.online_course_management.enums.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String username;
    String firstName;
    String lastName;
    LocalDate dob;
    String avatarUrl;
    String email;
    String phone;
    Gender gender;
    LocalDateTime createdAt;
    Set<RoleResponse> roles;
    boolean enabled;
}
