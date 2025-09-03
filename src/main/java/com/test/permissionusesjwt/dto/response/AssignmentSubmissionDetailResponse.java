package com.test.permissionusesjwt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentSubmissionDetailResponse {
    private String assignmentId;
    private String assignmentName;
    private String sectionName;
    private int assignmentIndex;
    private int sectionIndex;
    private String description;
    private String instructions;
    private Integer estimatedTime;
    private String completedAssignmentId;
    private LocalDateTime submittedAt;
    private LocalDateTime finishedAt;
    private String status; // SUBMITTED, EVALUATED, etc.
    private List<StudentAnswerResponse> studentAnswers;
    private String overallFeedback; // Lấy từ trường feedback của CompletedAssignment
    private int totalQuestions;
    private int answeredQuestions;
}