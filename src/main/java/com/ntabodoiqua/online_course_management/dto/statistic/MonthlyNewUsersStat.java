package com.ntabodoiqua.online_course_management.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyNewUsersStat {
    private int year;
    private int month; // 1-12
    private long newAdminCount;
    private long newInstructorCount;
    private long newStudentCount;
} 