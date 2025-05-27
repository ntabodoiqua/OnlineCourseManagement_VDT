package com.devteria.identityservice.repository;

import com.devteria.identityservice.entity.Course;
import com.devteria.identityservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    List<Course> findByInstructor_Id(String instructorId);
    List<Course> findByIsActiveTrue();
    boolean existsByTitleIgnoreCase(String title);
}
