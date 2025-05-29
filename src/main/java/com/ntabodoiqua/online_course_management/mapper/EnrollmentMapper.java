package com.ntabodoiqua.online_course_management.mapper;

import com.ntabodoiqua.online_course_management.dto.request.course.CategoryRequest;
import com.ntabodoiqua.online_course_management.dto.response.course.CategoryResponse;
import com.ntabodoiqua.online_course_management.dto.response.enrollment.EnrollmentResponse;
import com.ntabodoiqua.online_course_management.entity.Category;
import com.ntabodoiqua.online_course_management.entity.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CourseMapper.class, UserMapper.class})
public interface EnrollmentMapper {

    @Mapping(source = "course", target = "course")
    @Mapping(source = "student", target = "student")
    @Mapping(source = "approvalStatus", target = "approvalStatus")
    EnrollmentResponse toEnrollmentResponse(Enrollment enrollment);
}
