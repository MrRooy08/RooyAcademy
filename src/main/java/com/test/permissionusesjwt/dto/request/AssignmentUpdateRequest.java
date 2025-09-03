package com.test.permissionusesjwt.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignmentUpdateRequest {
    String name;
    String description;
    String instructions;
    Integer index;
    String estimatedTime;

    // Cấu trúc mới cho việc quản lý câu hỏi
    ModalRequest questions ;
}

