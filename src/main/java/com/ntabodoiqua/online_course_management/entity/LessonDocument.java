package com.ntabodoiqua.online_course_management.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class LessonDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false)
    String title; // Tiêu đề tài liệu

    String description; // Mô tả tài liệu

    @Column(nullable = false)
    String fileName; // Tên file đã upload

    @Column(nullable = false)
    String originalFileName; // Tên file gốc

    @Column(nullable = false)
    String contentType; // Loại file (pdf, doc, ppt, ...)

    long fileSize; // Kích thước file (bytes)

    LocalDateTime uploadedAt; // Thời gian upload

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    Lesson lesson; // Bài học chứa tài liệu này

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    User uploadedBy; // Người upload
} 