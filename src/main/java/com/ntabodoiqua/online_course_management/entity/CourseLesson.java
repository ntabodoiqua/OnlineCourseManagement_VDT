package com.ntabodoiqua.online_course_management.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class CourseLesson {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    Course course;

    @ManyToOne
    Lesson lesson;

    int orderIndex;
    Boolean isVisible;

    // Chỉ 1 bài học trước (trong cùng khóa học)
    @ManyToOne
    CourseLesson prerequisite;
}
