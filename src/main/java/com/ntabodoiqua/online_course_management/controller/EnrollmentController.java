package com.ntabodoiqua.online_course_management.controller;

import com.ntabodoiqua.online_course_management.dto.request.ApiResponse;
import com.ntabodoiqua.online_course_management.dto.response.enrollment.EnrollmentResponse;
import com.ntabodoiqua.online_course_management.service.CourseService;
import com.ntabodoiqua.online_course_management.service.EnrollmentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EnrollmentController {

    EnrollmentService enrollmentService;

    // Đăng ký khóa học
    @PostMapping("/courses/{courseId}")
    public ApiResponse<String> enroll(@PathVariable String courseId) {
        return ApiResponse.<String>builder()
                .result(enrollmentService.enroll(courseId))
                .build();
    }

    // Hủy đăng ký khóa học
    @DeleteMapping("/courses/{courseId}")
    public ApiResponse<String> cancelEnrollment(@PathVariable String courseId) {
        return ApiResponse.<String>builder()
                .result(enrollmentService.cancelEnrollment(courseId))
                .build();
    }

    // Lấy danh sách khóa học đã đăng ký
    @GetMapping("/my")
    public ApiResponse<Page<EnrollmentResponse>> getMyEnrollments(Pageable pageable) {
        return ApiResponse.<Page<EnrollmentResponse>>builder()
                .result(enrollmentService.getMyEnrollments(pageable))
                .build();
    }

    // Lấy danh sách Phê duyệt đăng ký
    @PutMapping("/{enrollmentId}/approve")
    public ApiResponse<String> approve(@PathVariable String enrollmentId) {
        return ApiResponse.<String>builder()
                .result(enrollmentService.approveEnrollment(enrollmentId))
                .build();
    }

    // Instructor/Admin: Từ chối đăng ký
    @PutMapping("/{enrollmentId}/reject")
    public ApiResponse<String> reject(@PathVariable String enrollmentId) {
        return ApiResponse.<String>builder()
                .result(enrollmentService.rejectEnrollment(enrollmentId))
                .build();
    }

    // Instructor/Admin: Xem danh sách học viên chờ duyệt theo khóa học (phân trang)
    @GetMapping("/pending/course/{courseId}")
    public ApiResponse<Page<EnrollmentResponse>> getPendingEnrollments(@PathVariable String courseId, Pageable pageable) {
        return ApiResponse.<Page<EnrollmentResponse>>builder()
                .result(enrollmentService.getPendingEnrollmentsByCourse(courseId, pageable))
                .build();
    }
}
