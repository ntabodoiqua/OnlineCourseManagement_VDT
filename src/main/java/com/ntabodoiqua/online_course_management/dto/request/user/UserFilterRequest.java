package com.ntabodoiqua.online_course_management.dto.request.user;


import com.ntabodoiqua.online_course_management.enums.Gender;
import lombok.*;
        import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserFilterRequest {
    String username;
    String firstName;
    String lastName;
    Boolean enabled;
    Gender gender;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime createdFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime createdTo;

    List<String> roles; // role name
}
