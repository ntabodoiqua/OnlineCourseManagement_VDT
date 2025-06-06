package com.ntabodoiqua.online_course_management.controller;

import com.ntabodoiqua.online_course_management.dto.request.ApiResponse;
import com.ntabodoiqua.online_course_management.dto.request.document.DocumentUploadRequest;
import com.ntabodoiqua.online_course_management.dto.response.document.DocumentResponse;
import com.ntabodoiqua.online_course_management.service.CourseDocumentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/courses/{courseId}/documents")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CourseDocumentController {
    
    CourseDocumentService courseDocumentService;

    @PostMapping("/upload")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ApiResponse<DocumentResponse> uploadDocument(
            @PathVariable String courseId,
            @RequestPart("request") @Valid DocumentUploadRequest request,
            @RequestPart("file") MultipartFile file) {
        
        DocumentResponse response = courseDocumentService.uploadDocument(courseId, request, file);
        return ApiResponse.<DocumentResponse>builder()
                .message("Tài liệu đã được tải lên thành công")
                .result(response)
                .build();
    }

    @GetMapping
    public ApiResponse<List<DocumentResponse>> getCourseDocuments(@PathVariable String courseId) {
        List<DocumentResponse> documents = courseDocumentService.getCourseDocuments(courseId);
        return ApiResponse.<List<DocumentResponse>>builder()
                .message("Lấy danh sách tài liệu thành công")
                .result(documents)
                .build();
    }

    @DeleteMapping("/{documentId}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ApiResponse<Void> deleteDocument(
            @PathVariable String courseId,
            @PathVariable String documentId) {
        
        courseDocumentService.deleteDocument(courseId, documentId);
        return ApiResponse.<Void>builder()
                .message("Tài liệu đã được xóa thành công")
                .build();
    }

    @GetMapping("/{documentId}/download")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable String courseId,
            @PathVariable String documentId) {
        
        Resource resource = courseDocumentService.downloadDocument(courseId, documentId);
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
} 