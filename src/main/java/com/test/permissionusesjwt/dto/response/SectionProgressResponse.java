package com.test.permissionusesjwt.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SectionProgressResponse {
    String sectionId;
    Integer sectionIndex;
    Integer completedLessons;
    Integer totalLessons;
    Integer completedAssignments;
    Integer totalAssignments;
    Double completionPercentage;
    Boolean isCompleted;
    List<LessonProgressResponse> lessons;
    List<AssignmentProgressResponse> assignments;
}