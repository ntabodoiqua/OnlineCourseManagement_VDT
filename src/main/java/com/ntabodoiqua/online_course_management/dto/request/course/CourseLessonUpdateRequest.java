package com.ntabodoiqua.online_course_management.dto.request.course;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseLessonUpdateRequest {
    Integer orderIndex;
    Boolean isVisible;
    String prerequisiteCourseLessonId;
}
