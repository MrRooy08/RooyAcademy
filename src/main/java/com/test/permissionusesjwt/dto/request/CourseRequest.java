package com.test.permissionusesjwt.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseRequest {
    String name;
    String description;
    String imageUrl;
    String videoUrl;
    String levelCourse;
    String category;
    String price_id;
    String topic;

}
