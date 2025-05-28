package com.ntabodoiqua.online_course_management.controller;

import com.ntabodoiqua.online_course_management.dto.request.ApiResponse;
import com.ntabodoiqua.online_course_management.dto.request.user.UserChangePasswordRequest;
import com.ntabodoiqua.online_course_management.dto.request.user.UserCreationRequest;
import com.ntabodoiqua.online_course_management.dto.response.user.UserResponse;
import com.ntabodoiqua.online_course_management.entity.User;
import com.ntabodoiqua.online_course_management.exception.AppException;
import com.ntabodoiqua.online_course_management.exception.ErrorCode;
import com.ntabodoiqua.online_course_management.repository.UserRepository;
import com.ntabodoiqua.online_course_management.service.UserService;
import com.ntabodoiqua.online_course_management.service.file.FileStorageService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserRepository userRepository;
    UserService userService;
    FileStorageService fileStorageService;

    // Controller tạo người dùng mới
    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request){
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }


    // Controller lấy thông tin người dùng hiện tại
    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo(){
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    // Controller tự người dùng cập nhật mật khẩu
    @PutMapping("/change-password")
    ApiResponse<String> changeMyPassword(@RequestBody @Valid UserChangePasswordRequest request) {
        var result = userService.changeMyPassword(request.getOldPassword(), request.getNewPassword());
        return ApiResponse.<String>builder()
                .result(result)
                .build();
    }

    // Controller người dùng cập nhật avatar
    @PostMapping("/avatar")
    public ApiResponse<String> setAvatar(@RequestParam("file") MultipartFile file) {
        String avatarUrl = userService.setAvatar(file);
        return ApiResponse.<String>builder()
                .message("Avatar updated successfully")
                .result(avatarUrl)
                .build();
    }

    // Controller người dùng cập nhật avatar từ file đã upload
    @PostMapping("/avatar/from-file")
    public ApiResponse<String> setAvatarFromUploadedFile(@RequestParam("fileName") String fileName) {
        String avatarUrl = userService.setAvatarFromUploadedFile(fileName);
        return ApiResponse.<String>builder()
                .message("Avatar updated successfully from existing file")
                .result(avatarUrl)
                .build();
    }


}
