package com.devteria.identityservice.mapper;

import com.devteria.identityservice.dto.request.course.CourseCreationRequest;
import com.devteria.identityservice.dto.request.course.CourseUpdateRequest;
import com.devteria.identityservice.dto.response.course.CourseResponse;
import com.devteria.identityservice.entity.Course;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CategoryMapper.class})
public interface CourseMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "instructor", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    Course toCourse(CourseCreationRequest request);

    // Map Course entity to CourseResponse DTO
    CourseResponse toCourseResponse(Course course);

    // Cập nhật thông tin từ update request vào Course entity
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", ignore = true)
    void updateCourse(@MappingTarget Course course, CourseUpdateRequest request);
}
