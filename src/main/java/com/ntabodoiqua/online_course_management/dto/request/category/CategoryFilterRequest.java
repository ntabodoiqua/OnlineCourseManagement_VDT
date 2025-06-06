package com.ntabodoiqua.online_course_management.dto.request.category;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryFilterRequest {
    String name;
    String createdByUsername;
} 