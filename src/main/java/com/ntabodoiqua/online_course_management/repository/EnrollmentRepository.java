package com.ntabodoiqua.online_course_management.repository;

import com.ntabodoiqua.online_course_management.entity.Course;
import com.ntabodoiqua.online_course_management.entity.Enrollment;
import com.ntabodoiqua.online_course_management.entity.User;
import com.ntabodoiqua.online_course_management.enums.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, String> {
    Optional<Enrollment> findByStudentIdAndCourseId(String studentId, String courseId);
    Page<Enrollment> findByStudent(User student, Pageable pageable);
    boolean existsByStudentIdAndCourseId(String studentId, String courseId);
    Page<Enrollment> findByCourseAndApprovalStatus(Course course, EnrollmentStatus status, Pageable pageable);
    List<Enrollment> findByStudent(User student);
    void deleteByCourseId(String courseId);
    List<Enrollment> findByCourse(Course course);

    @Query("SELECT e.course.id, COUNT(e.course.id) as enrollmentCount FROM Enrollment e GROUP BY e.course.id ORDER BY enrollmentCount DESC")
    Page<Object[]> findPopularCourseIds(Pageable pageable);

    // Methods for instructor statistics
    @Query("SELECT COUNT(DISTINCT e.student.id) FROM Enrollment e WHERE e.course.instructor.id = :instructorId AND e.approvalStatus = 'APPROVED'")
    long countDistinctStudentsByInstructorId(@Param("instructorId") String instructorId);

    @Query("SELECT e FROM Enrollment e WHERE e.course.instructor.id = :instructorId ORDER BY e.enrollmentDate DESC")
    Page<Enrollment> findByInstructorIdOrderByEnrollmentDateDesc(@Param("instructorId") String instructorId, Pageable pageable);

    @Query("SELECT e.course.id, COUNT(e.course.id) as enrollmentCount FROM Enrollment e WHERE e.course.instructor.id = :instructorId GROUP BY e.course.id ORDER BY enrollmentCount DESC")
    Page<Object[]> findPopularCourseIdsByInstructorId(@Param("instructorId") String instructorId, Pageable pageable);
}
