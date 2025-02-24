package com.test.permissionusesjwt.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseRequest {
    String name;
    String description;
    BigDecimal price;
    String imageUrl;
    String videoUrl;
    String levelCourse;

}
