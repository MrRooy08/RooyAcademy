package com.test.permissionusesjwt.mapper;

import com.test.permissionusesjwt.dto.request.CourseRequest;
import com.test.permissionusesjwt.dto.request.CourseUpdateRequest;
import com.test.permissionusesjwt.dto.response.CourseResponse;
import com.test.permissionusesjwt.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface CourseMapper {
    @Mapping(target = "levelCourse", ignore = true)
    Course toCourse (CourseRequest courseRequest);

    CourseResponse toCourseResponse (Course course);

    @Mapping(target = "levelCourse", ignore = true)
    void updateCourse(@MappingTarget Course course, CourseUpdateRequest request);
}
