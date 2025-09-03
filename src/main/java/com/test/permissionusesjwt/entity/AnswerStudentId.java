package com.test.permissionusesjwt.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerStudentId implements Serializable {
    String completedAssignmentId;
    String questionId;
}

