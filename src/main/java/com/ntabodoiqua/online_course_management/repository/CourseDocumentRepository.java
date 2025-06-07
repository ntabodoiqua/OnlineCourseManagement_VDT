package com.ntabodoiqua.online_course_management.repository;

import com.ntabodoiqua.online_course_management.entity.Course;
import com.ntabodoiqua.online_course_management.entity.CourseDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseDocumentRepository extends JpaRepository<CourseDocument, String> {
    List<CourseDocument> findByCourse(Course course);
    List<CourseDocument> findByCourseId(String courseId);
    Optional<CourseDocument> findByIdAndCourseId(String id, String courseId);
    void deleteByCourseId(String courseId);
    
    // New method to find documents by fileName
    List<CourseDocument> findByFileName(String fileName);
} 