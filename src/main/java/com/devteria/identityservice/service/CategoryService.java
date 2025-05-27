package com.devteria.identityservice.service;

import com.devteria.identityservice.dto.request.course.CategoryRequest;
import com.devteria.identityservice.dto.response.course.CategoryResponse;
import com.devteria.identityservice.entity.Category;
import com.devteria.identityservice.entity.User;
import com.devteria.identityservice.exception.AppException;
import com.devteria.identityservice.exception.ErrorCode;
import com.devteria.identityservice.mapper.CategoryMapper;
import com.devteria.identityservice.repository.CategoryRepository;
import com.devteria.identityservice.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CategoryService {
    // Service này sẽ chứa các phương thức để quản lý danh mục khóa học, bao gồm:
    // - Tạo danh mục mới
    // - Cập nhật danh mục
    // - Xóa danh mục
    // - Lấy danh sách danh mục

    CategoryRepository categoryRepository;
    UserRepository userRepository;
    CategoryMapper categoryMapper;

    // Service để tạo danh mục khóa học
    // Chỉ cho phép người dùng có vai trò INSTRUCTOR hoặc ADMIN mới có thể tạo danh mục
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public CategoryResponse createCategory(CategoryRequest request) {
        // Kiểm tra xem danh mục đã tồn tại chưa
        if (categoryRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }

        String instructorUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User instructor = userRepository.findByUsername(instructorUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .createdBy(instructor)
                .build();

        // Lưu danh mục vào cơ sở dữ liệu
        categoryRepository.save(category);

        return categoryMapper.toCategoryResponse(category);
    }

    // Service để cập nhật danh mục khóa học
    // Chỉ cho phép người dùng có vai trò INSTRUCTOR hoặc ADMIN mới có thể cập nhật danh mục
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public CategoryResponse updateCategory(String categoryId, CategoryRequest request) {
        // Tìm danh mục theo ID
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        // Cập nhật thông tin danh mục
        category.setName(request.getName());
        if (categoryRepository.existsByName(request.getName()) &&
                !category.getName().equalsIgnoreCase(request.getName())) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }
        category.setDescription(request.getDescription());

        // Lưu danh mục đã cập nhật vào cơ sở dữ liệu
        categoryRepository.save(category);

        return categoryMapper.toCategoryResponse(category);
    }

    // Service để xóa danh mục khóa học
    // Chỉ cho phép người dùng có vai trò INSTRUCTOR hoặc ADMIN mới có thể xóa danh mục
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public String deleteCategory(String categoryId) {
        // Tìm danh mục theo ID
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        String name = category.getName();
        // Xóa danh mục khỏi cơ sở dữ liệu
        categoryRepository.delete(category);
        log.info("Category with name {} has been deleted", name);
        return "Category '" + name + "' has been deleted successfully.";
    }

    // Service để lấy danh sách tất cả danh mục khóa học
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        // Chuyển đổi danh sách Category thành danh sách CategoryResponse
        return categories.stream()
                .map(categoryMapper::toCategoryResponse)
                .toList();
    }

    // Service để lấy danh mục khóa học theo ID
    public CategoryResponse getCategoryById(String categoryId) {
        // Tìm danh mục theo ID
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        return categoryMapper.toCategoryResponse(category);
    }

    // Service tìm danh mục khóa học theo tên
    public List<CategoryResponse> findCategoriesByName(String name) {
        // Tìm danh mục theo tên
        List<Category> categories = categoryRepository.findByNameContainingIgnoreCase(name);

        if (categories.isEmpty()) {
            throw new AppException(ErrorCode.CATEGORY_NOT_EXISTED);
        }

        return categories.stream()
                .map(categoryMapper::toCategoryResponse)
                .toList();
    }

}
