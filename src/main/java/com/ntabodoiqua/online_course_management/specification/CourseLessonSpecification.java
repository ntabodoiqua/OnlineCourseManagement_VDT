package com.ntabodoiqua.online_course_management.specification;

import com.ntabodoiqua.online_course_management.dto.request.lesson.CourseLessonFilterRequest;
import com.ntabodoiqua.online_course_management.entity.CourseLesson;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CourseLessonSpecification {
    public static Specification<CourseLesson> withFilter(String courseId, CourseLessonFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("course").get("id"), courseId));
            if (filter.getLessonTitle() != null) {
                predicates.add(cb.like(cb.lower(root.get("lesson").get("title")), "%" + filter.getLessonTitle().toLowerCase() + "%"));
            }
            if (filter.getIsVisible() != null) {
                predicates.add(cb.equal(root.get("isVisible"), filter.getIsVisible()));
            }
            if (filter.getPrerequisiteCourseLessonId() != null) {
                predicates.add(cb.equal(root.get("prerequisite").get("id"), filter.getPrerequisiteCourseLessonId()));
            }
            if (filter.getCreatedFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("lesson").get("createdAt"), filter.getCreatedFrom()));
            }
            if (filter.getCreatedTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("lesson").get("createdAt"), filter.getCreatedTo()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
