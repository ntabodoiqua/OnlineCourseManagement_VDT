package com.ntabodoiqua.online_course_management.specification;

import com.ntabodoiqua.online_course_management.entity.CourseReview;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class CourseReviewSpecification {
    public Specification<CourseReview> findHandledReviews(String courseId, Boolean isRejected, LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("course").get("id"), courseId));
            predicates.add(criteriaBuilder.isTrue(root.get("isApproved")));

            if (isRejected != null) {
                predicates.add(criteriaBuilder.equal(root.get("isRejected"), isRejected));
            }
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("reviewDate"), startDate));
            }
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("reviewDate"), endDate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
} 