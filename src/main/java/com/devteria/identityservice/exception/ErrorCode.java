package com.devteria.identityservice.exception;

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
    PHONE_EXISTED(1013, "Phone number existed", HttpStatus.BAD_REQUEST),

    // Lỗi xác thực
    UNAUTHENTICATED(1014, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1015, "You do not have permission", HttpStatus.FORBIDDEN),
    OLD_PASSWORD_FALSE(1016, "Old password is false", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_SAME_AS_OLD(1017, "New password must be different from old password", HttpStatus.BAD_REQUEST)
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
