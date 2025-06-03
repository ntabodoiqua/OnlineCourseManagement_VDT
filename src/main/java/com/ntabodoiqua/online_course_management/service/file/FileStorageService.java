package com.ntabodoiqua.online_course_management.service.file;

import com.ntabodoiqua.online_course_management.entity.UploadedFile;
import com.ntabodoiqua.online_course_management.entity.User;
import com.ntabodoiqua.online_course_management.exception.AppException;
import com.ntabodoiqua.online_course_management.exception.ErrorCode;
import com.ntabodoiqua.online_course_management.repository.UploadedFileRepository;
import com.ntabodoiqua.online_course_management.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileStorageService {
    UserRepository userRepository;
    UploadedFileRepository uploadedFileRepository;
    FileStorageProperties properties;

    public String storeFile(MultipartFile file, boolean isPublic) {
        try {
            String baseDir = isPublic ? properties.getPublicDir() : properties.getPrivateDir();
            Path dir = Paths.get(baseDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path targetLocation = dir.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("File stored successfully: {}", fileName);
            // Lưu thông tin file vào cơ sở dữ liệu
            UploadedFile uploadedFile = UploadedFile.builder()
                    .fileName(fileName)
                    .originalFileName(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .isPublic(isPublic)
                    .uploadedAt(LocalDateTime.now())
                    .uploadedBy(userRepository.findByUsername(
                            SecurityContextHolder.getContext().getAuthentication().getName())
                            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)))
                    .build();
            uploadedFileRepository.save(uploadedFile);
            return fileName;
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_CANNOT_STORED);
        }
    }

    public Resource loadFile(String fileName, boolean isPublic) {
        try {
            String baseDir = isPublic ? properties.getPublicDir() : properties.getPrivateDir();
            Path filePath = Paths.get(baseDir).toAbsolutePath().normalize().resolve(fileName);
            Resource resource = new org.springframework.core.io.UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new AppException(ErrorCode.FILE_NOT_FOUND);
            }
        } catch (Exception e) {
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        }
    }

    // Service để làm cho file trở nên công khai
    public String makeFilePublic(String fileName, String currentUsername) {
        UploadedFile uploadedFile = uploadedFileRepository.findByFileName(fileName)
                .orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_FOUND));

        if (!uploadedFile.getUploadedBy().getUsername().equals(currentUsername)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (uploadedFile.isPublic()) {
            return "File is already public: " + fileName;
        }

        uploadedFile.setPublic(true);
        uploadedFileRepository.save(uploadedFile);
        return "File is now public: " + fileName;
    }

    // Service để làm cho file trở nên riêng tư
    public String makeFilePrivate(String fileName, String currentUsername) {
        UploadedFile uploadedFile = uploadedFileRepository.findByFileName(fileName)
                .orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_FOUND));

        if (!uploadedFile.getUploadedBy().getUsername().equals(currentUsername)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (!uploadedFile.isPublic()) {
            return "File is already private: " + fileName;
        }

        uploadedFile.setPublic(false);
        uploadedFileRepository.save(uploadedFile);
        return "File is now private: " + fileName;
    }

    // Service để lấy tất cả ảnh công khai của người dùng
    public List<String> getAllPublicImagesOfUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return uploadedFileRepository.findByUploadedByAndIsPublicTrue(user)
                .stream()
                .filter(file -> file.getContentType() != null && file.getContentType().startsWith("image/"))
                .map(file -> "/uploads/public/" + file.getFileName())
                .toList();
    }
}
