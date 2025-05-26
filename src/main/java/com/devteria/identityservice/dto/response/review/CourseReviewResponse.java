package com.devteria.identityservice.dto.response.review;

import com.devteria.identityservice.dto.response.user.UserResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseReviewResponse {
    String id;
    int rating;
    String comment;
    LocalDate reviewDate;
    boolean isApproved;

    UserResponse student;
}
