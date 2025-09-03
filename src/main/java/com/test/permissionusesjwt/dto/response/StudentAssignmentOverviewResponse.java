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
public class StudentAssignmentOverviewResponse {
    private String courseId;
    private String courseName;
    private String studentId;
    private String studentName;
    private String studentEmail;
    private String enrollmentId;
    private LocalDateTime enrolledAt;
    private List<AssignmentSubmissionDetailResponse> assignments;
    private int totalAssignments;
    private int completedAssignments;
    private int evaluatedAssignments;
} 