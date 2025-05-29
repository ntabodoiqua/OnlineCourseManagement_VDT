package com.ntabodoiqua.online_course_management.mapper;

import com.ntabodoiqua.online_course_management.dto.request.course.CategoryRequest;
import com.ntabodoiqua.online_course_management.dto.response.course.CategoryResponse;
import com.ntabodoiqua.online_course_management.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    Category toCategory(CategoryRequest request);
    @Mapping(source = "createdBy.username", target = "createdByUsername")
    CategoryResponse toCategoryResponse(Category category);

    List<CategoryResponse> toCategoryResponseList(List<Category> categories);
}
