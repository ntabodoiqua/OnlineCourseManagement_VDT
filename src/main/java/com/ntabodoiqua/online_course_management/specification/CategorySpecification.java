package com.ntabodoiqua.online_course_management.specification;

import com.ntabodoiqua.online_course_management.dto.request.category.CategoryFilterRequest;
import com.ntabodoiqua.online_course_management.entity.Category;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CategorySpecification {
    public static Specification<Category> withFilter(CategoryFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter != null) {
                if (StringUtils.hasText(filter.getName())) {
                    predicates.add(cb.like(cb.lower(root.get("name")),
                            "%" + filter.getName().toLowerCase().trim() + "%"));
                }
                if (StringUtils.hasText(filter.getCreatedByUsername())) {
                    predicates.add(cb.equal(root.get("createdByUsername"), filter.getCreatedByUsername()));
                }
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
} 