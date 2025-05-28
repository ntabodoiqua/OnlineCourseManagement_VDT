package com.ntabodoiqua.online_course_management.repository;

import com.ntabodoiqua.online_course_management.entity.UploadedFile;
import com.ntabodoiqua.online_course_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, String> {
    // JpaRepository provides basic CRUD operations
    // Additional custom methods can be defined here if needed
    Optional<UploadedFile> findByFileName(String fileName);
    List<UploadedFile> findByUploadedByAndIsPublicTrue(User uploadedBy);
}
