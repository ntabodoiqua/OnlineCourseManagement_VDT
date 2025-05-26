package com.devteria.identityservice.entity;

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
public class Progress {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    // Tham chiếu đến Enrollment để biết học sinh nào đang học khóa nào
    @ManyToOne
    Enrollment enrollment;

    @ManyToOne
    Lesson lesson; // Bài học hiện tại

    boolean isCompleted; // Đã hoàn thành bài học hay chưa
    LocalDate completionDate; // Ngày hoàn thành bài học
}
