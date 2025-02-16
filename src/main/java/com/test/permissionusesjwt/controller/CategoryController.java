package com.test.permissionusesjwt.controller;

import com.test.permissionusesjwt.dto.request.ApiResponse;
import com.test.permissionusesjwt.dto.request.CategoryRequest;
import com.test.permissionusesjwt.dto.response.CategoryResponse;
import com.test.permissionusesjwt.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class CategoryController {
    CategoryService categoryService;
    @PostMapping ("/create-cate")
    public ApiResponse<CategoryResponse> create (@RequestBody CategoryRequest categoryRequest) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.handleCategoryCreation(categoryRequest))
                .build();
    }

    @GetMapping("/get-category")
    public ApiResponse<List<CategoryResponse>> getAllCategory ()
    {
        return ApiResponse.<List<CategoryResponse>>builder()
                .result(categoryService.getAllParentCategory())
                .build();
    }
}
