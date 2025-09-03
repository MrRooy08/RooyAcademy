package com.test.permissionusesjwt.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonUpdateRequest {
    String id;
    String name;
    String description;
    String content;
    String index;
    String isPreviewable;
    List<String> mediaKept;
}
