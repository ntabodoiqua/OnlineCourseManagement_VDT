package com.ntabodoiqua.online_course_management.service;

import com.ntabodoiqua.online_course_management.dto.request.review.CourseReviewRequest;
import com.ntabodoiqua.online_course_management.dto.response.review.CourseReviewResponse;
import com.ntabodoiqua.online_course_management.entity.Course;
import com.ntabodoiqua.online_course_management.entity.CourseReview;
import com.ntabodoiqua.online_course_management.entity.Enrollment;
import com.ntabodoiqua.online_course_management.entity.User;
import com.ntabodoiqua.online_course_management.exception.ErrorCode;
import com.ntabodoiqua.online_course_management.exception.AppException;
import com.ntabodoiqua.online_course_management.repository.CourseRepository;
import com.ntabodoiqua.online_course_management.repository.CourseReviewRepository;
import com.ntabodoiqua.online_course_management.repository.EnrollmentRepository;
import com.ntabodoiqua.online_course_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class CourseReviewService {
    CourseReviewRepository courseReviewRepository;
    CourseRepository courseRepository;
    UserRepository userRepository;
    EnrollmentRepository enrollmentRepository;

    public CourseReviewResponse createReview(CourseReviewRequest request) {
        // Kiểm tra tồn tại user, course, enrollment
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Course course = courseRepository.findById(request.getCourseId())
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
        return toResponse(review);
    }

    private CourseReviewResponse toResponse(CourseReview review) {
        return CourseReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .reviewDate(review.getReviewDate())
                .isApproved(review.isApproved())
                .student(null) // Có thể map sang UserResponse nếu cần
                .build();
    }
} 