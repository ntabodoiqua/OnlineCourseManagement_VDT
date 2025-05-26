package com.devteria.identityservice.dto.response.course;

import com.devteria.identityservice.dto.response.user.UserResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseResponse {
    String id;
    String title;
    String description;
    String detailedDescription;
    String thumbnailUrl;
    boolean isActive;
    int totalLessons;
    LocalDate startDate;
    LocalDate endDate;

    // Thông tin về giảng viên
    UserResponse instructor;
}
