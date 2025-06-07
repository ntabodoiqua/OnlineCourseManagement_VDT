package com.ntabodoiqua.online_course_management.service.file;

import com.ntabodoiqua.online_course_management.entity.UploadedFile;
import com.ntabodoiqua.online_course_management.entity.User;
import com.ntabodoiqua.online_course_management.entity.Course;
import com.ntabodoiqua.online_course_management.entity.CourseDocument;
import com.ntabodoiqua.online_course_management.entity.LessonDocument;
import com.ntabodoiqua.online_course_management.exception.AppException;
import com.ntabodoiqua.online_course_management.exception.ErrorCode;
import com.ntabodoiqua.online_course_management.repository.UploadedFileRepository;
import com.ntabodoiqua.online_course_management.repository.UserRepository;
import com.ntabodoiqua.online_course_management.repository.CourseDocumentRepository;
import com.ntabodoiqua.online_course_management.repository.LessonDocumentRepository;
import com.ntabodoiqua.online_course_management.repository.CourseRepository;
import com.ntabodoiqua.online_course_management.specification.UploadedFileSpecification;
import com.ntabodoiqua.online_course_management.dto.response.document.FileUsageResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileStorageService {
    UserRepository userRepository;
    UploadedFileRepository uploadedFileRepository;
    CourseDocumentRepository courseDocumentRepository;
    LessonDocumentRepository lessonDocumentRepository;
    CourseRepository courseRepository;
    FileStorageProperties properties;

    public String storeFile(MultipartFile file, boolean isPublic) {
        try {
            String baseDir = isPublic ? properties.getPublicDir() : properties.getPrivateDir();
            Path dir = Paths.get(baseDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            String originalFileName = file.getOriginalFilename();
            String sanitizedFileName = originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
            String fileName = UUID.randomUUID() + "_" + sanitizedFileName;
            Path targetLocation = dir.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("File stored successfully: {}", fileName);
            // Lưu thông tin file vào cơ sở dữ liệu
            UploadedFile uploadedFile = UploadedFile.builder()
                    .fileName(fileName)
                    .originalFileName(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
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

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        // Allow admin to make any file public, others can only modify their own files
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));
        
        if (!isAdmin && !uploadedFile.getUploadedBy().getUsername().equals(currentUsername)) {
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

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        // Allow admin to make any file private, others can only modify their own files
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));
        
        if (!isAdmin && !uploadedFile.getUploadedBy().getUsername().equals(currentUsername)) {
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

    // Helper method to delete only physical file without touching database records
    // Used internally by document services to avoid circular dependencies
    public void deletePhysicalFile(String fileName, boolean isPublic) throws IOException {
        String baseDir = isPublic ? properties.getPublicDir() : properties.getPrivateDir();
        Path filePath = Paths.get(baseDir).resolve(fileName).normalize();
        Files.deleteIfExists(filePath);
        log.info("Physical file deleted: {}", fileName);
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    @Transactional
    public void deleteFile(String fileName) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        UploadedFile uploadedFile = uploadedFileRepository.findByFileName(fileName)
                .orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_FOUND));

        // Allow admin to delete any file, others can only delete their own files
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));
        
        if (!isAdmin && !uploadedFile.getUploadedBy().equals(user)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        try {
            // 1. Delete all course documents that reference this file
            List<CourseDocument> courseDocuments = courseDocumentRepository.findByFileName(fileName);
            if (!courseDocuments.isEmpty()) {
                courseDocumentRepository.deleteAll(courseDocuments);
                log.info("Deleted {} course documents referencing file: {}", courseDocuments.size(), fileName);
            }

            // 2. Delete all lesson documents that reference this file
            List<LessonDocument> lessonDocuments = lessonDocumentRepository.findByFileName(fileName);
            if (!lessonDocuments.isEmpty()) {
                lessonDocumentRepository.deleteAll(lessonDocuments);
                log.info("Deleted {} lesson documents referencing file: {}", lessonDocuments.size(), fileName);
            }

            // 3. Clear avatar URLs that reference this file
            String[] possibleAvatarPaths = {
                "/uploads/public/" + fileName,
                "/uploads/private/" + fileName,
                fileName, // Direct filename
                "uploads/public/" + fileName, // Without leading slash
                "uploads/private/" + fileName // Without leading slash
            };
            
            for (String avatarPath : possibleAvatarPaths) {
                List<User> usersWithAvatar = userRepository.findByAvatarUrl(avatarPath);
                for (User userWithAvatar : usersWithAvatar) {
                    userWithAvatar.setAvatarUrl(null);
                    userRepository.save(userWithAvatar);
                    log.info("Cleared avatar URL for user: {}", userWithAvatar.getUsername());
                }
            }

            // 4. Clear course thumbnail URLs that reference this file
            for (String thumbnailPath : possibleAvatarPaths) {
                List<Course> coursesWithThumbnail = courseRepository.findByThumbnailUrl(thumbnailPath);
                for (Course course : coursesWithThumbnail) {
                    course.setThumbnailUrl(null);
                    courseRepository.save(course);
                    log.info("Cleared thumbnail URL for course: {}", course.getTitle());
                }
            }

            // 5. Delete the physical file
            deletePhysicalFile(fileName, uploadedFile.isPublic());

            // 6. Delete the UploadedFile record
            uploadedFileRepository.delete(uploadedFile);
            
            log.info("File deleted successfully with all references cleaned up: {}", fileName);
        } catch (IOException e) {
            log.error("Could not delete file: {}", fileName, e);
            throw new AppException(ErrorCode.FILE_DELETION_FAILED);
        }
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public Page<UploadedFile> getAllFilesOfUser(String contentType, String fileName, Pageable pageable) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Check if user is admin
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));

        if (isAdmin) {
            // Admin can see all files
            return uploadedFileRepository.findAll(
                    UploadedFileSpecification.withFilterForAdmin(contentType, fileName),
                    pageable
            );
        } else {
            // Regular users can only see their own files
            return uploadedFileRepository.findAll(
                    UploadedFileSpecification.withFilter(user, contentType, fileName),
                    pageable
            );
        }
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public FileUsageResponse checkFileUsage(String fileName) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        UploadedFile uploadedFile = uploadedFileRepository.findByFileName(fileName)
                .orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_FOUND));

        // Allow admin to check any file usage, others can only check their own files
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));
        
        if (!isAdmin && !uploadedFile.getUploadedBy().equals(user)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        List<FileUsageResponse.FileUsageDetail> usageDetails = new ArrayList<>();

        // Check course documents
        List<CourseDocument> courseDocuments = courseDocumentRepository.findByFileName(fileName);
        for (CourseDocument doc : courseDocuments) {
            usageDetails.add(FileUsageResponse.FileUsageDetail.builder()
                    .type("course")
                    .id(doc.getCourse().getId())
                    .title(doc.getCourse().getTitle())
                    .description("Tài liệu: " + doc.getTitle())
                    .build());
        }

        // Check lesson documents
        List<LessonDocument> lessonDocuments = lessonDocumentRepository.findByFileName(fileName);
        for (LessonDocument doc : lessonDocuments) {
            usageDetails.add(FileUsageResponse.FileUsageDetail.builder()
                    .type("lesson")
                    .id(doc.getLesson().getId())
                    .title(doc.getLesson().getTitle())
                    .description("Tài liệu: " + doc.getTitle())
                    .build());
        }

        // Check user avatars - check multiple possible path formats
        String[] possibleAvatarPaths = {
            "/uploads/public/" + fileName,
            "/uploads/private/" + fileName,
            fileName, // Direct filename
            "uploads/public/" + fileName, // Without leading slash
            "uploads/private/" + fileName // Without leading slash
        };
        
        for (String avatarPath : possibleAvatarPaths) {
            List<User> usersWithAvatar = userRepository.findByAvatarUrl(avatarPath);
            for (User userWithAvatar : usersWithAvatar) {
                // Avoid duplicate entries
                boolean alreadyAdded = usageDetails.stream()
                    .anyMatch(detail -> "user_avatar".equals(detail.getType()) && 
                             userWithAvatar.getId().equals(detail.getId()));
                
                if (!alreadyAdded) {
                    usageDetails.add(FileUsageResponse.FileUsageDetail.builder()
                            .type("user_avatar")
                            .id(userWithAvatar.getId())
                            .title(userWithAvatar.getUsername())
                            .description("Avatar của người dùng: " + userWithAvatar.getFirstName() + " " + userWithAvatar.getLastName())
                            .build());
                }
            }
        }

        // Check course thumbnails - check multiple possible path formats
        for (String thumbnailPath : possibleAvatarPaths) {
            List<Course> coursesWithThumbnail = courseRepository.findByThumbnailUrl(thumbnailPath);
            for (Course course : coursesWithThumbnail) {
                // Avoid duplicate entries
                boolean alreadyAdded = usageDetails.stream()
                    .anyMatch(detail -> "course_thumbnail".equals(detail.getType()) && 
                             course.getId().equals(detail.getId()));
                
                if (!alreadyAdded) {
                    usageDetails.add(FileUsageResponse.FileUsageDetail.builder()
                            .type("course_thumbnail")
                            .id(course.getId())
                            .title(course.getTitle())
                            .description("Thumbnail của khóa học")
                            .build());
                }
            }
        }

        return FileUsageResponse.builder()
                .fileName(fileName)
                .isUsed(!usageDetails.isEmpty())
                .usageDetails(usageDetails)
                .build();
    }
}
