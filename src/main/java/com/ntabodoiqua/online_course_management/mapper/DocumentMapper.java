package com.ntabodoiqua.online_course_management.mapper;

import com.ntabodoiqua.online_course_management.dto.response.document.DocumentResponse;
import com.ntabodoiqua.online_course_management.entity.CourseDocument;
import com.ntabodoiqua.online_course_management.entity.LessonDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
    
    @Mapping(source = "uploadedBy.username", target = "uploadedByUsername")
    @Mapping(target = "downloadUrl", ignore = true)
    DocumentResponse toCourseDocumentResponse(CourseDocument courseDocument);
    
    @Mapping(source = "uploadedBy.username", target = "uploadedByUsername") 
    @Mapping(target = "downloadUrl", ignore = true)
    DocumentResponse toLessonDocumentResponse(LessonDocument lessonDocument);
} 