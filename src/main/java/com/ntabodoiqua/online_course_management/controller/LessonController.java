package com.ntabodoiqua.online_course_management.controller;

import com.ntabodoiqua.online_course_management.dto.request.ApiResponse;
import com.ntabodoiqua.online_course_management.dto.request.lesson.LessonFilterRequest;
import com.ntabodoiqua.online_course_management.dto.request.lesson.LessonRequest;
import com.ntabodoiqua.online_course_management.dto.response.lesson.LessonResponse;
import com.ntabodoiqua.online_course_management.service.LessonService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lessons")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LessonController {
    LessonService lessonService;
    @PostMapping
    public ApiResponse<LessonResponse> createLesson(@RequestBody LessonRequest request) {
        return ApiResponse.<LessonResponse>builder()
                .result(lessonService.createLesson(request))
                .message("Lesson created successfully")
                .build();
    }

    @PutMapping("/{lessonId}")

    public ApiResponse<LessonResponse> updateLesson(@PathVariable String lessonId, @RequestBody LessonRequest request) {
        return ApiResponse.<LessonResponse>builder()
                .result(lessonService.updateLesson(lessonId, request))
                .message("Lesson updated successfully")
                .build();
    }

    // API lấy toàn bộ bài học với các tham số lọc
    @GetMapping
    public ApiResponse<Page<LessonResponse>> getAllLessons(@ModelAttribute LessonFilterRequest filter, Pageable pageable) {
        return ApiResponse.<Page<LessonResponse>>builder()
                .result(lessonService.getAllLessons(filter, pageable))
                .build();
    }

    // API xóa bài học
    @DeleteMapping("/{lessonId}")
    public ApiResponse<String> deleteLesson(@PathVariable String lessonId) {
        return ApiResponse.<String>builder()
                .message("Lesson deleted successfully")
                .result("Lesson with ID " + lessonId + " has been deleted.")
                .build();
    }
}
