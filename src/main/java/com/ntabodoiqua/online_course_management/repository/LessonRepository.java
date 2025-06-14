package com.ntabodoiqua.online_course_management.repository;

import com.ntabodoiqua.online_course_management.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, String>, JpaSpecificationExecutor<Lesson> {
    boolean existsByTitleAndIdNot(String title, String id);
}
