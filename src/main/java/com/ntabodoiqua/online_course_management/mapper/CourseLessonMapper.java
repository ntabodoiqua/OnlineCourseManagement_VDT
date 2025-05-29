package com.ntabodoiqua.online_course_management.mapper;

import com.ntabodoiqua.online_course_management.dto.response.course.CourseLessonResponse;
import com.ntabodoiqua.online_course_management.entity.CourseLesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {LessonMapper.class})
public interface CourseLessonMapper {
    @Mapping(source = "lesson", target = "lesson")
    @Mapping(source = "prerequisite.id", target = "prerequisiteCourseLessonId")
    @Mapping(source = "prerequisite.lesson.title", target = "prerequisiteLessonTitle")
    CourseLessonResponse toCourseLessonResponse(CourseLesson entity);
}
