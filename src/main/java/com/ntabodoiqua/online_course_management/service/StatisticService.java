package com.ntabodoiqua.online_course_management.service;

import com.ntabodoiqua.online_course_management.dto.statistic.MonthlyNewUsersStat;
import com.ntabodoiqua.online_course_management.dto.statistic.OverallStatsResponse;
import com.ntabodoiqua.online_course_management.dto.statistic.RoleDistributionStat;
import com.ntabodoiqua.online_course_management.dto.statistic.UserStatusStat;
import com.ntabodoiqua.online_course_management.enums.Role;
import com.ntabodoiqua.online_course_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
// Consider adding specific role names as constants if they are frequently used strings, though enum.name() is generally fine.
// For example:
// private static final String ROLE_ADMIN = Role.ADMIN.name();
// private static final String ROLE_INSTRUCTOR = Role.INSTRUCTOR.name();
// private static final String ROLE_STUDENT = Role.STUDENT.name();


@Service
@RequiredArgsConstructor
public class StatisticService {

    private final UserRepository userRepository;
    // Defines how many past months of new user data to retrieve for the chart
    private static final int MONTHS_FOR_NEW_USER_STATS = 12;

    public OverallStatsResponse getOverallStats() {
        long totalUsers = userRepository.count();
        List<MonthlyNewUsersStat> newUsersByMonth = getNewUsersByMonthStats(MONTHS_FOR_NEW_USER_STATS);
        List<RoleDistributionStat> roleDistribution = getRoleDistributionStats(totalUsers);
        UserStatusStat userStatusDistribution = getUserStatusDistributionStats(totalUsers);

        return OverallStatsResponse.builder()
                .totalUsers(totalUsers)
                .newUsersByMonth(newUsersByMonth)
                .roleDistribution(roleDistribution)
                .userStatusDistribution(userStatusDistribution)
                .build();
    }

    private List<MonthlyNewUsersStat> getNewUsersByMonthStats(int numberOfMonths) {
        List<MonthlyNewUsersStat> stats = new ArrayList<>();
        YearMonth currentYearMonth = YearMonth.now();

        for (int i = 0; i < numberOfMonths; i++) {
            YearMonth targetMonth = currentYearMonth.minusMonths(i);
            LocalDateTime startDate = targetMonth.atDay(1).atStartOfDay();
            // Ensure endDate is strictly less than the start of the next month
            LocalDateTime endDate = targetMonth.plusMonths(1).atDay(1).atStartOfDay();

            long adminCount = userRepository.countByRoleNameAndCreatedAtBetween(Role.ADMIN.name(), startDate, endDate);
            long instructorCount = userRepository.countByRoleNameAndCreatedAtBetween(Role.INSTRUCTOR.name(), startDate, endDate);
            long studentCount = userRepository.countByRoleNameAndCreatedAtBetween(Role.STUDENT.name(), startDate, endDate);

            stats.add(MonthlyNewUsersStat.builder()
                    .year(targetMonth.getYear())
                    .month(targetMonth.getMonthValue())
                    .newAdminCount(adminCount)
                    .newInstructorCount(instructorCount)
                    .newStudentCount(studentCount)
                    .build());
        }
        // The frontend might prefer recent months first, or chronological.
        // Current order is: Current Month, Month-1, Month-2 ...
        // If chronological (oldest first) is needed:
        Collections.reverse(stats);
        return stats;
    }

    private List<RoleDistributionStat> getRoleDistributionStats(long totalUsers) {
        List<RoleDistributionStat> distribution = new ArrayList<>();

        // Handle case where there are no users to avoid division by zero
        if (totalUsers == 0) {
            distribution.add(new RoleDistributionStat(Role.ADMIN.name(), 0, 0.0));
            distribution.add(new RoleDistributionStat(Role.INSTRUCTOR.name(), 0, 0.0));
            distribution.add(new RoleDistributionStat(Role.STUDENT.name(), 0, 0.0));
            return distribution;
        }

        long adminCount = userRepository.countByRoleName(Role.ADMIN.name());
        long instructorCount = userRepository.countByRoleName(Role.INSTRUCTOR.name());
        long studentCount = userRepository.countByRoleName(Role.STUDENT.name());

        distribution.add(new RoleDistributionStat(Role.ADMIN.name(), adminCount, calculatePercentage(adminCount, totalUsers)));
        distribution.add(new RoleDistributionStat(Role.INSTRUCTOR.name(), instructorCount, calculatePercentage(instructorCount, totalUsers)));
        distribution.add(new RoleDistributionStat(Role.STUDENT.name(), studentCount, calculatePercentage(studentCount, totalUsers)));

        return distribution;
    }

    private UserStatusStat getUserStatusDistributionStats(long totalUsers) {
        // Handle case where there are no users to avoid division by zero
        if (totalUsers == 0) {
            return UserStatusStat.builder()
                .enabledCount(0)
                .enabledPercentage(0.0)
                .disabledCount(0)
                .disabledPercentage(0.0)
                .build();
        }

        long enabledCount = userRepository.countByEnabled(true);
        // Calculate disabledCount based on totalUsers and enabledCount to ensure consistency
        long disabledCount = totalUsers - enabledCount;


        return UserStatusStat.builder()
                .enabledCount(enabledCount)
                .enabledPercentage(calculatePercentage(enabledCount, totalUsers))
                .disabledCount(disabledCount)
                .disabledPercentage(calculatePercentage(disabledCount, totalUsers))
                .build();
    }

    // Helper method to calculate percentage and handle division by zero if total is somehow zero despite earlier checks.
    private double calculatePercentage(long count, long total) {
        if (total == 0) {
            return 0.0;
        }
        return (double) count * 100.0 / total;
    }
} 