package com.ntabodoiqua.online_course_management.repository;

import com.ntabodoiqua.online_course_management.entity.CourseReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, String> {

    boolean existsByStudentIdAndCourseId(String studentId, String courseId);

    List<CourseReview> findByCourseId(String courseId);

    List<CourseReview> findByCourseIdAndIsApprovedTrue(String courseId);
    void deleteByCourseId(String courseId);
}
