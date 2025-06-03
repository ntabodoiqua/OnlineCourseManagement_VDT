package com.ntabodoiqua.online_course_management.controller;

import com.ntabodoiqua.online_course_management.dto.request.ApiResponse;
import com.ntabodoiqua.online_course_management.dto.request.review.CourseReviewRequest;
import com.ntabodoiqua.online_course_management.dto.response.review.CourseReviewResponse;
import com.ntabodoiqua.online_course_management.service.CourseReviewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course-reviews")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseReviewController {
    CourseReviewService courseReviewService;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<CourseReviewResponse> createReview(@RequestBody CourseReviewRequest request) {
        return ApiResponse.<CourseReviewResponse>builder()
                .result(courseReviewService.createReview(request))
                .build();
    }
} 