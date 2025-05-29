package com.ntabodoiqua.online_course_management.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false, unique = true)
    String name;

    @Column(length = 500)
    String description; // Mô tả ngắn gọn về category

    // Thời gian tạo
    @CreationTimestamp
    @Column(updatable = false)
    LocalDateTime createdAt;
    // Instructor tạo category
    @ManyToOne
    User createdBy;
}
