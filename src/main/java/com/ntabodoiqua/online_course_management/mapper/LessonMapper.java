package com.ntabodoiqua.online_course_management.mapper;


import com.ntabodoiqua.online_course_management.dto.request.lesson.LessonRequest;
import com.ntabodoiqua.online_course_management.dto.response.lesson.LessonResponse;
import com.ntabodoiqua.online_course_management.entity.Lesson;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface LessonMapper {
    Lesson toLesson(LessonRequest request);

    @Mapping(source = "createdBy", target = "createdBy")
    LessonResponse toLessonResponse(Lesson lesson);

    @Mapping(source = "lesson.createdBy", target = "createdBy")
    @Mapping(source = "courseCount", target = "courseCount")
    LessonResponse toLessonResponseWithCourseCount(Lesson lesson, Integer courseCount);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateLessonFromRequest(LessonRequest request, @MappingTarget Lesson lesson);
}
