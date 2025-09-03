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
public class CompletedAssignmentWithAnswersResponse {
    private String id;
    private String assignmentId;
    private String assignmentName;
    private String assignmentDescription;
    private String sectionName;
    private String progressId;
    private LocalDateTime finishedAt;
    private String feedback;
    private String status;
    private List<StudentAnswerResponse> studentAnswers;
    private java.sql.Timestamp createdAt;
    private java.sql.Timestamp updatedAt;
} 