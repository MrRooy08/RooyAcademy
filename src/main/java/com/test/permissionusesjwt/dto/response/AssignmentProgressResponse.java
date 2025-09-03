package com.test.permissionusesjwt.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssignmentProgressResponse {
    String assignmentId;
    Integer assignmentIndex;
    Boolean isCompleted;
    LocalDateTime completedAt;
    List<StudentAnswerResponse> studentAnswers;
} 