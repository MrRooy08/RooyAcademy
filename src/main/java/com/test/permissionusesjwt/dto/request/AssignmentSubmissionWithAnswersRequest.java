package com.test.permissionusesjwt.dto.request;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignmentSubmissionWithAnswersRequest {
    String progressId;
    String assignmentId;
    List<QuestionRequest> questions;
    int finishedTime;
}
