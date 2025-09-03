package com.test.permissionusesjwt.mapper;

import com.test.permissionusesjwt.dto.response.InstructorCourseResponse;
import com.test.permissionusesjwt.entity.Instructor;
import com.test.permissionusesjwt.entity.InstructorCourse;
import com.test.permissionusesjwt.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface InstructorCourseMapper {

    @Mapping(target = "instructor", source = "instructor", qualifiedByName = "mapInstructorToString")
    @Mapping(target = "permissions", source = "permissions", qualifiedByName = "mapPermissionsToNames")
    @Mapping(target = "isOwner", source = "owner")
    @Mapping(target = "name", source = "instructor", qualifiedByName = "mapInstructorToStringName")
    @Mapping(source = "status",target = "status")
    InstructorCourseResponse toInstructorCourseResponse(InstructorCourse instructorCourse);

    @Named("mapInstructorToString")
    default String mapInstructorToString(Instructor instructor) {
        if (instructor.getId() == null || instructor.getId().isBlank()) {
            return null;
        }
        return instructor.getId();
    }

    @Named("mapInstructorToStringName")
    default String mapInstructorToStringName(Instructor instructor) {
        if (instructor.getId() == null || instructor.getId().isBlank()) {
            return null;
        }
        return instructor.getLastName();
    }

    @Named("mapPermissionsToNames")
    default Set<String> mapPermissionsToNames(Set<Permission> permissions) {
        return permissions.stream()
                .map(Permission::getId)
                .collect(Collectors.toSet());
    }

}
