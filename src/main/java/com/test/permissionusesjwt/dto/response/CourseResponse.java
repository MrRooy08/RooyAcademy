package com.test.permissionusesjwt.dto.response;

import com.test.permissionusesjwt.dto.request.LevelRequest;
import com.test.permissionusesjwt.entity.Lesson;
import com.test.permissionusesjwt.entity.Level;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseResponse {
    String name;
    String description;
    BigDecimal price;
    String imageUrl;
    String videoUrl;
    LevelRequest levelCourse;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
