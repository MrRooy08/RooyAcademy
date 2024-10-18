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

    COURSE_NOT_EXISTED(1012, "Course not existed",HttpStatus.NOT_FOUND),

    LEVEL_NOT_EXISTED(1011, "Level not existed",HttpStatus.NOT_FOUND),

    LEVEL_EXISTED (1010,"Level existed ",HttpStatus.BAD_REQUEST),

    LESSON_NOT_EXISTED (1013,"Lesson not existed ",HttpStatus.BAD_REQUEST),

    LESSON_EXISTED (1013,"Lesson existed ",HttpStatus.BAD_REQUEST),
    //401 là unauthorized
    UNAUTHENTICATED(1006,"Unauthenticated ",HttpStatus.UNAUTHORIZED),

    //403 là forbidden
    UNAUTHORIZED(1007,"You do not have permission  ",HttpStatus.FORBIDDEN),

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
