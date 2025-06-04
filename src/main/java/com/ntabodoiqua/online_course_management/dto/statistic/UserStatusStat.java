package com.ntabodoiqua.online_course_management.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatusStat {
    private long enabledCount;
    private double enabledPercentage;
    private long disabledCount;
    private double disabledPercentage;
} 