package com.test.permissionusesjwt.mapper;


import com.test.permissionusesjwt.dto.request.EnrollmentRequest;
import com.test.permissionusesjwt.dto.response.EnrollmentResponse;
import com.test.permissionusesjwt.dto.response.LessonResponse;
import com.test.permissionusesjwt.entity.Enrollment;
import com.test.permissionusesjwt.entity.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

    @Mapping(target = "courseId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    Enrollment toEnrollment(EnrollmentRequest request);

    //target bên Lessonresponse và source lấy từ Lesson course.name
    @Mapping(target = "courseId",source = "courseId.name")
    @Mapping(target = "userId",source = "userId.username")
    EnrollmentResponse toEnrollmentResponse(Enrollment response);

}
