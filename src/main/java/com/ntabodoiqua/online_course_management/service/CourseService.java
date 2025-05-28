package com.ntabodoiqua.online_course_management.service;

import com.ntabodoiqua.online_course_management.dto.request.course.CourseCreationRequest;
import com.ntabodoiqua.online_course_management.dto.request.course.CourseUpdateRequest;
import com.ntabodoiqua.online_course_management.dto.response.course.CourseResponse;
import com.ntabodoiqua.online_course_management.entity.Category;
import com.ntabodoiqua.online_course_management.entity.Course;
import com.ntabodoiqua.online_course_management.entity.User;
import com.ntabodoiqua.online_course_management.exception.AppException;
import com.ntabodoiqua.online_course_management.exception.ErrorCode;
import com.ntabodoiqua.online_course_management.mapper.CourseMapper;
import com.ntabodoiqua.online_course_management.repository.CategoryRepository;
import com.ntabodoiqua.online_course_management.repository.CourseRepository;
import com.ntabodoiqua.online_course_management.repository.UserRepository;
import com.ntabodoiqua.online_course_management.service.file.FileStorageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CourseService {
    // Service này sẽ chứa các phương thức liên quan đến quản lý khóa học
    // Bao gồm:
    // - Tạo khóa học mới
    // - Cập nhật thông tin khóa học
    // - Xóa khóa học
    // - Lấy danh sách khóa học
    // - Lấy thông tin chi tiết khóa học
    // - Đăng ký khóa học
    // - Hủy đăng ký khóa học

    CourseRepository courseRepository;
    CategoryRepository categoryRepository;
    UserRepository userRepository;
    CourseMapper courseMapper;
    FileStorageService fileStorageService;

    // Service tạo khóa học mới
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public CourseResponse createCourse(CourseCreationRequest request, MultipartFile thumbnail) {
        // Kiểm tra xem danh mục có tồn tại không
        var category = categoryRepository.findFirstByNameContainingIgnoreCase(request.getCategoryName())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
        // Kiểm tra xem title khóa học đã tồn tại chưa
        if (courseRepository.existsByTitleIgnoreCase(request.getTitle())) {
            throw new AppException(ErrorCode.COURSE_EXISTED);
        }
        // Lấy thông tin người dùng hiện tại
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User instructor = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Lưu thumbnail vào hệ thống file
        String fileName = fileStorageService.storeFile(thumbnail, true);
        Course course = courseMapper.toCourse(request);
        course.setInstructor(instructor);
        course.setCategory(category);
        course.setThumbnailUrl("/uploads/public/" + fileName);
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());

        // Lưu khóa học vào cơ sở dữ liệu
        courseRepository.save(course);
        return courseMapper.toCourseResponse(course);
    }

    // Service cập nhật khóa học
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public CourseResponse updateCourse(String courseId, CourseUpdateRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));

        // Cập nhật thông tin từ DTO vào entity
        courseMapper.updateCourse(course, request);

        // Nếu có thay đổi category
        if (request.getCategoryName() != null) {
            Category category = categoryRepository.findFirstByNameContainingIgnoreCase(request.getCategoryName())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
            course.setCategory(category);
        }

        course.setUpdatedAt(LocalDateTime.now());
        courseRepository.save(course);

        return courseMapper.toCourseResponse(course);
    }



}
