package com.ntabodoiqua.online_course_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"student_id", "course_id"})
        }
)
public class CourseReview {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    User student;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    Course course; // Khóa học được đánh giá

    @Column(nullable = false)
    @Min(1) @Max(5)
    int rating; // Đánh giá từ 1 đến 5

    @Lob
    String comment; // Nhận xét của học sinh về khóa học

    LocalDate reviewDate; // Ngày đánh giá
    boolean isApproved; // Đánh giá đã được phê duyệt hay chưa
    boolean isRejected; // Đã bị từ chối hay chưa
}
