package com.devteria.identityservice.dto.response.enrollment;

import com.devteria.identityservice.dto.response.lesson.LessonResponse;
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
    LessonResponse lesson;
    boolean isCompleted;
    LocalDate completionDate;
}
