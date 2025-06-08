package com.ntabodoiqua.online_course_management.controller;

import com.ntabodoiqua.online_course_management.dto.request.ApiResponse;
import com.ntabodoiqua.online_course_management.dto.request.quiz.QuizAttemptAnswerRequest;
import com.ntabodoiqua.online_course_management.dto.response.quiz.*;
import com.ntabodoiqua.online_course_management.service.QuizAttemptService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/quiz-attempts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class QuizAttemptController {
    
    QuizAttemptService quizAttemptService;
    
    /**
     * Bắt đầu làm quiz
     * Chỉ Student có thể làm quiz
     */
    @PostMapping("/quiz/{quizId}/start")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<QuizAttemptResponse> startQuizAttempt(@PathVariable String quizId) {
        log.info("Starting quiz attempt for quiz: {}", quizId);
        
        QuizAttemptResponse attempt = quizAttemptService.startQuizAttempt(quizId);
        
        return ApiResponse.<QuizAttemptResponse>builder()
                .message("Quiz attempt started successfully")
                .result(attempt)
                .build();
    }
    
    /**
     * Lấy attempt hiện tại (nếu có)
     * Chỉ Student có thể xem attempt của mình
     */
    @GetMapping("/quiz/{quizId}/current")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<QuizAttemptResponse> getCurrentAttempt(@PathVariable String quizId) {
        log.info("Getting current attempt for quiz: {}", quizId);
        
        QuizAttemptResponse attempt = quizAttemptService.getCurrentAttempt(quizId);
        
        return ApiResponse.<QuizAttemptResponse>builder()
                .message("Current attempt retrieved successfully")
                .result(attempt)
                .build();
    }
    
    /**
     * Trả lời câu hỏi trong quiz
     * Chỉ Student có thể trả lời
     */
    @PostMapping("/{attemptId}/questions/{questionId}/answer")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<QuizAttemptAnswerResponse> answerQuestion(
            @PathVariable String attemptId,
            @PathVariable String questionId,
            @Valid @RequestBody QuizAttemptAnswerRequest request) {
        log.info("Answering question {} in attempt {}", questionId, attemptId);
        
        QuizAttemptAnswerResponse answer = quizAttemptService.answerQuestion(attemptId, questionId, request);
        
        return ApiResponse.<QuizAttemptAnswerResponse>builder()
                .message("Question answered successfully")
                .result(answer)
                .build();
    }
    
    /**
     * Nộp bài quiz
     * Chỉ Student có thể nộp bài
     */
    @PostMapping("/{attemptId}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<QuizResultResponse> submitQuiz(@PathVariable String attemptId) {
        log.info("Submitting quiz attempt: {}", attemptId);
        
        QuizResultResponse result = quizAttemptService.submitQuiz(attemptId);
        
        return ApiResponse.<QuizResultResponse>builder()
                .message("Quiz submitted successfully")
                .result(result)
                .build();
    }
    
    /**
     * Lấy lịch sử làm bài của student cho một quiz
     * Chỉ Student có thể xem lịch sử của mình
     */
    @GetMapping("/quiz/{quizId}/history")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<List<QuizResultResponse>> getStudentAttemptHistory(@PathVariable String quizId) {
        log.info("Getting attempt history for quiz: {}", quizId);
        
        List<QuizResultResponse> history = quizAttemptService.getStudentAttemptHistory(quizId);
        
        return ApiResponse.<List<QuizResultResponse>>builder()
                .message("Attempt history retrieved successfully")
                .result(history)
                .build();
    }
    
    /**
     * Lấy điểm cao nhất của student cho một quiz
     * Chỉ Student có thể xem điểm của mình
     */
    @GetMapping("/quiz/{quizId}/best-score")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<QuizResultResponse> getBestScore(@PathVariable String quizId) {
        log.info("Getting best score for quiz: {}", quizId);
        
        QuizResultResponse bestScore = quizAttemptService.getBestScore(quizId);
        
        return ApiResponse.<QuizResultResponse>builder()
                .message("Best score retrieved successfully")
                .result(bestScore)
                .build();
    }
    
    // =============== QUIZ PREVIEW FOR INSTRUCTOR/ADMIN ===============
    
    /**
     * Bắt đầu preview quiz cho instructor/admin
     * Không lưu vào database, chỉ để test quiz
     */
    @PostMapping("/quiz/{quizId}/preview/start")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ApiResponse<QuizAttemptResponse> startQuizPreview(@PathVariable String quizId) {
        log.info("Starting quiz preview for quiz: {}", quizId);
        
        QuizAttemptResponse attempt = quizAttemptService.startQuizPreview(quizId);
        
        return ApiResponse.<QuizAttemptResponse>builder()
                .message("Quiz preview started successfully")
                .result(attempt)
                .build();
    }
    
    /**
     * Trả lời câu hỏi trong preview mode
     * Không lưu vào database, chỉ để test
     */
    @PostMapping("/preview/{sessionId}/questions/{questionId}/answer")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ApiResponse<QuizAttemptAnswerResponse> answerQuestionPreview(
            @PathVariable String sessionId,
            @PathVariable String questionId,
            @Valid @RequestBody QuizAttemptAnswerRequest request) {
        log.info("Answering question {} in preview session {}", questionId, sessionId);
        
        QuizAttemptAnswerResponse answer = quizAttemptService.answerQuestionPreview(sessionId, questionId, request);
        
        return ApiResponse.<QuizAttemptAnswerResponse>builder()
                .message("Question answered successfully in preview")
                .result(answer)
                .build();
    }
    
    /**
     * Nộp bài preview quiz
     * Trả về kết quả mà không lưu vào database
     */
    @PostMapping("/preview/{sessionId}/submit")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ApiResponse<QuizResultResponse> submitQuizPreview(@PathVariable String sessionId) {
        log.info("Submitting quiz preview session: {}", sessionId);
        
        QuizResultResponse result = quizAttemptService.submitQuizPreview(sessionId);
        
        return ApiResponse.<QuizResultResponse>builder()
                .message("Quiz preview submitted successfully")
                .result(result)
                .build();
    }
    
    /**
     * Lấy trạng thái preview session hiện tại
     */
    @GetMapping("/preview/{sessionId}/status")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ApiResponse<QuizAttemptResponse> getPreviewStatus(@PathVariable String sessionId) {
        log.info("Getting preview session status: {}", sessionId);
        
        QuizAttemptResponse status = quizAttemptService.getPreviewStatus(sessionId);
        
        return ApiResponse.<QuizAttemptResponse>builder()
                .message("Preview status retrieved successfully")
                .result(status)
                .build();
    }
} 