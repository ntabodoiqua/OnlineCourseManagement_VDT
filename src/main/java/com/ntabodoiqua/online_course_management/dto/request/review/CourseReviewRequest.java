package com.ntabodoiqua.online_course_management.dto.request.review;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseReviewRequest {
    int rating;
    String comment;
}
