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
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String title;
    // Mô tả ngắn gọn
    String description;
    // Mô tả chi tiết
    @Lob
    String detailedDescription;
    // Đường dẫn ảnh đại diện
    String thumbnailUrl;

    boolean isActive; // Trạng thái khóa học (đang hoạt động hay không)
    int totalLessons; // Tổng số bài học trong khóa học

    LocalDate startDate;
    LocalDate endDate;

    @ManyToOne
    User instructor; // Giảng viên phụ trách khóa học

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    Set<Lesson> lessons; // Danh sách các bài học trong khóa học
}
