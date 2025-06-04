package com.ntabodoiqua.online_course_management.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDistributionStat {
    private String roleName;
    private long count;
    private double percentage;
} 