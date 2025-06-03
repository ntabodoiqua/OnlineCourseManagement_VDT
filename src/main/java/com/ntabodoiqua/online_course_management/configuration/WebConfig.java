package com.ntabodoiqua.online_course_management.configuration;

import com.ntabodoiqua.online_course_management.interceptor.UserStatusInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final UserStatusInterceptor userStatusInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map URL /uploads/** tới thư mục uploads/ ở ngoài root project
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/"); // Có thể dùng đường dẫn tuyệt đối nếu muốn
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173/") // Địa chỉ frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userStatusInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/token",
                        "/auth/introspect",
                        "/auth/logout",
                        "/auth/refresh",
                        "/users",
                        "/category/get-categories",
                        "/category",
                        "/uploads/public/**",
                        "/courses/**",
                        "/lessons",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html"
                );
    }
}
