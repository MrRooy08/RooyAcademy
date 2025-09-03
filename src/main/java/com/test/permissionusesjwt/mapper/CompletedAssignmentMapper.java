package com.test.permissionusesjwt.mapper;

import com.test.permissionusesjwt.dto.response.CompletedAssignmentResponse;
import com.test.permissionusesjwt.entity.AnswerStudent;
import com.test.permissionusesjwt.entity.CompletedAssignment;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CompletedAssignmentMapper {

    public CompletedAssignmentResponse toCompletedAssignmentResponse(CompletedAssignment completedAssignment) {
        return CompletedAssignmentResponse.builder()
                .id(completedAssignment.getId())
                .assignmentId(completedAssignment.getAssignment().getId())
                .assignmentName(completedAssignment.getAssignment().getName())
                .progressId(completedAssignment.getProgress().getId())
                .finishedAt(completedAssignment.getFinishedAt())
                .feedback(completedAssignment.getFeedback())
                .status(String.valueOf(completedAssignment.getStatus()))
                .createdAt(completedAssignment.getCreatedAt())
                .updatedAt(completedAssignment.getUpdatedAt())
                .build();
    }
}