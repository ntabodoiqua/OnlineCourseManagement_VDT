package com.devteria.identityservice.controller;

import com.devteria.identityservice.dto.request.ApiResponse;
import com.devteria.identityservice.dto.request.user.UserChangePasswordRequest;
import com.devteria.identityservice.dto.request.user.UserCreationRequest;
import com.devteria.identityservice.dto.request.user.UserSearchRequest;
import com.devteria.identityservice.dto.request.user.UserUpdateRequest;
import com.devteria.identityservice.dto.response.user.UserResponse;
import com.devteria.identityservice.service.AdminService;
import com.devteria.identityservice.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/manage-users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AdminController {
    AdminService adminService;

    // Controller lấy danh sách người dùng
    @GetMapping("/get-users")
    ApiResponse<List<UserResponse>> getUsers(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        return ApiResponse.<List<UserResponse>>builder()
                .result(adminService.getUsers())
                .build();
    }

    // Controller lấy thông tin người dùng theo ID
    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId){
        return ApiResponse.<UserResponse>builder()
                .result(adminService.getUser(userId))
                .build();
    }

    // Controller xóa người dùng theo ID
    @DeleteMapping("/{userId}")
    ApiResponse<String> deleteUser(@PathVariable String userId){
        adminService.deleteUser(userId);
        return ApiResponse.<String>builder()
                .result("User has been deleted")
                .build();
    }

    // Controller cập nhật thông tin người dùng theo ID
    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request){
        return ApiResponse.<UserResponse>builder()
                .result(adminService.updateUser(userId, request))
                .build();
    }

    // Controller thay đổi mật khẩu người dùng
    @PutMapping("/{userId}/change-password")
    ApiResponse<String> changeUserPassword(@PathVariable String userId, @RequestBody @Valid UserChangePasswordRequest request) {
        var result = adminService.changeUserPassword(userId, request.getNewPassword());
        return ApiResponse.<String>builder()
                .result(result)
                .build();
    }

    // Controller tìm kiếm người dùng theo tên hoặc username
    @PostMapping("/search")
    public ApiResponse<List<UserResponse>> searchUsers(@RequestBody UserSearchRequest request) {
        return ApiResponse.<List<UserResponse>>builder()
                .result(adminService.searchUsers(request))
                .build();
    }

    // Controller enable người dùng
    @PutMapping("/{userId}/enable")
    ApiResponse<String> enableUser(@PathVariable String userId) {
        return ApiResponse.<String>builder()
                .result(adminService.enableUser(userId))
                .build();
    }
}
