package com.ntabodoiqua.online_course_management.controller;

import com.ntabodoiqua.online_course_management.dto.request.ApiResponse;
import com.ntabodoiqua.online_course_management.dto.request.document.DocumentUploadRequest;
import com.ntabodoiqua.online_course_management.dto.response.document.DocumentResponse;
import com.ntabodoiqua.online_course_management.service.LessonDocumentService;
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
@RequestMapping("/lessons/{lessonId}/documents")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LessonDocumentController {
    
    LessonDocumentService lessonDocumentService;

    @PostMapping("/upload")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ApiResponse<DocumentResponse> uploadDocument(
            @PathVariable String lessonId,
            @RequestPart("request") @Valid DocumentUploadRequest request,
            @RequestPart("file") MultipartFile file) {
        
        DocumentResponse response = lessonDocumentService.uploadDocument(lessonId, request, file);
        return ApiResponse.<DocumentResponse>builder()
                .message("Tài liệu đã được tải lên thành công")
                .result(response)
                .build();
    }

    @GetMapping
    public ApiResponse<List<DocumentResponse>> getLessonDocuments(@PathVariable String lessonId) {
        List<DocumentResponse> documents = lessonDocumentService.getLessonDocuments(lessonId);
        return ApiResponse.<List<DocumentResponse>>builder()
                .message("Lấy danh sách tài liệu thành công")
                .result(documents)
                .build();
    }

    @DeleteMapping("/{documentId}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ApiResponse<Void> deleteDocument(
            @PathVariable String lessonId,
            @PathVariable String documentId) {
        
        lessonDocumentService.deleteDocument(lessonId, documentId);
        return ApiResponse.<Void>builder()
                .message("Tài liệu đã được xóa thành công")
                .build();
    }

    @GetMapping("/{documentId}/download")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable String lessonId,
            @PathVariable String documentId) {
        
        Resource resource = lessonDocumentService.downloadDocument(lessonId, documentId);
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
} 