package com.ntabodoiqua.online_course_management.dto.request.document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentUploadRequest {
    @NotBlank(message = "Tiêu đề tài liệu không được để trống")
    @Size(max = 255, message = "Tiêu đề tài liệu không được vượt quá 255 ký tự")
    String title;

    @Size(max = 1000, message = "Mô tả tài liệu không được vượt quá 1000 ký tự")
    String description;
} 