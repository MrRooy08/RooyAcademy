package com.test.permissionusesjwt.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LessonProgressResponse {
    String lessonId;
    Integer lessonIndex;
    Boolean isCompleted;
    LocalDateTime completedAt;
}