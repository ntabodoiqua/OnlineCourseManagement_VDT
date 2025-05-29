package com.ntabodoiqua.online_course_management.service;

import com.ntabodoiqua.online_course_management.dto.request.course.CategoryRequest;
import com.ntabodoiqua.online_course_management.dto.request.course.CategorySearchRequest;
import com.ntabodoiqua.online_course_management.dto.response.course.CategoryResponse;
import com.ntabodoiqua.online_course_management.entity.Category;
import com.ntabodoiqua.online_course_management.entity.User;
import com.ntabodoiqua.online_course_management.exception.AppException;
import com.ntabodoiqua.online_course_management.exception.ErrorCode;
import com.ntabodoiqua.online_course_management.mapper.CategoryMapper;
import com.ntabodoiqua.online_course_management.repository.CategoryRepository;
import com.ntabodoiqua.online_course_management.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
                .createdAt(LocalDateTime.now())
                .build();

        // Lưu danh mục vào cơ sở dữ liệu
        categoryRepository.save(category);

        return categoryMapper.toCategoryResponse(category);
    }

    // Service để cập nhật danh mục khóa học
    // Chỉ cho phép người dùng có vai trò INSTRUCTOR hoặc ADMIN mới có thể cập nhật danh mục
    public CategoryResponse updateCategory(String categoryId, CategoryRequest request) {
        // Tìm danh mục theo ID
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        // Lấy thông tin người dùng hiện tại
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // Kiểm tra vai trò ADMIN
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        // Kiểm tra nếu không phải admin và cũng không phải người tạo
        boolean isOwner = category.getCreatedBy().getUsername().equals(currentUsername);
        if (!isAdmin && !isOwner) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Kiểm tra trùng tên với danh mục khác
        if (!category.getName().equalsIgnoreCase(request.getName())
                && categoryRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }

        // Cập nhật thông tin
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        categoryRepository.save(category);

        return categoryMapper.toCategoryResponse(category);
    }

    // Service để xóa danh mục khóa học
    // Chỉ cho phép người dùng có vai trò INSTRUCTOR hoặc ADMIN mới có thể xóa danh mục
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public String deleteCategory(String categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        // Kiểm tra nếu là instructor thì chỉ được xóa category mình tạo ra
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !category.getCreatedBy().getUsername().equals(currentUsername)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        categoryRepository.delete(category);
        log.info("Category with name {} has been deleted", category.getName());
        return "Category '" + category.getName() + "' has been deleted successfully.";
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

    // Service để tìm kiếm danh mục khóa học theo các tiêu chí
    public Page<CategoryResponse> searchCategories(CategorySearchRequest request) {
        Sort sort = Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Specification<Category> spec = Specification.where(null);

        if (request.getName() != null)
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + request.getName().toLowerCase() + "%"));

        if (request.getDescription() != null)
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("description")), "%" + request.getDescription().toLowerCase() + "%"));

        if (request.getCreatedBy() != null)
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("createdBy").get("username")), "%" + request.getCreatedBy().toLowerCase() + "%"));

        if (request.getFrom() != null && request.getTo() != null)
            spec = spec.and((root, query, cb) -> cb.between(root.get("createdAt"),
                    request.getFrom().atStartOfDay(),
                    request.getTo().atTime(23, 59, 59)));

        Page<Category> resultPage = categoryRepository.findAll(spec, pageable);
        return resultPage.map(categoryMapper::toCategoryResponse);
    }


}
