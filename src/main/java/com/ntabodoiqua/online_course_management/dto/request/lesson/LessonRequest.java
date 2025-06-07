package com.ntabodoiqua.online_course_management.dto.request.lesson;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonRequest {
    String title;
    String description;
    String content;
}
