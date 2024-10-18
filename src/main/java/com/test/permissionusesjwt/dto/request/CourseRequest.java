package com.test.permissionusesjwt.dto.request;

import com.test.permissionusesjwt.entity.Level;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseRequest {
    String name;
    String description;
    BigDecimal price;
    String levelCourse;
    Set<String> lessonName;
}
