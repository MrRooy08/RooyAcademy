package com.test.permissionusesjwt.dto.request;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseUpdateRequest {
    String name;
    String description;
    String subtitle;
    String category;
    String price;
    String topic;
    String levelCourse;
}
