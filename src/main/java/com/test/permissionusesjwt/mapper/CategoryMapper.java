package com.test.permissionusesjwt.mapper;

import com.test.permissionusesjwt.dto.request.CategoryRequest;
import com.test.permissionusesjwt.dto.response.CategoryResponse;
import com.test.permissionusesjwt.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "parent", ignore = true)
    Category toCategory(CategoryRequest cateRequest);

    @Mapping(target = "parent", ignore = true)
    CategoryResponse toCategoryResponse(Category cate);

}
