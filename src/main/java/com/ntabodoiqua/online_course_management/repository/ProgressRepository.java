package com.ntabodoiqua.online_course_management.repository;

import com.ntabodoiqua.online_course_management.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, String> {
    List<Progress> findByEnrollmentId(String enrollmentId);
    Optional<Progress> findByEnrollmentIdAndLessonId(String enrollmentId, String lessonId);
    long countByEnrollmentIdAndIsCompletedTrue(String enrollmentId);
}
