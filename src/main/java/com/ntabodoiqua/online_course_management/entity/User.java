package com.ntabodoiqua.online_course_management.entity;

import com.ntabodoiqua.online_course_management.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "username", unique = true)
    String username;
    String password;
    String firstName;
    LocalDate dob;
    String lastName;
    // Đường dẫn ảnh đại diện
    String avatarUrl;

    @Column(unique = true)
    String email;

    @Column(unique = true)
    String phone;

    boolean enabled; // Trạng thái tài khoản (đã kích hoạt hay chưa)

    LocalDateTime createdAt; // Ngày tạo tài khoản
    LocalDateTime updatedAt; // Ngày cập nhật tài khoản

    @Enumerated(EnumType.STRING)
    Gender gender;

    String bio; // Tiểu sử ngắn gọn về người dùng

    @ManyToMany
    Set<Role> roles;

    @Column(nullable = false)
    int loginFailCount = 0;
}
