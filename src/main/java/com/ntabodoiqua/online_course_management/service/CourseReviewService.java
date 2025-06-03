package com.ntabodoiqua.online_course_management.service;

import com.ntabodoiqua.online_course_management.dto.request.review.CourseReviewRequest;
import com.ntabodoiqua.online_course_management.dto.response.review.CourseReviewResponse;
import com.ntabodoiqua.online_course_management.entity.Course;
import com.ntabodoiqua.online_course_management.entity.CourseReview;
import com.ntabodoiqua.online_course_management.entity.Enrollment;
import com.ntabodoiqua.online_course_management.entity.User;
import com.ntabodoiqua.online_course_management.exception.ErrorCode;
import com.ntabodoiqua.online_course_management.exception.AppException;
import com.ntabodoiqua.online_course_management.mapper.UserMapper;
import com.ntabodoiqua.online_course_management.repository.CourseRepository;
import com.ntabodoiqua.online_course_management.repository.CourseReviewRepository;
import com.ntabodoiqua.online_course_management.repository.EnrollmentRepository;
import com.ntabodoiqua.online_course_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class CourseReviewService {
    CourseReviewRepository courseReviewRepository;
    CourseRepository courseRepository;
    UserRepository userRepository;
    EnrollmentRepository enrollmentRepository;
    UserMapper userMapper;

    public CourseReviewResponse createReview(CourseReviewRequest request, String courseId) {
        // Kiểm tra tồn tại user, course, enrollment
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), course.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_EXISTED));
        if (!enrollment.isCompleted()) {
            throw new AppException(ErrorCode.CANNOT_REVIEW_UNCOMPLETED_COURSE);
        }
        if (courseReviewRepository.existsByStudentIdAndCourseId(student.getId(), course.getId())) {
            throw new AppException(ErrorCode.ALREADY_REVIEWED);
        }
        CourseReview review = CourseReview.builder()
                .student(student)
                .course(course)
                .rating(request.getRating())
                .comment(request.getComment())
                .reviewDate(LocalDate.now())
                .isApproved(false) // Chờ duyệt
                .build();
        courseReviewRepository.save(review);
        return toResponse(review, student);
    }

    private CourseReviewResponse toResponse(CourseReview review, User student) {
        return CourseReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .reviewDate(review.getReviewDate())
                .isApproved(review.isApproved())
                .student(userMapper.toUserResponse(student)) // Có thể map sang UserResponse nếu cần
                .build();
    }

    // Lấy tất cả đánh giá của khóa học
    @PreAuthorize("hasRole('STUDENT') or hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public List<CourseReviewResponse> getReviewsByCourse(String courseId) {
        // Kiểm tra tồn tại khóa học
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));

        // Lấy thông tin user hiện tại và role
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));
        boolean isInstructor = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("INSTRUCTOR"));
        boolean isCourseInstructor = course.getInstructor().getId().equals(currentUser.getId());

        // Lấy danh sách review dựa trên quyền
        List<CourseReview> reviews;
        if (isAdmin || (isInstructor && isCourseInstructor)) {
            // Admin và instructor của khóa học có thể xem tất cả review
            reviews = courseReviewRepository.findByCourseId(courseId);
        } else {
            // User thường chỉ xem được review đã được duyệt
            reviews = courseReviewRepository.findByCourseIdAndIsApprovedTrue(courseId);
        }

        return reviews.stream()
                .map(review -> toResponse(review, review.getStudent()))
                .collect(Collectors.toList());
    }
} 