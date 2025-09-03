package com.test.permissionusesjwt.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class StudentAnswerResponse {
    String questionId;
    String questionContent;
    String answerContent;
    String correctAnswerContent;
    String feedback;
    String finishStatus;
    Integer finishedTime;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
} 