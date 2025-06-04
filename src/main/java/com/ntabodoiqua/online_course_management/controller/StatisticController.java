package com.ntabodoiqua.online_course_management.controller;

import com.ntabodoiqua.online_course_management.dto.request.ApiResponse;
import com.ntabodoiqua.online_course_management.dto.statistic.OverallStatsResponse;
import com.ntabodoiqua.online_course_management.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics") // Base path for statistic-related endpoints
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

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
} 