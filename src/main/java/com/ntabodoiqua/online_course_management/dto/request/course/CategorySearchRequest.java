package com.ntabodoiqua.online_course_management.dto.request.course;


import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategorySearchRequest {
    String name;
    String description;
    String createdBy;
    LocalDate from;
    LocalDate to;
    int page = 0;
    int size = 10;
    String sortBy = "createdAt";
    String direction = "desc";
}
