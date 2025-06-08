package com.ntabodoiqua.online_course_management.service;

import com.ntabodoiqua.online_course_management.dto.request.quiz.*;
import com.ntabodoiqua.online_course_management.dto.response.quiz.*;
import com.ntabodoiqua.online_course_management.entity.*;
import com.ntabodoiqua.online_course_management.enums.AttemptStatus;
import com.ntabodoiqua.online_course_management.enums.ScoringMethod;
import com.ntabodoiqua.online_course_management.exception.AppException;
import com.ntabodoiqua.online_course_management.exception.ErrorCode;
import com.ntabodoiqua.online_course_management.mapper.quiz.QuizMapperFacade;
import com.ntabodoiqua.online_course_management.mapper.quiz.QuizAttemptMapper;
import com.ntabodoiqua.online_course_management.mapper.quiz.QuizQuestionMapper;
import com.ntabodoiqua.online_course_management.mapper.quiz.QuizAnswerMapper;
import com.ntabodoiqua.online_course_management.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuizAttemptService {
    
    QuizRepository quizRepository;
    QuizQuestionRepository quizQuestionRepository;
    QuizAnswerRepository quizAnswerRepository;
    QuizAttemptRepository quizAttemptRepository;
    QuizAttemptAnswerRepository quizAttemptAnswerRepository;
    UserRepository userRepository;
    EnrollmentRepository enrollmentRepository;
    ProgressService progressService;
    
    QuizMapperFacade quizMapperFacade;
    QuizAttemptMapper quizAttemptMapper;
    QuizQuestionMapper quizQuestionMapper;
    QuizAnswerMapper quizAnswerMapper;
    
    // In-memory storage for preview sessions (In production, use Redis or similar)
    private final Map<String, QuizPreviewSession> previewSessions = new ConcurrentHashMap<>();
    
    /**
     * Bắt đầu làm quiz - tạo attempt mới
     */
    @PreAuthorize("hasRole('STUDENT')")
    @Transactional
    public QuizAttemptResponse startQuizAttempt(String quizId) {
        log.info("Starting quiz attempt for quiz: {}", quizId);
        
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_FOUND));
        
        User student = getCurrentUser();
        
        // Validate quiz availability
        validateQuizAvailability(quiz, student);
        
        // Check if student has any in-progress attempt
        Optional<QuizAttempt> inProgressAttempt = quizAttemptRepository
                .findByQuizIdAndStudentIdAndStatus(quizId, student.getId(), AttemptStatus.IN_PROGRESS);
        
        if (inProgressAttempt.isPresent()) {
            // Return existing in-progress attempt
            return quizMapperFacade.toQuizAttemptResponseWithDetails(inProgressAttempt.get());
        }
        
        // Check attempt limits
        int currentAttempts = (int) quizAttemptRepository.countByQuizIdAndStudentId(quizId, student.getId());
        if (quiz.getMaxAttempts() != null && quiz.getMaxAttempts() > 0 && currentAttempts >= quiz.getMaxAttempts()) {
            throw new AppException(ErrorCode.QUIZ_MAX_ATTEMPTS_EXCEEDED);
        }
        
        // Create new attempt
        QuizAttempt attempt = QuizAttempt.builder()
                .quiz(quiz)
                .student(student)
                .attemptNumber(currentAttempts + 1)
                .status(AttemptStatus.IN_PROGRESS)
                .startedAt(LocalDateTime.now())
                .totalQuestions(quiz.getQuestions().size())
                .correctAnswers(0)
                .incorrectAnswers(0)
                .unansweredQuestions(quiz.getQuestions().size())
                .score(0.0)
                .percentage(0.0)
                .isPassed(false)
                .build();
        
        attempt = quizAttemptRepository.save(attempt);
        
        // Initialize attempt answers for all questions
        initializeAttemptAnswers(attempt, quiz.getQuestions());
        
        log.info("Quiz attempt started successfully: {}", attempt.getId());
        return quizMapperFacade.toQuizAttemptResponseWithDetails(attempt);
    }
    
    /**
     * Trả lời câu hỏi trong quiz
     */
    @PreAuthorize("hasRole('STUDENT')")
    @Transactional
    public QuizAttemptAnswerResponse answerQuestion(String attemptId, String questionId, QuizAttemptAnswerRequest request) {
        log.info("Answering question {} in attempt {}", questionId, attemptId);
        
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_ATTEMPT_NOT_FOUND));
        
        // Validate attempt
        validateAttemptAccess(attempt);
        
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new AppException(ErrorCode.QUIZ_ATTEMPT_NOT_IN_PROGRESS);
        }
        
        // Check time limit
        if (isAttemptExpired(attempt)) {
            expireAttempt(attempt);
            throw new AppException(ErrorCode.QUIZ_ATTEMPT_EXPIRED);
        }
        
        // Find attempt answer
        QuizAttemptAnswer attemptAnswer = quizAttemptAnswerRepository
                .findByAttemptIdAndQuestionId(attemptId, questionId)
                .orElseThrow(() -> new AppException(ErrorCode.ATTEMPT_ANSWER_NOT_FOUND));
        
        // Find selected answer
        QuizAnswer selectedAnswer = quizAnswerRepository.findById(request.getSelectedAnswerId())
                .orElseThrow(() -> new AppException(ErrorCode.ANSWER_NOT_FOUND));
        
        // Validate answer belongs to question
        if (!selectedAnswer.getQuestion().getId().equals(questionId)) {
            throw new AppException(ErrorCode.INVALID_ANSWER_FOR_QUESTION);
        }
        
        // Update attempt answer
        boolean wasAnswered = attemptAnswer.getSelectedAnswer() != null;
        attemptAnswer.setSelectedAnswer(selectedAnswer);
        attemptAnswer.setIsCorrect(selectedAnswer.getIsCorrect());
        attemptAnswer.setPointsEarned(selectedAnswer.getIsCorrect() ? attemptAnswer.getQuestion().getPoints() : 0.0);
        attemptAnswer.setAnsweredAt(LocalDateTime.now());
        
        attemptAnswer = quizAttemptAnswerRepository.save(attemptAnswer);
        
        // Update attempt statistics if this is a new answer
        if (!wasAnswered) {
            updateAttemptStatistics(attempt);
        }
        
        log.info("Question answered successfully");
        return quizMapperFacade.toQuizAttemptAnswerResponseWithDetails(attemptAnswer);
    }
    
    /**
     * Nộp bài quiz
     */
    @PreAuthorize("hasRole('STUDENT')")
    @Transactional
    public QuizResultResponse submitQuiz(String attemptId) {
        log.info("Submitting quiz attempt: {}", attemptId);
        
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_ATTEMPT_NOT_FOUND));
        
        validateAttemptAccess(attempt);
        
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new AppException(ErrorCode.QUIZ_ATTEMPT_NOT_IN_PROGRESS);
        }
        
        // Calculate final scores
        calculateFinalScores(attempt);
        
        // Mark as completed
        attempt.setStatus(AttemptStatus.COMPLETED);
        attempt.setSubmittedAt(LocalDateTime.now());
        attempt.setCompletedAt(LocalDateTime.now());
        
        attempt = quizAttemptRepository.save(attempt);
        
        // Update progress if quiz is passed
        if (attempt.getIsPassed()) {
            updateLessonProgress(attempt);
        }
        
        log.info("Quiz submitted successfully with score: {}%", attempt.getPercentage());
        
        // Prepare result response
        QuizResultResponse result = quizAttemptMapper.toQuizResultResponse(attempt);
        
        // Add additional info
        int userAttempts = (int) quizAttemptRepository.countByQuizIdAndStudentId(
                attempt.getQuiz().getId(), attempt.getStudent().getId());
        int remainingAttempts = calculateRemainingAttempts(attempt.getQuiz(), userAttempts);
        boolean canRetake = canStudentRetakeQuiz(attempt.getQuiz(), userAttempts);
        
        result.setCanRetake(canRetake);
        result.setRemainingAttempts(remainingAttempts);
        result.setFeedback(generateFeedback(attempt));
        
        return result;
    }
    
    /**
     * Lấy attempt hiện tại của student
     */
    @PreAuthorize("hasRole('STUDENT')")
    public QuizAttemptResponse getCurrentAttempt(String quizId) {
        log.info("Getting current attempt for quiz: {}", quizId);
        
        User student = getCurrentUser();
        
        Optional<QuizAttempt> attempt = quizAttemptRepository
                .findByQuizIdAndStudentIdAndStatus(quizId, student.getId(), AttemptStatus.IN_PROGRESS);
        
        if (attempt.isEmpty()) {
            throw new AppException(ErrorCode.NO_ACTIVE_ATTEMPT_FOUND);
        }
        
        // Check if attempt has expired
        if (isAttemptExpired(attempt.get())) {
            expireAttempt(attempt.get());
            throw new AppException(ErrorCode.QUIZ_ATTEMPT_EXPIRED);
        }
        
        return quizMapperFacade.toQuizAttemptResponseWithDetails(attempt.get());
    }
    
    /**
     * Lấy lịch sử attempts của student
     */
    @PreAuthorize("hasRole('STUDENT')")
    public List<QuizResultResponse> getStudentAttemptHistory(String quizId) {
        log.info("Getting attempt history for quiz: {}", quizId);
        
        User student = getCurrentUser();
        
        List<QuizAttempt> attempts = quizAttemptRepository
                .findByQuizIdAndStudentIdOrderByAttemptNumberDesc(quizId, student.getId());
        
        return attempts.stream()
                .filter(attempt -> attempt.getStatus() == AttemptStatus.COMPLETED)
                .map(quizAttemptMapper::toQuizResultResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy kết quả tốt nhất của student
     */
    @PreAuthorize("hasRole('STUDENT')")
    public QuizResultResponse getBestScore(String quizId) {
        log.info("Getting best score for quiz: {}", quizId);
        
        User student = getCurrentUser();
        
        List<QuizAttempt> attempts = quizAttemptRepository
                .findByQuizIdAndStudentIdOrderByScoreDesc(quizId, student.getId());
        
        if (attempts.isEmpty()) {
            throw new AppException(ErrorCode.NO_ATTEMPTS_FOUND);
        }
        
        return quizAttemptMapper.toQuizResultResponse(attempts.get(0));
    }
    
    // ================= HELPER METHODS =================
    
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }
    
    private void validateQuizAvailability(Quiz quiz, User student) {
        // Check if quiz is active
        if (!Boolean.TRUE.equals(quiz.getIsActive())) {
            throw new AppException(ErrorCode.QUIZ_NOT_AVAILABLE);
        }
        
        // Check time availability
        LocalDateTime now = LocalDateTime.now();
        if (quiz.getStartTime() != null && now.isBefore(quiz.getStartTime())) {
            throw new AppException(ErrorCode.QUIZ_NOT_STARTED);
        }
        
        if (quiz.getEndTime() != null && now.isAfter(quiz.getEndTime())) {
            throw new AppException(ErrorCode.QUIZ_EXPIRED);
        }
        
        // Check if student has access to the quiz (enrolled in course)
        boolean hasAccess = enrollmentRepository.findByStudent(student)
                .stream()
                .anyMatch(enrollment -> 
                    enrollment.getCourse().getCourseLessons()
                            .stream()
                            .anyMatch(cl -> cl.getLesson().getId().equals(quiz.getLesson().getId()))
                );
        
        if (!hasAccess) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }
    
    private void validateAttemptAccess(QuizAttempt attempt) {
        User currentUser = getCurrentUser();
        if (!attempt.getStudent().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }
    
    private boolean isAttemptExpired(QuizAttempt attempt) {
        if (attempt.getQuiz().getTimeLimitMinutes() == null) {
            return false; // No time limit
        }
        
        LocalDateTime expirationTime = attempt.getStartedAt()
                .plusMinutes(attempt.getQuiz().getTimeLimitMinutes());
        
        return LocalDateTime.now().isAfter(expirationTime);
    }
    
    private void expireAttempt(QuizAttempt attempt) {
        attempt.setStatus(AttemptStatus.EXPIRED);
        attempt.setCompletedAt(LocalDateTime.now());
        quizAttemptRepository.save(attempt);
    }
    
    private void initializeAttemptAnswers(QuizAttempt attempt, Set<QuizQuestion> questions) {
        for (QuizQuestion question : questions) {
            QuizAttemptAnswer attemptAnswer = QuizAttemptAnswer.builder()
                    .attempt(attempt)
                    .question(question)
                    .isCorrect(false)
                    .pointsEarned(0.0)
                    .build();
            
            quizAttemptAnswerRepository.save(attemptAnswer);
        }
    }
    
    private void updateAttemptStatistics(QuizAttempt attempt) {
        List<QuizAttemptAnswer> attemptAnswers = quizAttemptAnswerRepository.findByAttemptId(attempt.getId());
        
        int answered = 0;
        int correct = 0;
        int incorrect = 0;
        
        for (QuizAttemptAnswer answer : attemptAnswers) {
            if (answer.getSelectedAnswer() != null) {
                answered++;
                if (answer.getIsCorrect()) {
                    correct++;
                } else {
                    incorrect++;
                }
            }
        }
        
        attempt.setCorrectAnswers(correct);
        attempt.setIncorrectAnswers(incorrect);
        attempt.setUnansweredQuestions(attempt.getTotalQuestions() - answered);
        
        // Calculate current percentage
        if (attempt.getTotalQuestions() > 0) {
            attempt.setPercentage((double) correct / attempt.getTotalQuestions() * 100);
        }
        
        quizAttemptRepository.save(attempt);
    }
    
    private void calculateFinalScores(QuizAttempt attempt) {
        List<QuizAttemptAnswer> attemptAnswers = quizAttemptAnswerRepository.findByAttemptId(attempt.getId());
        
        double totalPossiblePoints = attemptAnswers.stream()
                .mapToDouble(answer -> answer.getQuestion().getPoints())
                .sum();
        
        double earnedPoints = attemptAnswers.stream()
                .mapToDouble(answer -> answer.getPointsEarned() != null ? answer.getPointsEarned() : 0.0)
                .sum();
        
        attempt.setScore(earnedPoints);
        
        if (totalPossiblePoints > 0) {
            attempt.setPercentage(earnedPoints / totalPossiblePoints * 100);
        } else {
            attempt.setPercentage(0.0);
        }
        
        // Check if passed
        Double passingScore = attempt.getQuiz().getPassingScore();
        if (passingScore != null) {
            attempt.setIsPassed(attempt.getPercentage() >= passingScore);
        } else {
            attempt.setIsPassed(attempt.getPercentage() >= 70.0); // Default 70%
        }
    }
    
    private void updateLessonProgress(QuizAttempt attempt) {
        try {
            progressService.updateQuizProgress(
                    attempt.getStudent().getId(),
                    attempt.getQuiz().getLesson().getId(),
                    attempt.getQuiz().getId(),
                    attempt.getScore()
            );
        } catch (Exception e) {
            log.warn("Failed to update lesson progress for attempt: {}", attempt.getId(), e);
            // Don't fail the submission if progress update fails
        }
    }
    
    private int calculateRemainingAttempts(Quiz quiz, int currentAttempts) {
        if (quiz.getMaxAttempts() == null || quiz.getMaxAttempts() <= 0) {
            return -1; // Unlimited
        }
        
        return Math.max(0, quiz.getMaxAttempts() - currentAttempts);
    }
    
    private boolean canStudentRetakeQuiz(Quiz quiz, int currentAttempts) {
        if (!Boolean.TRUE.equals(quiz.getIsActive())) {
            return false;
        }
        
        // Check time availability
        LocalDateTime now = LocalDateTime.now();
        if (quiz.getEndTime() != null && now.isAfter(quiz.getEndTime())) {
            return false;
        }
        
        // Check attempt limits
        if (quiz.getMaxAttempts() != null && quiz.getMaxAttempts() > 0) {
            return currentAttempts < quiz.getMaxAttempts();
        }
        
        return true; // Unlimited attempts
    }
    
    private String generateFeedback(QuizAttempt attempt) {
        double percentage = attempt.getPercentage();
        
        if (percentage >= 90) {
            return "Excellent work! You have mastered this topic.";
        } else if (percentage >= 80) {
            return "Great job! You have a solid understanding of the material.";
        } else if (percentage >= 70) {
            return "Good work! You passed, but consider reviewing the material.";
        } else if (percentage >= 60) {
            return "You're getting there. Review the material and try again.";
        } else {
            return "Keep studying and practicing. Don't give up!";
        }
    }
    
    // ================= QUIZ PREVIEW METHODS =================
    
    /**
     * Bắt đầu quiz preview cho instructor/admin
     */
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public QuizAttemptResponse startQuizPreview(String quizId) {
        log.info("Starting quiz preview for quiz: {}", quizId);
        
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_FOUND));
        
        User currentUser = getCurrentUser();
        
        // Validate permission
        validateQuizPreviewPermission(quiz, currentUser);
        
        // Generate session ID
        String sessionId = UUID.randomUUID().toString();
        
        // Create preview session
        QuizPreviewSession session = QuizPreviewSession.create(sessionId, quizId, currentUser.getId());
        previewSessions.put(sessionId, session);
        
        // Create mock QuizAttemptResponse for preview
        QuizAttemptResponse response = QuizAttemptResponse.builder()
                .id(sessionId) // Use sessionId as attemptId
                .quiz(quizMapperFacade.toQuizResponseWithDetails(quiz))
                .student(null) // No student in preview
                .attemptNumber(1)
                .startedAt(session.getStartedAt())
                .status(AttemptStatus.IN_PROGRESS)
                .totalQuestions(quiz.getQuestions().size())
                .correctAnswers(0)
                .incorrectAnswers(0)
                .unansweredQuestions(quiz.getQuestions().size())
                .score(0.0)
                .percentage(0.0)
                .isPassed(false)
                .attemptAnswers(new ArrayList<>())
                .build();
        
        log.info("Quiz preview started successfully with session: {}", sessionId);
        return response;
    }
    
    /**
     * Trả lời câu hỏi trong preview mode
     */
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public QuizAttemptAnswerResponse answerQuestionPreview(String sessionId, String questionId, QuizAttemptAnswerRequest request) {
        log.info("Answering question {} in preview session {}", questionId, sessionId);
        
        // Get preview session
        QuizPreviewSession session = previewSessions.get(sessionId);
        if (session == null) {
            throw new AppException(ErrorCode.QUIZ_ATTEMPT_NOT_FOUND);
        }
        
        // Validate user
        User currentUser = getCurrentUser();
        if (!session.getUserId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        
        if (session.isCompleted()) {
            throw new AppException(ErrorCode.QUIZ_ATTEMPT_NOT_IN_PROGRESS);
        }
        
        // Find question
        QuizQuestion question = quizQuestionRepository.findById(questionId)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));
        
        // Find selected answer
        QuizAnswer selectedAnswer = quizAnswerRepository.findById(request.getSelectedAnswerId())
                .orElseThrow(() -> new AppException(ErrorCode.ANSWER_NOT_FOUND));
        
        // Validate answer belongs to question
        if (!selectedAnswer.getQuestion().getId().equals(questionId)) {
            throw new AppException(ErrorCode.INVALID_ANSWER_FOR_QUESTION);
        }
        
        // Store answer in session
        session.answerQuestion(questionId, selectedAnswer);
        
        // Create response
        QuizAttemptAnswerResponse response = QuizAttemptAnswerResponse.builder()
                .id(sessionId + "-" + questionId)
                .question(quizQuestionMapper.toQuizQuestionResponse(question))
                .selectedAnswer(quizAnswerMapper.toQuizAnswerResponse(selectedAnswer))
                .isCorrect(selectedAnswer.getIsCorrect())
                .pointsEarned(selectedAnswer.getIsCorrect() ? question.getPoints() : 0.0)
                .answeredAt(LocalDateTime.now())
                .build();
        
        log.info("Question answered successfully in preview");
        return response;
    }
    
    /**
     * Nộp bài preview quiz
     */
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public QuizResultResponse submitQuizPreview(String sessionId) {
        log.info("Submitting quiz preview session: {}", sessionId);
        
        // Get preview session
        QuizPreviewSession session = previewSessions.get(sessionId);
        if (session == null) {
            throw new AppException(ErrorCode.QUIZ_ATTEMPT_NOT_FOUND);
        }
        
        // Validate user
        User currentUser = getCurrentUser();
        if (!session.getUserId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        
        if (session.isCompleted()) {
            throw new AppException(ErrorCode.QUIZ_ATTEMPT_NOT_IN_PROGRESS);
        }
        
        // Get quiz
        Quiz quiz = quizRepository.findById(session.getQuizId())
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_FOUND));
        
        // Calculate results
        QuizResultResponse result = calculatePreviewResults(session, quiz);
        
        // Mark session as completed
        session.complete();
        
        // Clean up session after some time (optional)
        // In production, you might want to use a scheduled task to clean up old sessions
        
        log.info("Quiz preview submitted successfully with score: {}%", result.getPercentage());
        return result;
    }
    
    /**
     * Lấy trạng thái preview session hiện tại
     */
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public QuizAttemptResponse getPreviewStatus(String sessionId) {
        log.info("Getting preview session status: {}", sessionId);
        
        // Get preview session
        QuizPreviewSession session = previewSessions.get(sessionId);
        if (session == null) {
            throw new AppException(ErrorCode.QUIZ_ATTEMPT_NOT_FOUND);
        }
        
        // Validate user
        User currentUser = getCurrentUser();
        if (!session.getUserId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        
        // Get quiz
        Quiz quiz = quizRepository.findById(session.getQuizId())
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_FOUND));
        
        // Build response
        AttemptStatus status = session.isCompleted() ? AttemptStatus.COMPLETED : AttemptStatus.IN_PROGRESS;
        int answeredCount = session.getAnswers().size();
        
        QuizAttemptResponse response = QuizAttemptResponse.builder()
                .id(sessionId)
                .quiz(quizMapperFacade.toQuizResponseWithDetails(quiz))
                .student(null) // No student in preview
                .attemptNumber(1)
                .startedAt(session.getStartedAt())
                .completedAt(session.getCompletedAt())
                .status(status)
                .totalQuestions(quiz.getQuestions().size())
                .unansweredQuestions(quiz.getQuestions().size() - answeredCount)
                .build();
        
        return response;
    }
    
    // Preview helper methods
    private void validateQuizPreviewPermission(Quiz quiz, User user) {
        // Admin can preview any quiz
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));
        
        if (isAdmin) {
            return;
        }
        
        // Instructor can preview their own quizzes
        boolean isOwner = quiz.getCreatedBy().getId().equals(user.getId());
        if (!isOwner) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }
    
    private QuizResultResponse calculatePreviewResults(QuizPreviewSession session, Quiz quiz) {
        int totalQuestions = quiz.getQuestions().size();
        int answeredQuestions = session.getAnswers().size();
        int correctAnswers = 0;
        double totalPoints = 0.0;
        double earnedPoints = 0.0;
        
        for (QuizQuestion question : quiz.getQuestions()) {
            totalPoints += question.getPoints();
            
            QuizAnswer selectedAnswer = session.getAnswer(question.getId());
            if (selectedAnswer != null && selectedAnswer.getIsCorrect()) {
                correctAnswers++;
                earnedPoints += question.getPoints();
            }
        }
        
        double percentage = totalPoints > 0 ? (earnedPoints / totalPoints * 100) : 0.0;
        boolean isPassed = quiz.getPassingScore() != null ? 
                percentage >= quiz.getPassingScore() : percentage >= 70.0;
        
        return QuizResultResponse.builder()
                .attemptId(session.getSessionId())
                .quizTitle(quiz.getTitle())
                .score(earnedPoints)
                .percentage(percentage)
                .isPassed(isPassed)
                .totalQuestions(totalQuestions)
                .correctAnswers(correctAnswers)
                .incorrectAnswers(answeredQuestions - correctAnswers)
                .attemptNumber(1)
                .completedAt(LocalDateTime.now())
                .feedback("Preview completed - " + generatePreviewFeedback(percentage))
                .canRetake(true) // Always true for preview
                .remainingAttempts(-1) // Unlimited for preview
                .build();
    }
    
    private String generatePreviewFeedback(double percentage) {
        return "This is a preview result. " + generateFeedback(null);
    }
} 