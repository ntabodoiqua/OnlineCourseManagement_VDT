package com.ntabodoiqua.online_course_management.dto.request.enrollment;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProgressUpdateRequest {
    @Setter
    String enrollmentId;
    String lessonId;
    boolean completed;

}
