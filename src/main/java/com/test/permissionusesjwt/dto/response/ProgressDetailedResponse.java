package com.test.permissionusesjwt.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProgressDetailedResponse {
    String progressId;
    String courseId;
    String courseName;
    String profileId;
    String finishStatus;
    LocalDateTime finishedAt;
    Double completionPercentage;
    Integer completedLessons;
    Integer totalLessons;
    Integer completedAssignments;
    Integer totalAssignments;
    List<SectionProgressResponse> sectionProgress;
    LessonProgressResponse nextLesson;
    
    // Thống kê bổ sung
    LocalDateTime startedAt;
    LocalDateTime lastActivityAt;
    Integer completedSections;
    Integer totalSections;
}