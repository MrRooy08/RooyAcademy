package com.test.permissionusesjwt.mapper;

import com.test.permissionusesjwt.dto.request.AssignmentRequest;
import com.test.permissionusesjwt.dto.response.AssignmentQuestionResponse;
import com.test.permissionusesjwt.dto.response.AssignmentResponse;
import com.test.permissionusesjwt.entity.Assignment;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.stream.Collectors;

@Component
public class AssignmentMapper {

    private final AssignmentQuestionMapper assignmentQuestionMapper;

    public AssignmentMapper(AssignmentQuestionMapper assignmentQuestionMapper) {
        this.assignmentQuestionMapper = assignmentQuestionMapper;
    }

    public Assignment toAssignment(AssignmentRequest request) {
        return Assignment.builder()
                .name(request.getName())
                .description(request.getDescription())
                .instructions(request.getInstructions())
                .estimatedTime(Integer.valueOf(request.getEstimatedTime()))
                .build();
    }

    public AssignmentResponse toAssignmentResponse(Assignment assignment) {
        return AssignmentResponse.builder()
                .id(assignment.getId())
                .name(assignment.getName())
                .description(assignment.getDescription())
                .instructions(assignment.getInstructions())
                .index(assignment.getIndex())
                .estimatedTime(assignment.getEstimatedTime())
                .questions(assignment.getQuestions().stream().map(
                        assignmentQuestionMapper::toAssignmentQuestionResponse
                ).sorted(Comparator.comparingInt(AssignmentQuestionResponse::getIndex))
                        .collect(Collectors.toList()))
                .build();
    }
} 