package com.test.permissionusesjwt.mapper;

import com.test.permissionusesjwt.dto.request.CourseDraftRequest;
import com.test.permissionusesjwt.dto.request.CourseRequest;
import com.test.permissionusesjwt.dto.request.CourseUpdateRequest;
import com.test.permissionusesjwt.dto.response.CourseResponse;
import com.test.permissionusesjwt.entity.*;
import com.test.permissionusesjwt.repository.TopicRepository;
import org.mapstruct.*;

import java.math.BigDecimal;


@Mapper(componentModel = "spring", uses = {InstructorCourseMapper.class, SectionMapper.class})
public interface CourseMapper {


    @Mapping(source = "price_id", target = "price.id", ignore = true)
    @Mapping (source = "levelCourse", target ="levelCourse.id")
    @Mapping(source = "category", target = "category.id")
    @Mapping(source ="topic", target = "topic.id")
    Course toCourse (CourseRequest courseRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "category", target = "category.id")
    @Mapping(target = "levelCourse", ignore = true)
    Course toCourse (CourseDraftRequest courseDraftRequest);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "category", target = "category", qualifiedByName = "mapCategoryToCategoryRequest")
    @Mapping(source = "category", target = "parentcategory", qualifiedByName = "mapCategoryToParentCategoryRequesty", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "price", target = "price", qualifiedByName = "mapPriceCourseToPriceResponse")
    @Mapping(source = "topic.name",  target = "topic")
    @Mapping(source = "id",target = "id")
    @Mapping(source = "instructorCourse", target = "instructorCourse")
    @Mapping(source = "subtitle",    target = "subtitle")
    @Mapping(source = "levelCourse", target = "levelCourse" , qualifiedByName = "mapLevelToLevelResponse")
    CourseResponse toCourseResponse(Course course);



    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "levelCourse", source = "levelCourse", ignore = true)
    @Mapping(target = "price", source = "price" ,ignore = true)
    @Mapping(source = "category", target = "category", ignore = true)
    @Mapping(source = "topic", target = "topic", ignore = true)
    void updateCourse(@MappingTarget Course course, CourseUpdateRequest request);

    @Named("mapPriceCourseToPriceResponse")
    default BigDecimal mapPriceCourseToPriceResponse (TierPrice tierPrice) {
        if (tierPrice == null) {
            return null;
        }
        BigDecimal price1 = tierPrice.getPrice();
        return price1;
    }

    @Named("mapCategoryRequestToCategory")
    default Category mapCategoryRequestToCategory(String categoryName) {
        if (categoryName == null) return null;
        Category category = new Category();
        category.setName(categoryName);
        return category;
    }

    @Named("mapCategoryToCategoryRequest")
    default String mapCategoryToCategoryRequest(Category category) {
        if (category == null) return null;
        return category.getName();
    }

    @Named("mapCategoryToParentCategoryRequesty")
    default String mapCategoryToParentCategoryRequesty(Category category) {
        if (category == null) return null;
        if (category.getParent() != null) {
            return category.getParent().getName();
        }
        return category.getName();
    }


    @Named("mapLevelToLevelResponse")
    default String mapLevelToLevelResponse(Level level) {
        if (level == null) return null;
        return level.getId();
    }



    @Named("mapLevelRequestToLevel")
    default Level mapLevelRequestToLevel(String levelName) {
        if (levelName == null) return null;
        Level level = new Level();
        level.setName(levelName);
        return level;
    }


}
