package com.ntabodoiqua.online_course_management.specification;

import com.ntabodoiqua.online_course_management.entity.UploadedFile;
import com.ntabodoiqua.online_course_management.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class UploadedFileSpecification {

    public static Specification<UploadedFile> withFilter(User user, String contentType, String originalFileName) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always filter by user
            predicates.add(criteriaBuilder.equal(root.get("uploadedBy"), user));

            // Filter by content type if provided
            if (StringUtils.hasText(contentType)) {
                predicates.add(criteriaBuilder.like(root.get("contentType"), contentType + "%"));
            }

            // Filter by original file name (case-insensitive) if provided
            if (StringUtils.hasText(originalFileName)) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("originalFileName")),
                        "%" + originalFileName.toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    // Admin version - can see all files, not just their own
    public static Specification<UploadedFile> withFilterForAdmin(String contentType, String originalFileName) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by content type if provided
            if (StringUtils.hasText(contentType)) {
                predicates.add(criteriaBuilder.like(root.get("contentType"), contentType + "%"));
            }

            // Filter by original file name (case-insensitive) if provided
            if (StringUtils.hasText(originalFileName)) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("originalFileName")),
                        "%" + originalFileName.toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
} 