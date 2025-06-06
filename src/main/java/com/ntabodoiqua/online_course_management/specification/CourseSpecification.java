package com.ntabodoiqua.online_course_management.specification;

import com.ntabodoiqua.online_course_management.dto.request.course.CourseFilterRequest;
import com.ntabodoiqua.online_course_management.entity.Course;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CourseSpecification {
    public static Specification<Course> withFilterAndPermission(CourseFilterRequest filter,
                                                                boolean canViewInactive,
                                                                String instructorUsername) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter theo các tiêu chí
            if (filter.getTitle() != null && !filter.getTitle().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")),
                        "%" + filter.getTitle().toLowerCase().trim() + "%"));
            }

            if (filter.getCategory() != null && !filter.getCategory().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("category").get("name")),
                        "%" + filter.getCategory().toLowerCase().trim() + "%"));
            }

            if (filter.getInstructorName() != null && !filter.getInstructorName().trim().isEmpty()) {
                // Tìm theo firstName, lastName, fullName hoặc username
                String searchTerm = "%" + filter.getInstructorName().toLowerCase().trim() + "%";

                Predicate firstNamePredicate = cb.like(cb.lower(root.get("instructor").get("firstName")), searchTerm);
                Predicate lastNamePredicate = cb.like(cb.lower(root.get("instructor").get("lastName")), searchTerm);
                Predicate usernamePredicate = cb.like(cb.lower(root.get("instructor").get("username")), searchTerm);
                Predicate fullNamePredicate = cb.like(cb.lower(
                                cb.concat(cb.concat(root.get("instructor").get("firstName"), " "),
                                        root.get("instructor").get("lastName"))),
                        searchTerm);

                predicates.add(cb.or(firstNamePredicate, lastNamePredicate, fullNamePredicate, usernamePredicate));
            }

            if (filter.getIsActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), filter.getIsActive()));
            }

            if (filter.getCreatedFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getCreatedFrom()));
            }

            if (filter.getCreatedTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.getCreatedTo()));
            }

            if (filter.getStartDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), filter.getStartDateFrom()));
            }

            if (filter.getStartDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startDate"), filter.getStartDateTo()));
            }

            // Phân quyền xem khóa học
            if (!canViewInactive) {
                if (instructorUsername != null) {
                    // Instructor: xem khóa học active + khóa học của mình (kể cả inactive)
                    Predicate activeCourses = cb.equal(root.get("isActive"), true);
                    Predicate ownCourses = cb.equal(root.get("instructor").get("username"), instructorUsername);
                    predicates.add(cb.or(activeCourses, ownCourses));
                } else {
                    // Student/Guest: chỉ xem khóa học active
                    predicates.add(cb.equal(root.get("isActive"), true));
                }
            }
            // Admin: không thêm điều kiện gì (xem tất cả)

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
