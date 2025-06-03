package com.ntabodoiqua.online_course_management.repository;

import com.ntabodoiqua.online_course_management.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String>, JpaSpecificationExecutor<Course> {
    List<Course> findByInstructor_Id(String instructorId);
    List<Course> findByIsActiveTrue();
    boolean existsByTitleIgnoreCase(String title);
}
