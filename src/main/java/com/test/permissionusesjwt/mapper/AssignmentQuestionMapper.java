package com.test.permissionusesjwt.mapper;

import com.test.permissionusesjwt.dto.request.AssignmentQuestionRequest;
import com.test.permissionusesjwt.dto.response.AssignmentQuestionResponse;
import com.test.permissionusesjwt.entity.AssignmentQuestion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AssignmentQuestionMapper {
    
    public AssignmentQuestion toAssignmentQuestion(AssignmentQuestionRequest request) {
        return AssignmentQuestion.builder()
                .questionContent(request.getQuestionContent())
                .index(request.getIndex())
                .build();
    }
    
    public AssignmentQuestionResponse toAssignmentQuestionResponse(AssignmentQuestion question) {
        return AssignmentQuestionResponse.builder()
                .id(question.getId())
                .questionContent(question.getQuestionContent())
                .answerContent(question.getAnswer().getAnswerContent())
                .index(question.getIndex())
                .build();
    }
} 