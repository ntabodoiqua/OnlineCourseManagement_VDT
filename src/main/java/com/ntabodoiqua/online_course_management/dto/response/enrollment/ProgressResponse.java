package com.ntabodoiqua.online_course_management.dto.response.enrollment;

import com.ntabodoiqua.online_course_management.dto.response.lesson.LessonResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProgressResponse {
    String id;
    String lessonId;
    LessonResponse lesson;
    boolean isCompleted;
    LocalDate completionDate;
}
