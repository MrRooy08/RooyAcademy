package com.test.permissionusesjwt.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompletedAssignmentResponse {
    private String id;
    private String assignmentId;
    private String assignmentName;
    private String progressId;
    private LocalDateTime finishedAt;
    private String feedback;
    private String status;
    private String submission;

    // Các trường từ StudentAnswer
    private String answerStudentContent;
    private String questionId;
    private String questionContent;
    private Timestamp createdAt;
    private Timestamp updatedAt;
} 