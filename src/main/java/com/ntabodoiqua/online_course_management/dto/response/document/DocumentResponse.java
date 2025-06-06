package com.ntabodoiqua.online_course_management.dto.response.document;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentResponse {
    String id;
    String title;
    String description;
    String fileName;
    String originalFileName;
    String contentType;
    long fileSize;
    LocalDateTime uploadedAt;
    String uploadedByUsername;
    String downloadUrl;
} 