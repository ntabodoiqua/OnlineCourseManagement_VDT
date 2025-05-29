package com.ntabodoiqua.online_course_management.dto.request.course;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseLessonRequest {
    String lessonId;
    Integer orderIndex; // Cho phép null để tự động gán cuối danh sách
    Boolean isVisible;
    String prerequisiteCourseLessonId; // null nếu không có điều kiện unlock
}
