package com.ntabodoiqua.online_course_management.service;

import com.ntabodoiqua.online_course_management.dto.request.course.CourseLessonRequest;
import com.ntabodoiqua.online_course_management.dto.request.course.CourseLessonUpdateRequest;
import com.ntabodoiqua.online_course_management.dto.request.lesson.CourseLessonFilterRequest;
import com.ntabodoiqua.online_course_management.dto.response.course.CourseLessonResponse;
import com.ntabodoiqua.online_course_management.entity.Course;
import com.ntabodoiqua.online_course_management.entity.CourseLesson;
import com.ntabodoiqua.online_course_management.entity.Lesson;
import com.ntabodoiqua.online_course_management.entity.User;
import com.ntabodoiqua.online_course_management.exception.AppException;
import com.ntabodoiqua.online_course_management.exception.ErrorCode;
import com.ntabodoiqua.online_course_management.mapper.CourseLessonMapper;
import com.ntabodoiqua.online_course_management.repository.CourseLessonRepository;
import com.ntabodoiqua.online_course_management.repository.CourseRepository;
import com.ntabodoiqua.online_course_management.repository.LessonRepository;
import com.ntabodoiqua.online_course_management.repository.UserRepository;
import com.ntabodoiqua.online_course_management.specification.CourseLessonSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CourseLessonService {
    CourseRepository courseRepository;
    LessonRepository lessonRepository;
    CourseLessonRepository courseLessonRepository;
    CourseLessonMapper courseLessonMapper;
    UserRepository userRepository;

    /*** Helper method: kiểm tra quyền instructor/admin trên course ***/
    private void checkCoursePermission(Course course) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equals("ADMIN"));
        boolean isInstructor = user.getRoles().stream().anyMatch(r -> r.getName().equals("INSTRUCTOR"));
        boolean isOwner = course.getInstructor() != null && course.getInstructor().getUsername().equals(username);
        if (!isAdmin && !(isInstructor && isOwner)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    public CourseLessonResponse addLessonToCourse(String courseId, CourseLessonRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_EXISTED));
        checkCoursePermission(course);

        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
        // Không cho phép thêm trùng bài học vào cùng một khóa học
        boolean alreadyExists = courseLessonRepository.findByCourseOrderByOrderIndexAsc(course)
                .stream().anyMatch(cl -> cl.getLesson().getId().equals(lesson.getId()));
        if (alreadyExists) throw new AppException(ErrorCode.LESSON_ALREADY_IN_COURSE);

        // Xử lý prerequisite
        CourseLesson prerequisite = null;
        if (request.getPrerequisiteCourseLessonId() != null) {
            prerequisite = courseLessonRepository.findById(request.getPrerequisiteCourseLessonId())
                    .orElseThrow(() -> new AppException(ErrorCode.PREQUISITE_NOT_FOUND));
            if (!prerequisite.getCourse().getId().equals(courseId))
                throw new AppException(ErrorCode.PREQUISITE_MUST_SAME_COURSE);
        }

        // Gán orderIndex
        Integer orderIndex = request.getOrderIndex();
        if (orderIndex == null) {
            // Nếu không truyền, mặc định cuối cùng
            int maxOrder = courseLessonRepository.findByCourseOrderByOrderIndexAsc(course)
                    .stream().mapToInt(CourseLesson::getOrderIndex).max().orElse(0);
            orderIndex = maxOrder + 1;
        }

        CourseLesson courseLesson = CourseLesson.builder()
                .course(course)
                .lesson(lesson)
                .orderIndex(orderIndex)
                .isVisible(request.getIsVisible() != null ? request.getIsVisible() : true)
                .prerequisite(prerequisite)
                .build();

        courseLesson = courseLessonRepository.save(courseLesson);
        return courseLessonMapper.toCourseLessonResponse(courseLesson);
    }

    public CourseLessonResponse updateCourseLesson(String courseId, String courseLessonId, CourseLessonUpdateRequest request) {
        CourseLesson courseLesson = courseLessonRepository.findById(courseLessonId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_LESSON_NOT_FOUND));

        checkCoursePermission(courseLesson.getCourse());
        if (!courseLesson.getCourse().getId().equals(courseId))
            throw new AppException(ErrorCode.COURSE_LESSON_COURSE_MISMATCH);

        // Update orderIndex, isVisible
        if (request.getOrderIndex() != null) {
            courseLesson.setOrderIndex(request.getOrderIndex());
        }
        if (request.getIsVisible() != null) {
            courseLesson.setVisible(request.getIsVisible());
        }

        // Update prerequisite
        if (request.getPrerequisiteCourseLessonId() != null) {
            if (request.getPrerequisiteCourseLessonId().equals(courseLessonId))
                throw new AppException(ErrorCode.PREQUISITE_CANNOT_SELF);

            CourseLesson prerequisite = courseLessonRepository.findById(request.getPrerequisiteCourseLessonId())
                    .orElseThrow(() -> new AppException(ErrorCode.PREQUISITE_NOT_FOUND));
            if (!prerequisite.getCourse().getId().equals(courseId))
                throw new AppException(ErrorCode.PREQUISITE_MUST_SAME_COURSE);

            // Kiểm tra vòng lặp prerequisite (optional)
            if (isCircularPrerequisite(courseLesson, prerequisite)) {
                throw new AppException(ErrorCode.PREQUISITE_CIRCULAR);
            }
            courseLesson.setPrerequisite(prerequisite);
        } else {
            courseLesson.setPrerequisite(null);
        }

        courseLesson = courseLessonRepository.save(courseLesson);
        return courseLessonMapper.toCourseLessonResponse(courseLesson);
    }

    public void removeLessonFromCourse(String courseId, String courseLessonId) {
        CourseLesson courseLesson = courseLessonRepository.findById(courseLessonId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_LESSON_NOT_FOUND));

        checkCoursePermission(courseLesson.getCourse());
        if (!courseLesson.getCourse().getId().equals(courseId))
            throw new AppException(ErrorCode.COURSE_LESSON_COURSE_MISMATCH);

        // Nếu có bài học nào khác phụ thuộc courseLesson này, không cho xóa (optional)
        boolean hasDependent = courseLessonRepository.findByCourseOrderByOrderIndexAsc(courseLesson.getCourse())
                .stream()
                .anyMatch(cl -> cl.getPrerequisite() != null && cl.getPrerequisite().getId().equals(courseLessonId));
        if (hasDependent) throw new AppException(ErrorCode.COURSE_LESSON_HAS_DEPENDENT);

        courseLessonRepository.deleteById(courseLessonId);
    }

    public Page<CourseLessonResponse> getLessonsOfCourse(String courseId, CourseLessonFilterRequest filter, Pageable pageable) {
        return courseLessonRepository.findAll(
                CourseLessonSpecification.withFilter(courseId, filter), pageable
        ).map(courseLessonMapper::toCourseLessonResponse);
    }

    /*** Hàm kiểm tra vòng lặp prerequisite ***/
    private boolean isCircularPrerequisite(CourseLesson current, CourseLesson prerequisite) {
        CourseLesson cursor = prerequisite;
        while (cursor != null) {
            if (cursor.getId().equals(current.getId())) return true;
            cursor = cursor.getPrerequisite();
        }
        return false;
    }
}
