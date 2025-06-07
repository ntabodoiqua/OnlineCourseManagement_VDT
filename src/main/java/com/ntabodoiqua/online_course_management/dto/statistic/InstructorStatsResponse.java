package com.ntabodoiqua.online_course_management.dto.statistic;

import com.ntabodoiqua.online_course_management.dto.response.course.PopularCourseResponse;
import com.ntabodoiqua.online_course_management.dto.response.enrollment.EnrollmentResponse;
import com.ntabodoiqua.online_course_management.dto.response.review.CourseReviewResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorStatsResponse {
    // Basic statistics
    private long totalCourses;
    private long totalLessons;
    private long totalStudents;
    
    // Advanced statistics
    private double completionRate;
    private double averageRating;
    private double approvalRate;
    private long activeStudents;
    
    // Chart data
    private List<MonthlyEnrollmentData> enrollmentTrends;
    private List<CategoryDistributionData> categoryDistribution;
    private List<MonthlyRatingData> ratingTrends;
    
    // Existing data
    private List<EnrollmentResponse> recentEnrollments;
    private List<CourseReviewResponse> recentReviews;
    private List<PopularCourseResponse> popularCourses;
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlyEnrollmentData {
        private String month;
        private long enrollments;
    }
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryDistributionData {
        private String name;
        private long value;
        private String color;
    }
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlyRatingData {
        private String month;
        private double rating;
    }
} 