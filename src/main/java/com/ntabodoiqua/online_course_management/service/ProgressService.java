package com.ntabodoiqua.online_course_management.service;

import com.ntabodoiqua.online_course_management.dto.request.enrollment.ProgressUpdateRequest;
import com.ntabodoiqua.online_course_management.dto.response.enrollment.ProgressResponse;
import com.ntabodoiqua.online_course_management.entity.Enrollment;
import com.ntabodoiqua.online_course_management.entity.Lesson;
import com.ntabodoiqua.online_course_management.entity.Progress;
import com.ntabodoiqua.online_course_management.entity.Quiz;
import com.ntabodoiqua.online_course_management.exception.AppException;
import com.ntabodoiqua.online_course_management.exception.ErrorCode;
import com.ntabodoiqua.online_course_management.repository.EnrollmentRepository;
import com.ntabodoiqua.online_course_management.repository.LessonRepository;
import com.ntabodoiqua.online_course_management.repository.ProgressRepository;
import com.ntabodoiqua.online_course_management.repository.QuizRepository;
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
    QuizRepository quizRepository;

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
                .orElse(new Progress(null, enrollment, lesson, false, null, null, null));
        progress.setCompleted(request.isCompleted());
        progress.setCompletionDate(request.isCompleted() ? LocalDate.now() : null);
        progressRepository.save(progress);

        recalculateAndSaveEnrollmentProgress(enrollment);

        return toResponse(progress);
    }

    @Transactional
    public void recalculateAndSaveEnrollmentProgress(Enrollment enrollment) {
        // Cập nhật progress tổng thể cho enrollment
        long completed = progressRepository.countByEnrollmentIdAndIsCompletedTrue(enrollment.getId());
        int total = enrollment.getCourse().getTotalLessons();
        enrollment.setProgress(total > 0 ? (double) completed / total : 0.0);
        if (completed == total && total > 0) {
            enrollment.setCompleted(true);
            enrollment.setCompletionDate(LocalDate.now());
        } else {
            enrollment.setCompleted(false);
            enrollment.setCompletionDate(null);
        }
        enrollmentRepository.save(enrollment);
    }

    @Transactional
    public void updateQuizProgress(String studentId, String lessonId, String quizId, Double quizScore) {
        // Find the enrollment for this student and the course containing this lesson
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
        
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_FOUND));
                
        // Find enrollment through the course-lesson relationship
        // First, find the course that contains this lesson
        List<Enrollment> studentEnrollments = enrollmentRepository.findAll()
                .stream()
                .filter(e -> e.getStudent().getId().equals(studentId))
                .filter(e -> e.getCourse().getCourseLessons()
                        .stream()
                        .anyMatch(cl -> cl.getLesson().getId().equals(lessonId)))
                .collect(java.util.stream.Collectors.toList());
                
        if (studentEnrollments.isEmpty()) {
            throw new AppException(ErrorCode.ENROLLMENT_NOT_EXISTED);
        }
        
        Enrollment enrollment = studentEnrollments.get(0);
        
        // Find or create progress record
        Progress progress = progressRepository.findByEnrollmentIdAndLessonId(enrollment.getId(), lessonId)
                .orElse(new Progress(null, enrollment, lesson, false, null, null, null));
        
        // Update quiz completion info
        progress.setCompletedQuiz(quiz);
        progress.setQuizScore(quizScore);
        
        // Mark lesson as completed if quiz is passed (assuming 60% is passing)
        boolean isQuizPassed = quizScore != null && quizScore >= 60.0;
        if (isQuizPassed) {
            progress.setCompleted(true);
            progress.setCompletionDate(LocalDate.now());
        }
        
        progressRepository.save(progress);
        
        // Recalculate overall enrollment progress
        recalculateAndSaveEnrollmentProgress(enrollment);
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