package com.devteria.identityservice.dto.request.enrollment;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrollmentRequest {
    String studentId;
    String courseId;
}
