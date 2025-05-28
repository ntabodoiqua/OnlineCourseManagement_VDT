package com.ntabodoiqua.online_course_management.controller;

import com.ntabodoiqua.online_course_management.dto.request.ApiResponse;
import com.ntabodoiqua.online_course_management.dto.request.course.CategoryRequest;
import com.ntabodoiqua.online_course_management.dto.response.course.CategoryResponse;
import com.ntabodoiqua.online_course_management.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CategoryController {
    CategoryService categoryService;

    // Lấy danh sách tất cả các danh mục
    @GetMapping("/get-categories")
    public ApiResponse<List<CategoryResponse>> getCategories() {
        log.info("Fetching all categories");
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ApiResponse.<List<CategoryResponse>>builder()
                .message("Categories fetched successfully")
                .result(categories)
                .build();
    }

    // Tạo danh mục mới
    @PostMapping()
    public ApiResponse<CategoryResponse> createCategory(@RequestBody @Valid CategoryRequest categoryRequest) {
        log.info("Creating new category: {}", categoryRequest.getName());
        CategoryResponse createdCategory = categoryService.createCategory(categoryRequest);
        return ApiResponse.<CategoryResponse>builder()
                .message("Category created successfully")
                .result(createdCategory)
                .build();
    }

    // Cập nhật danh mục theo ID
    @PutMapping("/{categoryId}")
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable String categoryId,
                                                        @RequestBody @Valid CategoryRequest categoryRequest) {
        log.info("Updating category with ID: {}", categoryId);
        CategoryResponse updatedCategory = categoryService.updateCategory(categoryId, categoryRequest);
        return ApiResponse.<CategoryResponse>builder()
                .message("Category updated successfully")
                .result(updatedCategory)
                .build();
    }

    // Xóa danh mục theo ID
    @DeleteMapping("/{categoryId}")
    public ApiResponse<String> deleteCategory(@PathVariable String categoryId) {
        log.info("Deleting category with ID: {}", categoryId);
        categoryService.deleteCategory(categoryId);
        return ApiResponse.<String>builder()
                .message("Category deleted successfully")
                .result("Category with ID " + categoryId + " has been deleted.")
                .build();
    }

    // Lấy thông tin danh mục theo ID
    @GetMapping("/{categoryId}")
    public ApiResponse<CategoryResponse> getCategoryById(@PathVariable String categoryId) {
        log.info("Fetching category with ID: {}", categoryId);
        CategoryResponse category = categoryService.getCategoryById(categoryId);
        return ApiResponse.<CategoryResponse>builder()
                .message("Category fetched successfully")
                .result(category)
                .build();
    }

    // Tìm kiếm danh mục theo tên
    @GetMapping("/search")
    public ApiResponse<List<CategoryResponse>> searchCategories(@RequestParam String name) {
        log.info("Searching categories with name: {}", name);
        List<CategoryResponse> categories = categoryService.findCategoriesByName(name);
        return ApiResponse.<List<CategoryResponse>>builder()
                .message("Categories fetched successfully")
                .result(categories)
                .build();
    }


}
