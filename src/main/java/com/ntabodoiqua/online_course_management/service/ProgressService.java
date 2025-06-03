package com.ntabodoiqua.online_course_management.service;

import com.ntabodoiqua.online_course_management.dto.request.enrollment.ProgressUpdateRequest;
import com.ntabodoiqua.online_course_management.dto.response.enrollment.ProgressResponse;
import com.ntabodoiqua.online_course_management.entity.Enrollment;
import com.ntabodoiqua.online_course_management.entity.Lesson;
import com.ntabodoiqua.online_course_management.entity.Progress;
import com.ntabodoiqua.online_course_management.exception.AppException;
import com.ntabodoiqua.online_course_management.exception.ErrorCode;
import com.ntabodoiqua.online_course_management.repository.EnrollmentRepository;
import com.ntabodoiqua.online_course_management.repository.LessonRepository;
import com.ntabodoiqua.online_course_management.repository.ProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class ProgressService {
    ProgressRepository progressRepository;
    EnrollmentRepository enrollmentRepository;
    LessonRepository lessonRepository;

    public List<ProgressResponse> getProgressByEnrollment(String enrollmentId) {
        List<Progress> progresses = progressRepository.findByEnrollmentId(enrollmentId);
        return progresses.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public ProgressResponse updateProgress(ProgressUpdateRequest request) {
        Enrollment enrollment = enrollmentRepository.findById(request.getEnrollmentId())
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_EXISTED));
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
        Progress progress = progressRepository.findByEnrollmentIdAndLessonId(request.getEnrollmentId(), request.getLessonId())
                .orElse(new Progress(null, enrollment, lesson, false, null));
        progress.setCompleted(request.isCompleted());
        progress.setCompletionDate(request.isCompleted() ? LocalDate.now() : null);
        progressRepository.save(progress);
        // Cập nhật progress tổng thể cho enrollment
        long completed = progressRepository.countByEnrollmentIdAndIsCompletedTrue(request.getEnrollmentId());
        int total = enrollment.getTotalLessons();
        enrollment.setProgress(total > 0 ? (double) completed / total : 0.0);
        if (completed == total && total > 0) {
            enrollment.setCompleted(true);
            enrollment.setCompletionDate(LocalDate.now());
        } else {
            enrollment.setCompleted(false);
            enrollment.setCompletionDate(null);
        }
        enrollmentRepository.save(enrollment);
        return toResponse(progress);
    }

    private ProgressResponse toResponse(Progress progress) {
        return ProgressResponse.builder()
                .id(progress.getId())
                .lessonId(progress.getLesson() != null ? progress.getLesson().getId() : null)
                .isCompleted(progress.isCompleted())
                .completionDate(progress.getCompletionDate())
                .build();
    }
} 