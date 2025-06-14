package com.ntabodoiqua.online_course_management.entity;

import com.ntabodoiqua.online_course_management.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

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

    double progress; // Tiến độ học tập (0.0 - 1.0)

    @Enumerated(EnumType.STRING)
    EnrollmentStatus approvalStatus; // PENDING, APPROVED, REJECTED
}
