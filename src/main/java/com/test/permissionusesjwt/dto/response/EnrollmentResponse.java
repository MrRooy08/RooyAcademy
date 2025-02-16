package com.test.permissionusesjwt.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrollmentResponse {

    String userId;
    String courseId;
    LocalDateTime enrolled_at;

}
