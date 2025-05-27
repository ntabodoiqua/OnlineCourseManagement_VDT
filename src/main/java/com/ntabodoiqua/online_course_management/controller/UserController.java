package com.ntabodoiqua.online_course_management.controller;

import com.ntabodoiqua.online_course_management.dto.request.ApiResponse;
import com.ntabodoiqua.online_course_management.dto.request.user.UserChangePasswordRequest;
import com.ntabodoiqua.online_course_management.dto.request.user.UserCreationRequest;
import com.ntabodoiqua.online_course_management.dto.response.user.UserResponse;
import com.ntabodoiqua.online_course_management.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;

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
}
