package com.test.permissionusesjwt.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.test.permissionusesjwt.entity.Progress;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProgressResponse {
    String id;
    String courseId;
    String profileId;
    String finishStatus;
    LocalDateTime finishedAt;
    Double completionPercentage;
    Integer completedLessons;
    Integer totalLessons;
    Integer completedAssignments;
    Integer totalAssignments;

    public static ProgressResponse from(Progress progress) {
        return ProgressResponse.builder()
                .id(progress.getId())
                .courseId(progress.getEnrollment().getCourse().getId())
                .profileId(progress.getEnrollment().getProfile().getId())
                .finishStatus(progress.getFinishStatus().name())
                .finishedAt(progress.getFinishedAt())
                .build();
    }

    public static ProgressResponse from(Progress progress, int completedLessons, int totalLessons) {
        double percentage = totalLessons > 0 ? (double) completedLessons / totalLessons * 100 : 0.0;
        
        return ProgressResponse.builder()
                .id(progress.getId())
                .courseId(progress.getEnrollment().getCourse().getId())
                .profileId(progress.getEnrollment().getProfile().getId())
                .finishStatus(progress.getFinishStatus().name())
                .finishedAt(progress.getFinishedAt())
                .completionPercentage(Math.round(percentage * 100.0) / 100.0) // Làm tròn 2 chữ số thập phân
                .completedLessons(completedLessons)
                .totalLessons(totalLessons)
                .build();
    }
    
    public static ProgressResponse from(Progress progress, int completedLessons, int totalLessons, 
                                      int completedAssignments, int totalAssignments) {
        // Tính phần trăm tổng thể (bao gồm cả bài học và bài tập)
        double overallPercentage = 0.0;
        if (totalLessons > 0 || totalAssignments > 0) {
            int totalItems = totalLessons + totalAssignments;
            int completedItems = completedLessons + completedAssignments;
            overallPercentage = (double) completedItems / totalItems * 100;
        }
        
        return ProgressResponse.builder()
                .id(progress.getId())
                .courseId(progress.getEnrollment().getCourse().getId())
                .profileId(progress.getEnrollment().getProfile().getId())
                .finishStatus(progress.getFinishStatus().name())
                .finishedAt(progress.getFinishedAt())
                .completionPercentage(Math.round(overallPercentage * 100.0) / 100.0) // Phần trăm tổng thể
                .completedLessons(completedLessons)
                .totalLessons(totalLessons)
                .completedAssignments(completedAssignments)
                .totalAssignments(totalAssignments)
                .build();
    }
}
