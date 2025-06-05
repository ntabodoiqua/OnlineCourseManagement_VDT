package com.ntabodoiqua.online_course_management.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntabodoiqua.online_course_management.dto.request.ApiResponse;
import com.ntabodoiqua.online_course_management.dto.request.course.CourseCreationRequest;
import com.ntabodoiqua.online_course_management.dto.request.course.CourseFilterRequest;
import com.ntabodoiqua.online_course_management.dto.request.course.CourseUpdateRequest;
import com.ntabodoiqua.online_course_management.dto.request.lesson.LessonFilterRequest;
import com.ntabodoiqua.online_course_management.dto.response.course.CourseResponse;
import com.ntabodoiqua.online_course_management.dto.response.lesson.LessonResponse;
import com.ntabodoiqua.online_course_management.exception.AppException;
import com.ntabodoiqua.online_course_management.exception.ErrorCode;
import com.ntabodoiqua.online_course_management.service.CourseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CourseController {
    CourseService courseService;
    @Autowired
    private final ObjectMapper objectMapper;
    // API tạo khóa học mới với thông tin và hình ảnh thumbnail
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CourseResponse> createCourse(
            @RequestPart("course") String courseJson,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail) {
        try {
            CourseCreationRequest request = objectMapper.readValue(courseJson, CourseCreationRequest.class);
            CourseResponse courseResponse = courseService.createCourse(request, thumbnail);

            return ApiResponse.<CourseResponse>builder()
                    .message("Course created successfully")
                    .result(courseResponse)
                    .build();
        } catch (IOException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

//    // API lấy toàn bộ bài học với các tham số lọc
//    @GetMapping
//    public ApiResponse<Page<LessonResponse>> getAllLessons(@ModelAttribute LessonFilterRequest filter, Pageable pageable) {
//        return ApiResponse.<Page<LessonResponse>>builder()
//                .result(lessonService.getAllLessons(filter, pageable))
//                .build();
//    }

    // API lấy danh sách khóa học với các tham số lọc và phân trang
    @GetMapping
    public ApiResponse<Page<CourseResponse>> getAllCourses(
            @ModelAttribute CourseFilterRequest filter,
            Pageable pageable) {
        Page<CourseResponse> courses = courseService.getCourses(filter, pageable);
        return ApiResponse.<Page<CourseResponse>>builder()
                .result(courses)
                .build();
    }

    // API lấy thông tin chi tiết của một khóa học theo ID
    @GetMapping("/{courseId}")
    public ApiResponse<CourseResponse> getCourseById(@PathVariable String courseId) {
        CourseResponse courseResponse = courseService.getCourseById(courseId);
        return ApiResponse.<CourseResponse>builder()
                .result(courseResponse)
                .build();
    }

    // API xóa khóa học theo ID
    @DeleteMapping("/{courseId}")
    public ApiResponse<String> deleteCourse(@PathVariable String courseId) {
        courseService.deleteCourse(courseId);
        return ApiResponse.<String>builder()
                .message("Course deleted successfully")
                .result("Course has been deleted")
                .build();
    }

    // API cập nhật thông tin khóa học với thông tin và hình ảnh thumbnail
    @PutMapping(value = "/{courseId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CourseResponse> updateCourse(
            @PathVariable String courseId,
            @RequestPart("course") String courseJson,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail) {
        try {
            CourseUpdateRequest request = objectMapper.readValue(courseJson, CourseUpdateRequest.class);
            CourseResponse courseResponse = courseService.updateCourse(courseId, request, thumbnail);

            return ApiResponse.<CourseResponse>builder()
                    .message("Course updated successfully")
                    .result(courseResponse)
                    .build();
        } catch (JsonProcessingException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    // API toggle trạng thái khóa học (đơn giản)
    @PatchMapping("/{courseId}/toggle-status")
    public ApiResponse<CourseResponse> toggleCourseStatus(
            @PathVariable String courseId,
            @RequestBody Map<String, Boolean> statusRequest) {
        Boolean isActive = statusRequest.get("isActive");
        if (isActive == null) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }
        
        CourseResponse courseResponse = courseService.toggleCourseStatus(courseId, isActive);
        return ApiResponse.<CourseResponse>builder()
                .message("Course status updated successfully")
                .result(courseResponse)
                .build();
    }

    
}
