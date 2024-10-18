package com.test.permissionusesjwt.dto.response;

import com.test.permissionusesjwt.entity.Course;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonResponse {
    String name;
    String content;
    String videoUrl;
    String courseName;
}
