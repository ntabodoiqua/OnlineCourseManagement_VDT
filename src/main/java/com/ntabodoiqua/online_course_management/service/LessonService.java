package com.ntabodoiqua.online_course_management.service;

import com.ntabodoiqua.online_course_management.dto.request.lesson.LessonFilterRequest;
import com.ntabodoiqua.online_course_management.dto.request.lesson.LessonRequest;
import com.ntabodoiqua.online_course_management.dto.response.course.CourseResponse;
import com.ntabodoiqua.online_course_management.dto.response.lesson.LessonResponse;
import com.ntabodoiqua.online_course_management.entity.Course;
import com.ntabodoiqua.online_course_management.entity.CourseLesson;
import com.ntabodoiqua.online_course_management.entity.Lesson;
import com.ntabodoiqua.online_course_management.entity.User;
import com.ntabodoiqua.online_course_management.exception.AppException;
import com.ntabodoiqua.online_course_management.exception.ErrorCode;
import com.ntabodoiqua.online_course_management.mapper.CourseMapper;
import com.ntabodoiqua.online_course_management.mapper.LessonMapper;
import com.ntabodoiqua.online_course_management.repository.CourseLessonRepository;
import com.ntabodoiqua.online_course_management.repository.EnrollmentRepository;
import com.ntabodoiqua.online_course_management.repository.LessonRepository;
import com.ntabodoiqua.online_course_management.repository.UserRepository;
import com.ntabodoiqua.online_course_management.specification.LessonSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LessonService {
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    LessonRepository lessonRepository;
    LessonMapper lessonMapper;
    CourseLessonRepository courseLessonRepository;
    CourseMapper courseMapper;

    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")
    public LessonResponse createLesson(LessonRequest request) {
        Lesson lesson = lessonMapper.toLesson(request);
        lesson.setCreatedAt(LocalDateTime.now());
        lesson.setUpdatedAt(LocalDateTime.now());
        // Lấy user hiện tại
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        lesson.setCreatedBy(user);
        lesson = lessonRepository.save(lesson);
        
        // Tính courseCount cho response
        Integer courseCount = (int) courseLessonRepository.countByLesson(lesson);
        return lessonMapper.toLessonResponseWithCourseCount(lesson, courseCount);
    }

    // Logic kiểm tra quyền truy cập
    private void checkLessonPermission(Lesson lesson) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"));
        boolean isOwner = lesson.getCreatedBy() != null && lesson.getCreatedBy().getUsername().equals(username);

        if (!isAdmin && !isOwner) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")
    public LessonResponse updateLesson(String lessonId, LessonRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
        checkLessonPermission(lesson); // kiểm tra quyền
        lessonMapper.updateLessonFromRequest(request, lesson);
        lesson.setUpdatedAt(LocalDateTime.now());
        lesson = lessonRepository.save(lesson);
        
        // Tính courseCount cho response
        Integer courseCount = (int) courseLessonRepository.countByLesson(lesson);
        return lessonMapper.toLessonResponseWithCourseCount(lesson, courseCount);
    }

    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")
    public void deleteLesson(String lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
        checkLessonPermission(lesson); // kiểm tra quyền

        boolean inUse = courseLessonRepository.existsByLessonId(lessonId);
        if (inUse) throw new AppException(ErrorCode.LESSON_IS_USED_IN_COURSE);

        lessonRepository.deleteById(lessonId);
    }

    // Định nghĩa Lesson Response tùy quyền
    public LessonResponse mapLessonToResponse(Lesson lesson, boolean full) {
        if (full) {
            // Tính courseCount cho response đầy đủ
            Integer courseCount = (int) courseLessonRepository.countByLesson(lesson);
            return lessonMapper.toLessonResponseWithCourseCount(lesson, courseCount);
        } else {
            // chỉ trả về id, title, các trường khác null
            return LessonResponse.builder()
                    .id(lesson.getId())
                    .title(lesson.getTitle())
                    .courseCount(0) // Không show courseCount cho limited access
                    .build();
        }
    }

    // Lấy danh sách các bài học với các tham số tìm kiếm
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public Page<LessonResponse> getAllLessons(LessonFilterRequest filter, Pageable pageable) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        boolean isAdmin = roles.contains("ROLE_ADMIN");
        boolean isInstructor = roles.contains("ROLE_INSTRUCTOR");

        Page<Lesson> lessons = lessonRepository.findAll(LessonSpecification.withFilter(filter), pageable);

        // Admin/instructor: luôn trả về full với courseCount
        if (isAdmin || isInstructor) {
            return lessons.map(lesson -> {
                Integer courseCount = (int) courseLessonRepository.countByLesson(lesson);
                return lessonMapper.toLessonResponseWithCourseCount(lesson, courseCount);
            });
        }

        // Student: kiểm tra từng bài học
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Set<String> lessonIdsEnrolled = enrollmentRepository.findByStudent(student).stream()
                .flatMap(enr -> enr.getCourse().getCourseLessons().stream())
                .map(cl -> cl.getLesson().getId())
                .collect(Collectors.toSet());

        return lessons.map(lesson ->
                mapLessonToResponse(lesson, lessonIdsEnrolled.contains(lesson.getId()))
        );
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public LessonResponse getLessonById(String lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));

        // Lấy thông tin user hiện tại
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        boolean isAdmin = roles.contains("ROLE_ADMIN");
        boolean isInstructor = roles.contains("ROLE_INSTRUCTOR");

        if (isAdmin || isInstructor) {
            // Toàn quyền truy cập nội dung với courseCount
            Integer courseCount = (int) courseLessonRepository.countByLesson(lesson);
            return lessonMapper.toLessonResponseWithCourseCount(lesson, courseCount);
        }

        // Nếu là student: kiểm tra đã đăng ký khóa học có lesson này chưa
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean hasAccess = enrollmentRepository.findByStudent(student)
                .stream()
                .flatMap(enr -> enr.getCourse().getCourseLessons().stream())
                .anyMatch(cl -> cl.getLesson().getId().equals(lessonId));

        if (hasAccess) {
            Integer courseCount = (int) courseLessonRepository.countByLesson(lesson);
            return lessonMapper.toLessonResponseWithCourseCount(lesson, courseCount);
        }

        // Nếu không có quyền, chỉ trả về title/id
        return LessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .courseCount(0) // Không show courseCount cho limited access
                .build();
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public List<CourseResponse> getCoursesByLesson(String lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));

        List<CourseLesson> courseLessons = courseLessonRepository.findByLesson(lesson);

        return courseLessons.stream()
                .map(CourseLesson::getCourse)
                .map(courseMapper::toCourseResponse)
                .collect(Collectors.toList());
    }
}
