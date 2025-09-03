package com.test.permissionusesjwt.mapper;


import com.test.permissionusesjwt.dto.request.EnrollmentRequest;
import com.test.permissionusesjwt.dto.response.EnrollmentResponse;
import com.test.permissionusesjwt.entity.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

    @Mapping(target = "course", ignore = true)
    @Mapping(target = "profile", ignore = true)
    Enrollment toEnrollment(EnrollmentRequest request);

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "profileId", source = "profile.id")
    EnrollmentResponse toEnrollmentResponse(Enrollment response);

}
