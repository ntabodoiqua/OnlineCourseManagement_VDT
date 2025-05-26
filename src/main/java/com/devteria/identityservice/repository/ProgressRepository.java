package com.devteria.identityservice.repository;

import com.devteria.identityservice.entity.Course;
import com.devteria.identityservice.entity.Progress;
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
