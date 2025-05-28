package com.ntabodoiqua.online_course_management.controller;

import com.ntabodoiqua.online_course_management.dto.request.ApiResponse;
import com.ntabodoiqua.online_course_management.entity.UploadedFile;
import com.ntabodoiqua.online_course_management.entity.User;
import com.ntabodoiqua.online_course_management.exception.AppException;
import com.ntabodoiqua.online_course_management.exception.ErrorCode;
import com.ntabodoiqua.online_course_management.repository.UploadedFileRepository;
import com.ntabodoiqua.online_course_management.repository.UserRepository;
import com.ntabodoiqua.online_course_management.service.UserService;
import com.ntabodoiqua.online_course_management.service.file.FileStorageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FileController {
    UploadedFileRepository uploadedFileRepository;
    UserRepository userRepository;
    FileStorageService fileStorageService;
    UserService userService;

    @PostMapping("/upload")
    public ApiResponse<String> upload(@RequestParam("file") MultipartFile file,
                                      @RequestParam boolean isPublic) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User uploader = userRepository.findByUsername(username).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        String fileName = fileStorageService.storeFile(file, isPublic);

        UploadedFile uploadedFile = UploadedFile.builder()
                .fileName(fileName)
                .originalFileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .isPublic(isPublic)
                .uploadedAt(LocalDateTime.now())
                .uploadedBy(uploader)
                .build();

        // Lưu vào csdl
        uploadedFileRepository.save(uploadedFile);
        log.info("File uploaded: {}", fileName);
        return ApiResponse.<String>builder()
                .result("File uploaded successfully: " + fileName)
                .build();
    }

//    @GetMapping("/download/{fileName}")
//    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
//        UploadedFile file = uploadedFileRepository.findByFileName(fileName)
//                .orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_FOUND));
//
//        if (!file.isPublic()) {
//            // kiểm tra quyền sở hữu hoặc tham gia khóa học
//            String username = SecurityContextHolder.getContext().getAuthentication().getName();
//            boolean hasAccess = file.getUploadedBy().getUsername().equals(username)
//                    || userService.isEnrolled(username, file.getCourse().getId());
//
//            if (!hasAccess) throw new AppException(ErrorCode.ACCESS_DENIED);
//        }
//
//        Resource resource = fileStorageService.loadFile(fileName, file.isPublic());
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(file.getContentType()))
//                .body(resource);
//    }

    // Controller chuyển file từ private sang public
    @PutMapping("/make-public/{fileName}")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public ApiResponse<String> makeFilePublic(@PathVariable String fileName) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String result = fileStorageService.makeFilePublic(fileName, username);

        return ApiResponse.<String>builder()
                .result(result)
                .build();
    }

    // Controller lấy tất cả ảnh công khai của người dùng
    @GetMapping("/all-images-of-user")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public ApiResponse<List<String>> getAllImagesOfUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<String> imageUrls = fileStorageService.getAllPublicImagesOfUser(username);

        return ApiResponse.<List<String>>builder()
                .message("Public images fetched successfully")
                .result(imageUrls)
                .build();
    }
}
