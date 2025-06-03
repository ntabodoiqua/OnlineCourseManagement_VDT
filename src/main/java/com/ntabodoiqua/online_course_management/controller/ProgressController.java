package com.ntabodoiqua.online_course_management.controller;

import com.ntabodoiqua.online_course_management.dto.request.ApiResponse;
import com.ntabodoiqua.online_course_management.dto.request.enrollment.ProgressUpdateRequest;
import com.ntabodoiqua.online_course_management.dto.response.enrollment.ProgressResponse;
import com.ntabodoiqua.online_course_management.service.ProgressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enrollments/{enrollmentId}/progress")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProgressController {
    ProgressService progressService;

    @GetMapping
    @PreAuthorize("hasRole('STUDENT') or hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ApiResponse<List<ProgressResponse>> getProgress(@PathVariable String enrollmentId) {
        return ApiResponse.<List<ProgressResponse>>builder()
                .result(progressService.getProgressByEnrollment(enrollmentId))
                .build();
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<ProgressResponse> updateProgress(@PathVariable String enrollmentId, @RequestBody ProgressUpdateRequest request) {
        request.setEnrollmentId(enrollmentId);
        return ApiResponse.<ProgressResponse>builder()
                .result(progressService.updateProgress(request))
                .build();
    }
} 