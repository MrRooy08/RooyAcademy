package com.test.permissionusesjwt.mapper;


import com.test.permissionusesjwt.dto.request.SectionRequest;
import com.test.permissionusesjwt.dto.response.SectionResponse;
import com.test.permissionusesjwt.entity.Section;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {LessonMapper.class, AssignmentMapper.class})
public interface SectionMapper {
    @Mapping(target = "index", source = "index", qualifiedByName = "mapStringToInteger")
    Section toSection(SectionRequest sectionRequest);

    @Named("mapStringToInteger")
    default Integer mapStringToInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.err.println("Không thể chuyển đổi chuỗi thành Integer: " + value);
            return null;
        }
    }


    //target bên Lessonresponse và source lấy từ Lesson course.name
    @Mapping(target = "course",source = "course.name")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "lessons", source = "lessons")
    @Mapping(target ="assignments", source = "assignments")
    @Mapping(target = "title", source = "title")
    SectionResponse toSectionResponse(Section section);

    @Mapping(target = "name", source = "name", ignore = true)
    @Mapping(target = "index", source = "index", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSectionFromDto(SectionRequest dto, @MappingTarget Section section);
}
