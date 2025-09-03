package com.test.permissionusesjwt.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults (level = AccessLevel.PRIVATE)
public class AssignmentQuestionResponse {
    String id;
    String questionContent;
    int index;
    String answerContent;
} 