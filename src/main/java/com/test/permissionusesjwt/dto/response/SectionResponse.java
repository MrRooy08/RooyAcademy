package com.test.permissionusesjwt.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SectionResponse {
    String id;
    String name;
    String description;
    String index;
    String course;
    String title;
    List<LessonResponse> lessons;
    List<AssignmentResponse> assignments;
}
