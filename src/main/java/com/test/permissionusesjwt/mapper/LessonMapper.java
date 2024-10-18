package com.test.permissionusesjwt.mapper;


import com.test.permissionusesjwt.dto.request.LessonRequest;
import com.test.permissionusesjwt.dto.response.LessonResponse;
import com.test.permissionusesjwt.entity.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LessonMapper {
    @Mapping(target = "course", ignore = true)
    Lesson toLesson(LessonRequest lessonRequest);

    //target bên Lessonresponse và source lấy từ Lesson course.name
    @Mapping(target = "courseName",source = "course.name")
    LessonResponse toLessonResponse(Lesson lesson);
}
