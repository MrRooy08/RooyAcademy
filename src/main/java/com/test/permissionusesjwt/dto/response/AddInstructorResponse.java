package com.test.permissionusesjwt.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddInstructorResponse {
    String instructorId;
    String courseId;
    String status;
    String time;
    @Builder.Default
    Set<String> permissions = new HashSet<>();
}
