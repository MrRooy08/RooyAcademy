package com.test.permissionusesjwt.dto.request;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrollmentRequest {

    String userId;
    String courseId;
    LocalDateTime enrolled_at;

}
