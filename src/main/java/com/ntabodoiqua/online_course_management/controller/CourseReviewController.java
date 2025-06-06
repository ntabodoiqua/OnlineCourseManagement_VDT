package com.ntabodoiqua.online_course_management.controller;

import com.ntabodoiqua.online_course_management.dto.request.ApiResponse;
import com.ntabodoiqua.online_course_management.dto.request.review.CourseReviewRequest;
import com.ntabodoiqua.online_course_management.dto.response.review.CourseReviewResponse;
import com.ntabodoiqua.online_course_management.service.CourseReviewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course-reviews")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseReviewController {
    CourseReviewService courseReviewService;

    @PostMapping("/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<CourseReviewResponse> createReview(@RequestBody CourseReviewRequest request, @PathVariable String courseId) {
        return ApiResponse.<CourseReviewResponse>builder()
                .result(courseReviewService.createReview(request, courseId))
                .build();
    }

    // Lấy danh sách đánh giá của khóa học
    @GetMapping("/{courseId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ApiResponse<?> getReviewsByCourseId(@PathVariable String courseId, Pageable pageable) {
        return ApiResponse.<Object>builder()
                .result(courseReviewService.getReviewsByCourse(courseId, pageable))
                .build();
    }

    // Lấy danh sách đánh giá đã được phê duyệt cho admin
    @GetMapping("/approved/{courseId}")
    public ApiResponse<?> getApprovedReviewsByCourseId(@PathVariable String courseId, Pageable pageable) {
        return ApiResponse.<Object>builder()
                .result(courseReviewService.getApprovedReviewsByCourseForAdmin(courseId, pageable))
                .build();
    }

    // Lấy danh sách đánh giá chưa được phê duyệt
    @GetMapping("/unapproved/{courseId}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ApiResponse<?> getUnapprovedReviewsByCourseId(@PathVariable String courseId, Pageable pageable) {
        return ApiResponse.<Object>builder()
                .result(courseReviewService.getPendingReviewsByCourse(courseId, pageable))
                .build();
    }

    // Phê duyệt đánh giá khóa học
    @PutMapping("/approve/{reviewId}")
    public ApiResponse<?> approveReview(@PathVariable String reviewId) {

        return ApiResponse.<Object>builder()
                .result(courseReviewService.approveReview(reviewId))
                .message("Review approved successfully")
                .build();
    }

    // Từ chối đánh giá khóa học
    @PutMapping("/reject/{reviewId}")
    public ApiResponse<?> rejectReview(@PathVariable String reviewId) {
        return ApiResponse.<Object>builder()
                .result(courseReviewService.rejectReview(reviewId))
                .message("Review rejected successfully")
                .build();
    }
} 