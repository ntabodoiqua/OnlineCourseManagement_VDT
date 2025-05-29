package com.ntabodoiqua.online_course_management.dto.response.enrollment;

import com.ntabodoiqua.online_course_management.dto.response.course.CourseResponse;
import com.ntabodoiqua.online_course_management.dto.response.user.UserResponse;
import com.ntabodoiqua.online_course_management.enums.EnrollmentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrollmentResponse {
    String id;
    CourseResponse course;
    UserResponse student;

    LocalDate enrollmentDate;
    boolean isCompleted;
    LocalDate completionDate;
    double progress;
    EnrollmentStatus approvalStatus; // PENDING, APPROVED, REJECTED
}
