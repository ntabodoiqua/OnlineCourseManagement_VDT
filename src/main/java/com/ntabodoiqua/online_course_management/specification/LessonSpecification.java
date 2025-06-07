package com.ntabodoiqua.online_course_management.specification;

import com.ntabodoiqua.online_course_management.dto.request.lesson.LessonFilterRequest;
import com.ntabodoiqua.online_course_management.entity.Lesson;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class LessonSpecification {
    public static Specification<Lesson> withFilter(LessonFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getTitle() != null) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + filter.getTitle().toLowerCase() + "%"));
            }
//            if (filter.getContentKeyword() != null) {
//                predicates.add(cb.like(cb.lower(root.get("content")), "%" + filter.getContentKeyword().toLowerCase() + "%"));
//            }
            if (filter.getCreatedBy() != null) {
                predicates.add(cb.equal(root.get("createdBy").get("username"), filter.getCreatedBy()));
            }
            if (filter.getCreatedFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getCreatedFrom()));
            }
            if (filter.getCreatedTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.getCreatedTo()));
            }
            if (filter.getUpdatedFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("updatedAt"), filter.getUpdatedFrom()));
            }
            if (filter.getUpdatedTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("updatedAt"), filter.getUpdatedTo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
