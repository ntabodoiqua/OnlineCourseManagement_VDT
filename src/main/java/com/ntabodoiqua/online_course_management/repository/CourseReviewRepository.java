package com.ntabodoiqua.online_course_management.repository;

import com.ntabodoiqua.online_course_management.entity.CourseReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, String>, JpaSpecificationExecutor<CourseReview> {

    boolean existsByStudentIdAndCourseId(String studentId, String courseId);

    Page<CourseReview> findByCourseId(String courseId, Pageable pageable);

    Page<CourseReview> findByCourseIdAndIsApprovedTrue(String courseId, Pageable pageable);

    Page<CourseReview> findByCourseIdAndIsApprovedTrueAndIsRejectedFalse(String courseId, Pageable pageable);


    void deleteByCourseId(String courseId);

    Page<CourseReview> findByCourseIdAndIsApprovedFalse(String courseId, Pageable pageable);

    // Methods for instructor statistics
    @Query("SELECT cr FROM CourseReview cr WHERE cr.course.instructor.id = :instructorId ORDER BY cr.reviewDate DESC")
    Page<CourseReview> findByInstructorIdOrderByReviewDateDesc(@Param("instructorId") String instructorId, Pageable pageable);
}
