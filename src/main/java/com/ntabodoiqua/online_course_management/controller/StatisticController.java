package com.ntabodoiqua.online_course_management.controller;

import com.ntabodoiqua.online_course_management.dto.request.ApiResponse;
import com.ntabodoiqua.online_course_management.dto.statistic.InstructorStatsResponse;
import com.ntabodoiqua.online_course_management.dto.statistic.OverallStatsResponse;
import com.ntabodoiqua.online_course_management.service.InstructorStatisticService;
import com.ntabodoiqua.online_course_management.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/statistics") // Base path for statistic-related endpoints
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;
    private final InstructorStatisticService instructorStatisticService;

    /**
     * Endpoint to retrieve an overview of user statistics.
     * This includes total users, new users by month (broken down by role),
     * role distribution, and user status (enabled/disabled) distribution.
     * Access is restricted to users with 'ADMIN' role.
     * @return ResponseEntity containing ApiResponse with OverallStatsResponse DTO.
     */
    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN')") // Secure this endpoint for ADMIN users only
    public ResponseEntity<ApiResponse<OverallStatsResponse>> getOverallStatistics() {
        OverallStatsResponse stats = statisticService.getOverallStats();
        ApiResponse<OverallStatsResponse> response = ApiResponse.<OverallStatsResponse>builder()
                .message("Successfully retrieved overall statistics.")
                .result(stats)
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to retrieve instructor statistics.
     * This includes total courses, lessons, students, revenue, recent enrollments,
     * recent reviews, and popular courses for the authenticated instructor.
     * Access is restricted to users with 'INSTRUCTOR' role.
     * @return ResponseEntity containing ApiResponse with InstructorStatsResponse DTO.
     */
    @GetMapping("/instructor")
    @PreAuthorize("hasRole('INSTRUCTOR')") // Secure this endpoint for INSTRUCTOR users only
    public ResponseEntity<ApiResponse<InstructorStatsResponse>> getInstructorStatistics() {
        InstructorStatsResponse stats = instructorStatisticService.getInstructorStatistics();
        ApiResponse<InstructorStatsResponse> response = ApiResponse.<InstructorStatsResponse>builder()
                .message("Successfully retrieved instructor statistics.")
                .result(stats)
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to retrieve instructor statistics filtered by date range.
     * This provides the same statistics as the main endpoint but filtered by the specified date range.
     * Access is restricted to users with 'INSTRUCTOR' role.
     * @param startDate The start date for filtering (optional)
     * @param endDate The end date for filtering (optional)
     * @return ResponseEntity containing ApiResponse with InstructorStatsResponse DTO.
     */
    @GetMapping("/instructor/filtered")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse<InstructorStatsResponse>> getInstructorStatisticsFiltered(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        InstructorStatsResponse stats = instructorStatisticService.getInstructorStatisticsFiltered(startDate, endDate);
        ApiResponse<InstructorStatsResponse> response = ApiResponse.<InstructorStatsResponse>builder()
                .message("Successfully retrieved filtered instructor statistics.")
                .result(stats)
                .build();
        return ResponseEntity.ok(response);
    }
} 