package com.ntabodoiqua.online_course_management.dto.request.lesson;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonUpdateRequest {
    String title;
    String description;
    String content;
    Integer orderIndex;
    LocalDate startDate;
    LocalDate endDate;
}
