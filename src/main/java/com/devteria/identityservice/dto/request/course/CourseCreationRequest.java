package com.devteria.identityservice.dto.request.course;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseCreationRequest {
    String title;
    String description;
    String detailedDescription;
    String thumbnailUrl;
    LocalDate startDate;
    LocalDate endDate;
    String instructorId;
    String categoryName;
}
