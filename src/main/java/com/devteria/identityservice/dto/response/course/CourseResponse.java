package com.devteria.identityservice.dto.response.course;

import com.devteria.identityservice.dto.response.user.UserResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    // Thông tin về giảng viên
    UserResponse instructor;
}
