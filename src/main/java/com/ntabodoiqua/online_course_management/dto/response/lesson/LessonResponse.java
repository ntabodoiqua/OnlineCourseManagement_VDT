package com.ntabodoiqua.online_course_management.dto.response.lesson;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonResponse {
    String id;
    String title;
    String content;
    String videoUrl;
    String attachmentUrl;
    int orderIndex;
    LocalDate startDate;
    LocalDate endDate;
}
