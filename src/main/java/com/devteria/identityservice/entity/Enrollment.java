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
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    // Học sinh tham gia khóa học
    @ManyToOne
    User student;

    // Khóa học mà học sinh tham gia
    @ManyToOne
    Course course;

    // Ngày đăng ký khóa học
    LocalDate enrollmentDate;

    // Đã hoàn thành khóa học hay chưa
    boolean isCompleted;
    // Ngày hoàn thành khóa học (nếu đã hoàn thành)
    LocalDate completionDate;

    int totalLessons; // Tổng số bài học trong khóa học

    double progress; // Tiến độ học tập (0.0 - 1.0)
}
