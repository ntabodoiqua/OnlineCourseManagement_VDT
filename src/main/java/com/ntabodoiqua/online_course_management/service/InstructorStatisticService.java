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

import java.util.List;
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
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User instructor = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String instructorId = instructor.getId();

        // Lấy tổng số khóa học của instructor
        long totalCourses = courseRepository.findByInstructorId(instructorId).size();

        // Lấy tổng số bài học của instructor
        long totalLessons = courseLessonRepository.countByInstructorId(instructorId);

        // Lấy tổng số học viên đã đăng ký khóa học của instructor
        long totalStudents = enrollmentRepository.countDistinctStudentsByInstructorId(instructorId);

        // Lấy danh sách đăng ký gần đây (5 đăng ký mới nhất)
        Pageable enrollmentPageable = PageRequest.of(0, 5);
        Page<Enrollment> recentEnrollmentsPage = enrollmentRepository.findByInstructorIdOrderByEnrollmentDateDesc(instructorId, enrollmentPageable);
        List<EnrollmentResponse> recentEnrollments = recentEnrollmentsPage.getContent().stream()
                .map(enrollmentMapper::toEnrollmentResponse)
                .collect(Collectors.toList());

        // Lấy danh sách đánh giá gần đây (5 đánh giá mới nhất)
        Pageable reviewPageable = PageRequest.of(0, 5);
        Page<CourseReview> recentReviewsPage = courseReviewRepository.findByInstructorIdOrderByReviewDateDesc(instructorId, reviewPageable);
        List<CourseReviewResponse> recentReviews = recentReviewsPage.getContent().stream()
                .map(this::toReviewResponse)
                .collect(Collectors.toList());

        // Lấy danh sách khóa học phổ biến nhất (5 khóa học có nhiều đăng ký nhất)
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
                .recentEnrollments(recentEnrollments)
                .recentReviews(recentReviews)
                .popularCourses(popularCourses)
                .build();
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