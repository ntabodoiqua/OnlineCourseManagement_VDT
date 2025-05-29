package com.ntabodoiqua.online_course_management.dto.response.course;

import com.ntabodoiqua.online_course_management.dto.response.lesson.LessonResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseLessonResponse {
    String id;
    LessonResponse lesson;
    Integer orderIndex;
    Boolean isVisible;
    String prerequisiteCourseLessonId;
    String prerequisiteLessonTitle;
}
