package com.ntabodoiqua.online_course_management.dto.request.lesson;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonCreationRequest {
    String title;
    String content;
    String videoUrl;
    String attachmentUrl;
    int orderIndex;
    LocalDate startDate;
    LocalDate endDate;
    String courseId;
}
