package com.ntabodoiqua.online_course_management.dto.request.course;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseFilterRequest {
    String title;
    String instructorName;
    String category;
    Boolean isActive;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime createdFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime createdTo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDate startDateFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDate startDateTo;
}
