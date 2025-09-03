package com.test.permissionusesjwt.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.test.permissionusesjwt.entity.AssignmentQuestion;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults (level = AccessLevel.PRIVATE)
public class AssignmentResponse {
    String id;
    String name;
    String description;
    String instructions;
    int index;
    Integer estimatedTime;
    List<AssignmentQuestionResponse> questions;
} 