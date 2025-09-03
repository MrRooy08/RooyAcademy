package com.test.permissionusesjwt.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE)
public class AssignmentRequest {
    String name;
    String description;
    String instructions;
    String estimatedTime;
    String sectionId;

    List<QuestionRequest> questions;
}