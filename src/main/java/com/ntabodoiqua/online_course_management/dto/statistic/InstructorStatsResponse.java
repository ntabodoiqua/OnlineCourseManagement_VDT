package com.ntabodoiqua.online_course_management.dto.statistic;

import com.ntabodoiqua.online_course_management.dto.response.course.PopularCourseResponse;
import com.ntabodoiqua.online_course_management.dto.response.enrollment.EnrollmentResponse;
import com.ntabodoiqua.online_course_management.dto.response.review.CourseReviewResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorStatsResponse {
    private long totalCourses;
    private long totalLessons;
    private long totalStudents;
    private List<EnrollmentResponse> recentEnrollments;
    private List<CourseReviewResponse> recentReviews;
    private List<PopularCourseResponse> popularCourses;
} 