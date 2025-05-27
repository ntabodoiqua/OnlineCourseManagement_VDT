package com.ntabodoiqua.online_course_management.dto.request.review;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseReviewRequest {
    String studentId;
    String courseId;
    int rating;
    String comment;
}
