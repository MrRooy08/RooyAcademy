package com.test.permissionusesjwt.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CompletedLessonId implements Serializable {
    String progress;
    String lesson;
}
