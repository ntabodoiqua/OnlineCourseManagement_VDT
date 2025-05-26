package com.devteria.identityservice.dto.request.lesson;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonUpdateRequest {
    String title;
    String content;
    String videoUrl;
    String attachmentUrl;
    Integer orderIndex;
    LocalDate startDate;
    LocalDate endDate;
}
