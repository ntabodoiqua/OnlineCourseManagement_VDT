package com.ntabodoiqua.online_course_management.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {DobValidator.class})

// Định nghĩa annotation: Giới hạn độ tuổi nhập vào
public @interface DobConstraint {
    String message() default "{Invalid date of birth}";

    int min();

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
