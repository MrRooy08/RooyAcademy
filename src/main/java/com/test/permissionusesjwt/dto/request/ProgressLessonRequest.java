package com.test.permissionusesjwt.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProgressLessonRequest {
    String enrollmentId;
    String lessonId;
}
