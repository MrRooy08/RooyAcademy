package com.test.permissionusesjwt.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    //lỗi k xác định được trả về 500
    UNCATEGORIZED_EXCEPTION(9999,"Uncategorized error ",HttpStatus.INTERNAL_SERVER_ERROR),

    INVALID_KEY(1001, "Uncategorized key error ",HttpStatus.BAD_REQUEST),

    USERNAME_INVALID ( 1003,"Username must be at least 3 characters ",HttpStatus.BAD_REQUEST),
    //bad request dành cho những lỗi về input output
    PASSWORD_INVALID ( 1004,"Password must be at least 6 characters ",HttpStatus.BAD_REQUEST),

    USER_EXISTED (1001,"User existed ",HttpStatus.BAD_REQUEST),

    USER_NOT_EXISTED(1005, "User not existed",HttpStatus.NOT_FOUND),

    COURSE_EXISTED (1009,"Course existed ",HttpStatus.BAD_REQUEST),

    ENROLLMENT_EXISTED (1015,"ENROLLMENT_EXISTED ",HttpStatus.BAD_REQUEST),
    
    ENROLLMENT_NOT_EXISTED(1021, "Enrollment not existed", HttpStatus.NOT_FOUND),

    COURSE_NOT_EXISTED(1012, "Course not existed",HttpStatus.NOT_FOUND),

    LEVEL_NOT_EXISTED(1011, "Level not existed",HttpStatus.NOT_FOUND),

    LEVEL_EXISTED (1010,"Level existed ",HttpStatus.BAD_REQUEST),

    LESSON_NOT_EXISTED (1013,"Lesson not existed ",HttpStatus.BAD_REQUEST),

    LESSON_EXISTED (1014,"Lesson existed ",HttpStatus.BAD_REQUEST),
    //401 là unauthorized
    UNAUTHENTICATED(1006,"Unauthenticated ",HttpStatus.UNAUTHORIZED),


    //403 là forbidden
    UNAUTHORIZED(1007,"You do not have permission  ",HttpStatus.FORBIDDEN),
    
    FORBIDDEN(1022, "Access denied", HttpStatus.FORBIDDEN),

    CART_NOT_EXISTED(1016, "Cart not existed", HttpStatus.NOT_FOUND),

    CART_ITEM_EXISTED(1017, "Cart item existed", HttpStatus.BAD_REQUEST),
    CART_ITEM_NOT_EXISTED(1018, "Cart item not existed", HttpStatus.NOT_FOUND),

    TIER_PRICE_NOT_EXISTED(1019, "Tier price not existed", HttpStatus.NOT_FOUND),

    ORDER_NOT_PAID(1020, "ORDER NOT PAID", HttpStatus.NOT_FOUND),

    // Assignment related errors
    ASSIGNMENT_NOT_FOUND(1023, "Assignment not found", HttpStatus.NOT_FOUND),
    ASSIGNMENT_ALREADY_SUBMITTED(1024, "Assignment already submitted", HttpStatus.BAD_REQUEST),
    COMPLETED_ASSIGNMENT_NOT_FOUND(1025, "Completed assignment not found", HttpStatus.NOT_FOUND),
    SECTION_NOT_FOUND(1026, "Section not found", HttpStatus.NOT_FOUND),
    PROGRESS_NOT_FOUND(1027, "Progress not found", HttpStatus.NOT_FOUND),
    INSTRUCTOR_NOT_FOUND(1028, "Instructor not found", HttpStatus.NOT_FOUND),
    
    // Question and Answer related errors
    QUESTION_NOT_FOUND(1029, "Question not found", HttpStatus.NOT_FOUND),
    ANSWER_NOT_FOUND(1030, "Answer not found", HttpStatus.NOT_FOUND),
    INVALID_QUESTION_FOR_ASSIGNMENT(1031, "Question does not belong to this assignment", HttpStatus.BAD_REQUEST),
    ASSIGNMENT_HAS_NO_QUESTIONS(1032, "Assignment has no questions", HttpStatus.BAD_REQUEST),
    NO_ANSWERS_PROVIDED(1033, "No answers provided", HttpStatus.BAD_REQUEST),
    INCOMPLETE_ANSWERS(1034, "Incomplete answers - must answer all questions", HttpStatus.BAD_REQUEST),
    EMPTY_ANSWER_CONTENT(1035, "Answer content cannot be empty", HttpStatus.BAD_REQUEST),

    CANNOT_INVITE_COURSE_OWNER(1036, "Cannot invited yourself", HttpStatus.BAD_REQUEST),
    PREVIOUS_INSTRUCTOR(1037, "\"You have previously been an instructor\"", HttpStatus.BAD_REQUEST),

    INSTRUCTOR_ALREADY_EXISTS(1038, "User is already an instructor", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(1039, "Role not found", HttpStatus.NOT_FOUND),


    INVALID_DOB(1008,"Your age must be at least {min}",HttpStatus.BAD_REQUEST);

    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = httpStatusCode;
    }
    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

}
