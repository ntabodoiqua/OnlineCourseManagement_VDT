package com.devteria.identityservice.repository;

import com.devteria.identityservice.entity.Course;
import com.devteria.identityservice.entity.CourseReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, String> {

    boolean existsByStudentIdAndCourseId(String studentId, String courseId);

    List<CourseReview> findByCourseId(String courseId);

    List<CourseReview> findByCourseIdAndIsApprovedTrue(String courseId);
}
