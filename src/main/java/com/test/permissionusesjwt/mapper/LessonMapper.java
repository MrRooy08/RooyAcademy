package com.test.permissionusesjwt.mapper;

import com.test.permissionusesjwt.dto.request.LessonRequest;
import com.test.permissionusesjwt.dto.request.LessonUpdateRequest;
import com.test.permissionusesjwt.dto.response.LessonResponse;
import com.test.permissionusesjwt.entity.Lesson;
import com.test.permissionusesjwt.entity.Media;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface LessonMapper {

    @Mapping( target = "section.id",source = "section")
    Lesson toLesson(LessonRequest request);

    @Mapping(source = "id",target = "id")
    Lesson toLesson(LessonUpdateRequest request);

    @Mapping( target = "mediaList", source = "mediaList", qualifiedByName = "mapMediaListToSetString")
    @Mapping(target = "description", source = "description")
    LessonResponse toLessonResponse(Lesson lesson);

    @Named("mapMediaListToSetString")
    default Map<String, String> mapMediaListToSetString(List<Media> mediaList) {
        if (mediaList == null) return Collections.emptyMap();

        return mediaList.stream()
                .collect(Collectors.toMap(Media::getId, Media::getName));
    }

}