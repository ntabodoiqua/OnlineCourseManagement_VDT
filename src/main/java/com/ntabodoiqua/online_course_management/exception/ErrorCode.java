package com.ntabodoiqua.online_course_management.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),

    // Lỗi User
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USERNAME_REQUIRED(1004, "Username must not be blank", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1005, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED(1006, "Password must not be blank", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1007, "User not existed", HttpStatus.NOT_FOUND),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(1009, "Email is invalid", HttpStatus.BAD_REQUEST),
    INVALID_PHONE(1010, "Phone number is invalid", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1011, "Email existed", HttpStatus.BAD_REQUEST),
    PHONE_EXISTED(1012, "Phone number existed", HttpStatus.BAD_REQUEST),
    USER_ALREADY_ENABLED(1013, "User is already enabled", HttpStatus.BAD_REQUEST),
    USER_ALREADY_DISABLED(1014, "User is already disabled", HttpStatus.BAD_REQUEST),
    // Lỗi xác thực
    UNAUTHENTICATED(1015, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1016, "You do not have permission", HttpStatus.FORBIDDEN),
    OLD_PASSWORD_FALSE(1017, "Old password is false", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_SAME_AS_OLD(1018, "New password must be different from old password", HttpStatus.BAD_REQUEST),

    // Lỗi khóa học
    CATEGORY_NAME_REQUIRED(1019, "Category name must not be blank", HttpStatus.BAD_REQUEST),
    CATEGORY_EXISTED(1020, "Category existed", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_EXISTED(1021, "Category not existed", HttpStatus.NOT_FOUND),
    COURSE_EXISTED(1022, "Course existed", HttpStatus.BAD_REQUEST),
    COURSE_NOT_EXISTED(1023, "Course not existed", HttpStatus.NOT_FOUND),

    // Lỗi file
    FILE_CANNOT_STORED(1024, "File cannot be stored", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_NOT_FOUND(1025, "File not found", HttpStatus.NOT_FOUND),
    INVALID_IMAGE_TYPE(1026, "Invalid image type", HttpStatus.BAD_REQUEST),


    // Lỗi đăng ký khóa học
    COURSE_NOT_AVAILABLE(1027, "Course is not available", HttpStatus.BAD_REQUEST),
    ALREADY_ENROLLED(1028, "You are already enrolled in this course", HttpStatus.BAD_REQUEST),
    ENROLLMENT_NOT_EXISTED(1029, "Enrollment not existed", HttpStatus.NOT_FOUND),
    CANNOT_CANCEL_COMPLETED_ENROLLMENT(1030, "Cannot cancel completed enrollment", HttpStatus.BAD_REQUEST),
    ENROLLMENT_ALREADY_PROCESSED(1031, "Enrollment has already been processed", HttpStatus.BAD_REQUEST),

    // Lỗi bài học
    LESSON_NOT_FOUND(1032, "Lesson not found", HttpStatus.NOT_FOUND),
    LESSON_IS_USED_IN_COURSE(1033, "Lesson is used in course", HttpStatus.BAD_REQUEST),

    // Lỗi bài học trong khóa học
    LESSON_ALREADY_IN_COURSE(1034, "Lesson already in course", HttpStatus.BAD_REQUEST),
    PREQUISITE_NOT_FOUND(1035, "Prerequisite lesson not found", HttpStatus.NOT_FOUND),
    PREQUISITE_MUST_SAME_COURSE(1036, "Prerequisite lesson must be in the same course", HttpStatus.BAD_REQUEST),
    COURSE_LESSON_NOT_FOUND(1037, "Course lesson not found", HttpStatus.NOT_FOUND),
    COURSE_LESSON_COURSE_MISMATCH(1038, "Course lesson does not belong to the specified course", HttpStatus.BAD_REQUEST),
    PREQUISITE_CANNOT_SELF(1039, "Prerequisite cannot be the same as the lesson itself", HttpStatus.BAD_REQUEST),
    PREQUISITE_CIRCULAR(1040, "Circular prerequisite detected", HttpStatus.BAD_REQUEST),
    COURSE_LESSON_HAS_DEPENDENT(1041, "Course lesson has dependent lessons", HttpStatus.BAD_REQUEST),

    USER_DISABLED(1042, "User is disabled", HttpStatus.BAD_REQUEST),
    USER_DISABLED_DUE_TO_TOO_MANY_ATTEMPTS(1043, "User is disabled due to too many login attempts", HttpStatus.BAD_REQUEST),
    TOO_MANY_ATTEMPTS(1044, "Too many login attempts, please try again later", HttpStatus.BAD_REQUEST),
    INVALID_RECAPTCHA(1045, "Invalid reCAPTCHA", HttpStatus.BAD_REQUEST),

    // Lỗi đánh giá khóa học
    CANNOT_REVIEW_UNCOMPLETED_COURSE(1046, "Cannot review course before completion", HttpStatus.BAD_REQUEST),
    ALREADY_REVIEWED(1047, "You have already reviewed this course", HttpStatus.BAD_REQUEST),

    ;





    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
