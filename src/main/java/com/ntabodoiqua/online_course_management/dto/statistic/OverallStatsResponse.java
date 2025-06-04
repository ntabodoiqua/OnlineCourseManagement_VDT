package com.ntabodoiqua.online_course_management.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OverallStatsResponse {
    private Long totalUsers;
    private List<MonthlyNewUsersStat> newUsersByMonth; // For the column chart
    private List<RoleDistributionStat> roleDistribution; // For the role proportion pie chart
    private UserStatusStat userStatusDistribution; // For the enable/disable pie chart
} 