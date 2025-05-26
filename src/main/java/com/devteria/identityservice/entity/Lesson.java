package com.devteria.identityservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String title;

    // Nội dung chi tiết
    @Lob
    String content;
    // Đường dẫn video hoặc tài liệu đính kèm
    String videoUrl;
    String attachmentUrl; // Đường dẫn tài liệu đính kèm

    // Thứ tự hiển thị của bài học trong khóa học
    int orderIndex;

    // Ngày bắt đầu và kết thúc của khóa học
    LocalDate startDate;
    LocalDate endDate;

    @ManyToOne
    Course course; // Khóa học mà bài học thuộc về
}
