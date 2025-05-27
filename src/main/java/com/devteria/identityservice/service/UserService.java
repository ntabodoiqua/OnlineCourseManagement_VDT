package com.devteria.identityservice.service;

import com.devteria.identityservice.constant.PredefinedRole;
import com.devteria.identityservice.dto.request.user.UserCreationRequest;
import com.devteria.identityservice.dto.request.user.UserUpdateRequest;
import com.devteria.identityservice.dto.response.user.UserResponse;
import com.devteria.identityservice.entity.Role;
import com.devteria.identityservice.entity.User;
import com.devteria.identityservice.exception.AppException;
import com.devteria.identityservice.exception.ErrorCode;
import com.devteria.identityservice.mapper.UserMapper;
import com.devteria.identityservice.repository.RoleRepository;
import com.devteria.identityservice.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    // Service tạo người dùng mới
    public UserResponse createUser(UserCreationRequest request){
        // Kiểm tra trùng email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        // Kiểm tra trùng số điện thoại
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }
        // Kiểm tra trùng username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        // Mã hóa mật khẩu
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Thiết lập mặc định
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Gán role mặc định
        Set<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.STUDENT_ROLE)
                .ifPresent(roles::add);
        user.setRoles(roles);

        // Lưu và trả về thông tin người dùng
        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        return userMapper.toUserResponse(user);
    }

    // Service lấy thông tin người dùng hiện tại
    public UserResponse getMyInfo(){
        // Lấy securityContext từ SecurityContextHolder
        var context = SecurityContextHolder.getContext();

        // Lấy tên người dùng từ authentication
        String name = context.getAuthentication().getName();

        // Kiểm tra xem người dùng có tồn tại không
        User user = userRepository.findByUsername(name).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }

    // Service người dùng đổi mật khẩu
    public String changeMyPassword(String oldPassword, String newPassword) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Kiểm tra mật khẩu cũ có đúng không
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new AppException(ErrorCode.OLD_PASSWORD_FALSE);
        }

        // Kiểm tra mật khẩu mới có khác mật khẩu cũ không
        if (oldPassword.equals(newPassword)) {
            throw new AppException(ErrorCode.NEW_PASSWORD_SAME_AS_OLD);
        }
        // Mã hóa mật khẩu mới
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        // Ghi log
        log.info("User {} changed password successfully", username);
        return "Password changed successfully";
    }

}
