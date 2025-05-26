package com.devteria.identityservice.dto.request.course;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseUpdateRequest {
    String title;
    String description;
    String detailedDescription;
    String thumbnailUrl;
    LocalDate startDate;
    LocalDate endDate;
    // Chỉnh sửa trạng thái khóa học
    Boolean isActive;
}
