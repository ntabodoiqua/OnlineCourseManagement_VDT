package com.ntabodoiqua.online_course_management.service;

import com.ntabodoiqua.online_course_management.dto.response.course.PopularCourseResponse;
import com.ntabodoiqua.online_course_management.dto.response.enrollment.EnrollmentResponse;
import com.ntabodoiqua.online_course_management.dto.response.review.CourseReviewResponse;
import com.ntabodoiqua.online_course_management.dto.statistic.InstructorStatsResponse;
import com.ntabodoiqua.online_course_management.entity.Course;
import com.ntabodoiqua.online_course_management.entity.CourseReview;
import com.ntabodoiqua.online_course_management.entity.Enrollment;
import com.ntabodoiqua.online_course_management.entity.User;
import com.ntabodoiqua.online_course_management.exception.AppException;
import com.ntabodoiqua.online_course_management.exception.ErrorCode;
import com.ntabodoiqua.online_course_management.mapper.CourseMapper;
import com.ntabodoiqua.online_course_management.mapper.EnrollmentMapper;
import com.ntabodoiqua.online_course_management.mapper.UserMapper;
import com.ntabodoiqua.online_course_management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstructorStatisticService {

    private final CourseRepository courseRepository;
    private final CourseLessonRepository courseLessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseReviewRepository courseReviewRepository;
    private final UserRepository userRepository;
    private final CourseMapper courseMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final UserMapper userMapper;

    @PreAuthorize("hasRole('INSTRUCTOR')")
    public InstructorStatsResponse getInstructorStatistics() {
        return getInstructorStatisticsFiltered(null, null);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    public InstructorStatsResponse getInstructorStatisticsFiltered(LocalDate startDate, LocalDate endDate) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User instructor = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String instructorId = instructor.getId();

        // Basic statistics
        long totalCourses = courseRepository.findByInstructorId(instructorId).size();
        long totalLessons = courseLessonRepository.countByInstructorId(instructorId);
        long totalStudents = enrollmentRepository.countDistinctStudentsByInstructorId(instructorId);

        // Advanced statistics
        long totalEnrollments = enrollmentRepository.countTotalEnrollmentsByInstructorId(instructorId);
        long completedEnrollments = enrollmentRepository.countCompletedEnrollmentsByInstructorId(instructorId);
        long approvedEnrollments = enrollmentRepository.countApprovedEnrollmentsByInstructorId(instructorId);
        long activeStudents = enrollmentRepository.countActiveStudentsByInstructorId(instructorId);

        double completionRate = totalEnrollments > 0 ? (double) completedEnrollments / totalEnrollments * 100 : 0.0;
        double approvalRate = totalEnrollments > 0 ? (double) approvedEnrollments / totalEnrollments * 100 : 100.0;

        // Average rating
        Double avgRating = courseReviewRepository.findAverageRatingByInstructorId(instructorId);
        double averageRating = avgRating != null ? avgRating : 0.0;

        // Chart data
        List<InstructorStatsResponse.MonthlyEnrollmentData> enrollmentTrends = getEnrollmentTrends(instructorId);
        List<InstructorStatsResponse.CategoryDistributionData> categoryDistribution = getCategoryDistribution(instructorId);
        List<InstructorStatsResponse.MonthlyRatingData> ratingTrends = getRatingTrends(instructorId);

        // Recent data (existing functionality)
        Pageable enrollmentPageable = PageRequest.of(0, 5);
        Page<Enrollment> recentEnrollmentsPage = enrollmentRepository.findByInstructorIdOrderByEnrollmentDateDesc(instructorId, enrollmentPageable);
        List<EnrollmentResponse> recentEnrollments = recentEnrollmentsPage.getContent().stream()
                .map(enrollmentMapper::toEnrollmentResponse)
                .collect(Collectors.toList());

        Pageable reviewPageable = PageRequest.of(0, 5);
        Page<CourseReview> recentReviewsPage = courseReviewRepository.findByInstructorIdOrderByReviewDateDesc(instructorId, reviewPageable);
        List<CourseReviewResponse> recentReviews = recentReviewsPage.getContent().stream()
                .map(this::toReviewResponse)
                .collect(Collectors.toList());

        Pageable popularPageable = PageRequest.of(0, 5);
        Page<Object[]> popularCourseData = enrollmentRepository.findPopularCourseIdsByInstructorId(instructorId, popularPageable);
        List<PopularCourseResponse> popularCourses = popularCourseData.getContent().stream()
                .map(result -> {
                    String courseId = (String) result[0];
                    Long enrollmentCount = (Long) result[1];
                    Course course = courseRepository.findById(courseId).orElse(null);
                    if (course != null) {
                        return PopularCourseResponse.builder()
                                .course(courseMapper.toCourseResponse(course))
                                .enrollmentCount(enrollmentCount)
                                .build();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return InstructorStatsResponse.builder()
                .totalCourses(totalCourses)
                .totalLessons(totalLessons)
                .totalStudents(totalStudents)
                .completionRate(Math.round(completionRate * 10.0) / 10.0) // Round to 1 decimal place
                .averageRating(Math.round(averageRating * 10.0) / 10.0)
                .approvalRate(Math.round(approvalRate * 10.0) / 10.0)
                .activeStudents(activeStudents)
                .enrollmentTrends(enrollmentTrends)
                .categoryDistribution(categoryDistribution)
                .ratingTrends(ratingTrends)
                .recentEnrollments(recentEnrollments)
                .recentReviews(recentReviews)
                .popularCourses(popularCourses)
                .build();
    }

    private List<InstructorStatsResponse.MonthlyEnrollmentData> getEnrollmentTrends(String instructorId) {
        List<Object[]> trends = enrollmentRepository.findMonthlyEnrollmentTrendsByInstructorId(instructorId);
        List<InstructorStatsResponse.MonthlyEnrollmentData> result = new ArrayList<>();
        
        // Convert month format and create data
        for (Object[] trend : trends) {
            String monthYear = (String) trend[0]; // Format: 2024-12
            Long enrollments = ((Number) trend[1]).longValue();
            
            // Convert to display format (T1, T2, etc.)
            String displayMonth = convertToDisplayMonth(monthYear);
            
            result.add(InstructorStatsResponse.MonthlyEnrollmentData.builder()
                    .month(displayMonth)
                    .enrollments(enrollments)
                    .build());
        }
        
        // Ensure we have data for the last 6 months even if no enrollments
        while (result.size() < 6) {
            result.add(InstructorStatsResponse.MonthlyEnrollmentData.builder()
                    .month("T" + (result.size() + 1))
                    .enrollments(0L)
                    .build());
        }
        
        return result.stream().limit(6).collect(Collectors.toList());
    }

    private List<InstructorStatsResponse.CategoryDistributionData> getCategoryDistribution(String instructorId) {
        // Get all courses by instructor
        List<Course> instructorCourses = courseRepository.findByInstructorId(instructorId);
        
        // Group by category name and count
        Map<String, Long> categoryCount = instructorCourses.stream()
                .collect(Collectors.groupingBy(
                    course -> course.getCategory() != null ? course.getCategory().getName() : "Không có danh mục",
                    Collectors.counting()
                ));
        
        List<InstructorStatsResponse.CategoryDistributionData> result = new ArrayList<>();
        String[] colors = {"#8884d8", "#82ca9d", "#ffc658", "#ff7300", "#8dd1e1", "#d084d0"};
        
        // Sort by count descending and create result list
        List<Map.Entry<String, Long>> sortedEntries = categoryCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toList());
        
        for (int i = 0; i < sortedEntries.size(); i++) {
            Map.Entry<String, Long> entry = sortedEntries.get(i);
            result.add(InstructorStatsResponse.CategoryDistributionData.builder()
                    .name(entry.getKey())
                    .value(entry.getValue())
                    .color(colors[i % colors.length])
                    .build());
        }
        
        return result;
    }

    private List<InstructorStatsResponse.MonthlyRatingData> getRatingTrends(String instructorId) {
        List<Object[]> trends = courseReviewRepository.findMonthlyRatingTrendsByInstructorId(instructorId);
        List<InstructorStatsResponse.MonthlyRatingData> result = new ArrayList<>();
        
        for (Object[] trend : trends) {
            String monthYear = (String) trend[0]; // Format: 2024-12
            Double rating = ((Number) trend[1]).doubleValue();
            
            // Convert to display format (T1, T2, etc.)
            String displayMonth = convertToDisplayMonth(monthYear);
            
            result.add(InstructorStatsResponse.MonthlyRatingData.builder()
                    .month(displayMonth)
                    .rating(Math.round(rating * 10.0) / 10.0) // Round to 1 decimal place
                    .build());
        }
        
        // Ensure we have data for the last 6 months even if no ratings
        while (result.size() < 6) {
            result.add(InstructorStatsResponse.MonthlyRatingData.builder()
                    .month("T" + (result.size() + 1))
                    .rating(0.0)
                    .build());
        }
        
        return result.stream().limit(6).collect(Collectors.toList());
    }

    private String convertToDisplayMonth(String monthYear) {
        // Convert 2024-12 to T12, 2024-01 to T1, etc.
        if (monthYear != null && monthYear.contains("-")) {
            String[] parts = monthYear.split("-");
            if (parts.length == 2) {
                int month = Integer.parseInt(parts[1]);
                return "T" + month;
            }
        }
        return "T1"; // Default fallback
    }

    private CourseReviewResponse toReviewResponse(CourseReview review) {
        return CourseReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .reviewDate(review.getReviewDate())
                .isApproved(review.isApproved())
                .isRejected(review.isRejected())
                .student(userMapper.toUserResponse(review.getStudent()))
                .course(courseMapper.toCourseResponse(review.getCourse()))
                .build();
    }
} 