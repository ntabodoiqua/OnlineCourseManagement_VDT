package com.ntabodoiqua.online_course_management.dto.response.lesson;

import com.ntabodoiqua.online_course_management.dto.response.user.UserResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonResponse {
    String id;
    String title;
    String content;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    UserResponse createdBy;
    Integer courseCount;
}
