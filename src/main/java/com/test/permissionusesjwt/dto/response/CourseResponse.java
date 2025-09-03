package com.test.permissionusesjwt.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.test.permissionusesjwt.dto.request.LevelRequest;
import com.test.permissionusesjwt.enums.CourseStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseResponse {
    String id;
    String name;
    String description;
    String subtitle;
    BigDecimal price;
    String imageUrl;
    String videoUrl;
    String category;
    String parentcategory;
    String levelCourse;
    String topic;
    String isActive;
    String approveStatus;

    List<InstructorCourseResponse> instructorCourse;

    List<SectionResponse> sections;
    List<AssignmentResponse> assignments;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
