package com.test.permissionusesjwt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentSummaryResponse {
    private String assignmentId;
    private String assignmentName;
    private String sectionName;
    private int assignmentIndex;
    private int sectionIndex;
    private int totalSubmissions;
    private int evaluatedSubmissions;
    private int pendingEvaluations;
} 