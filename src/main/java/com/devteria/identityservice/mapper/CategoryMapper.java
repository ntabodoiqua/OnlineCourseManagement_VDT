package com.devteria.identityservice.mapper;

import com.devteria.identityservice.dto.request.course.CategoryRequest;
import com.devteria.identityservice.dto.response.course.CategoryResponse;
import com.devteria.identityservice.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    Category toCategory(CategoryRequest request);
    @Mapping(source = "createdBy.username", target = "createdByUsername")
    CategoryResponse toCategoryResponse(Category category);

}
