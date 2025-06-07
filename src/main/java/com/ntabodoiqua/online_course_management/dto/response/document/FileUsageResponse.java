package com.ntabodoiqua.online_course_management.dto.response.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileUsageResponse {
    @JsonProperty("isUsed")
    boolean isUsed;
    String fileName;
    List<FileUsageDetail> usageDetails;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class FileUsageDetail {
        String type; // "course", "lesson", "course_thumbnail", "user_avatar"
        String id;
        String title;
        String description;
    }
} 