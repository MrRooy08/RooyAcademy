package com.test.permissionusesjwt.service;


import com.test.permissionusesjwt.dto.request.CategoryRequest;
import com.test.permissionusesjwt.dto.response.CategoryResponse;
import com.test.permissionusesjwt.dto.response.CourseResponse;
import com.test.permissionusesjwt.entity.Category;
import com.test.permissionusesjwt.exception.AppException;
import com.test.permissionusesjwt.exception.ErrorCode;
import com.test.permissionusesjwt.mapper.CategoryMapper;
import com.test.permissionusesjwt.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;
    CategoryMapper  categoryMapper;


    public List<CategoryResponse> getAllParentCategory() {
        List<Category> parentCategories = categoryRepository.findByParentIsNull();

        // Chuyển đổi danh mục cha và các danh mục con thành CategoryResponse
        return parentCategories.stream()
                .map(parentCategory -> {
                    // Map parent category
                    CategoryResponse parentResponse = categoryMapper.toCategoryResponse(parentCategory);

                    // Map các danh mục con (subcategories) nếu có
                    if (parentCategory.getSubCategories() != null) {
                        List<CategoryResponse> subCategoryResponses = parentCategory.getSubCategories()
                                .stream()
                                .map(categoryMapper::toCategoryResponse)
                                .toList();
                        parentResponse.setSubCategories(subCategoryResponses); // Gắn danh mục con vào danh mục cha
                    }
                    return parentResponse;
                })
                .toList();
    }

    public CategoryResponse handleCategoryCreation(CategoryRequest rootRequest) {
        List<Category> toSave = new ArrayList<>(); // Danh sách tạm để lưu các danh mục
        CategoryResponse response = createCategory(rootRequest, null, toSave); // Đệ quy tạo danh mục

        // Sau khi đệ quy xong, lưu toàn bộ danh mục vào cơ sở dữ liệu
        categoryRepository.saveAll(toSave);
        return response;
    }


    public CategoryResponse createCategory(CategoryRequest request, Category parent, List<Category> toSave) {
        // Chuẩn hóa tên danh mục
        String normalizedName = request.getName().replaceAll("\\s+", "");

        // Tạo danh mục mới
        Category category = new Category();
        category.setName(normalizedName);
        if( !request.getParent().isEmpty()) {
            String parentRequest = request.getParent().replaceAll("\\s+", "");
//            parent = categoryRepository.findByName(parentRequest).orElseThrow();
            category.setParent(parent); // Gán danh mục cha
        }


        // Thêm danh mục mới vào danh sách tạm
        toSave.add(category);
        for (Category cate : toSave){
            System.out.print(cate.getName());
        }

        // Xử lý các danh mục con (nếu có)
        List<CategoryResponse> subCategories = new ArrayList<>();
        if (request.getSubCategories() != null) {
            for (CategoryRequest subRequest : request.getSubCategories()) {
                subCategories.add(createCategory(subRequest, category, toSave)); // Đệ quy tạo danh mục con
            }
        }

        return new CategoryResponse(
                category.getName(),
                parent != null ? parent.getName() : null,
                subCategories.isEmpty() ? null : subCategories
        );
    }


}
