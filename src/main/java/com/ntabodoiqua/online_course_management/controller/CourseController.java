package com.ntabodoiqua.online_course_management.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntabodoiqua.online_course_management.dto.request.ApiResponse;
import com.ntabodoiqua.online_course_management.dto.request.course.CourseCreationRequest;
import com.ntabodoiqua.online_course_management.dto.response.course.CourseResponse;
import com.ntabodoiqua.online_course_management.exception.AppException;
import com.ntabodoiqua.online_course_management.exception.ErrorCode;
import com.ntabodoiqua.online_course_management.service.CourseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CourseController {
    CourseService courseService;
    @Autowired
    private final ObjectMapper objectMapper;
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


}
