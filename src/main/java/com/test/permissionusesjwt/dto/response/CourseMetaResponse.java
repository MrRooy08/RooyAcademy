package com.test.permissionusesjwt.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.test.permissionusesjwt.dto.request.CourseMetaItem;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseMetaResponse {
    Map<String, List<CourseMetaItem>> meta;
}