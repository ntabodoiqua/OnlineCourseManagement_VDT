package com.ntabodoiqua.online_course_management.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity

// Entity lưu các token đã xác nhận
public class InvalidatedToken {
    @Id
    String id;
    Date expiryDate;
}
