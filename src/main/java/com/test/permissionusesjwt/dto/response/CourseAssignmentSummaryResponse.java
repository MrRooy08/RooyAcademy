package com.test.permissionusesjwt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseAssignmentSummaryResponse {
    private String courseId;
    private String courseName;
    private int totalStudents;
    private int totalAssignments;
    private List<StudentAssignmentOverviewResponse> studentSubmissions;
    private List<AssignmentSummaryResponse> assignmentSummaries;
} 