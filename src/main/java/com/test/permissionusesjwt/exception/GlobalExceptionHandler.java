package com.test.permissionusesjwt.exception;

import com.test.permissionusesjwt.dto.request.ApiResponse;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.Objects;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String MIN_ATTRIBUTE = "min";
    // này là lỗi về thêm,xoá,sửa sẽ trả về 500
//    @ExceptionHandler(value = RuntimeException.class)
//    ResponseEntity<String> handlingRuntimeException(RuntimeException e) {
//        return ResponseEntity.badRequest().body(e.getMessage());
//    }

    //Ap dung chuẩn apiResponse
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException e) {
        log.error("Exception", e);
        // đây là cách sẽ trả về cac response còn lại
        // throw new RuntimeException(ErrorCode.USER_EXISTED);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException e) {
        ErrorCode errorCode = e.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(apiResponse);
    }

//    @ExceptionHandler(value = MethodArgumentNotValidException.class)
//    ResponseEntity<String> handlingNotValidException(MethodArgumentNotValidException e) {
//        return ResponseEntity.badRequest().body(Objects.requireNonNull(e.getFieldError()).getDefaultMessage());
//    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handlingNotValidException(MethodArgumentNotValidException e) {
        // Lấy message từ annotation validation
        String enumKey = Objects.requireNonNull(e.getFieldError()).getDefaultMessage();
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        Map<String, Object> attributes = null;

        try {
            // Kiểm tra nếu message trong annotation trùng với enum ErrorCode
            errorCode = ErrorCode.valueOf(enumKey);

            var constraintViolation = e.getBindingResult()
                    .getAllErrors()
                    .stream()
                    .findFirst()
                    .map(error -> error.unwrap(ConstraintViolation.class))
                    .orElse(null);

            if (constraintViolation != null) {
                attributes = constraintViolation.getConstraintDescriptor().getAttributes();
                log.info("Validation attributes: {}", attributes);
            }

        } catch (IllegalArgumentException ex) {
            log.warn("Validation message '{}' không khớp với ErrorCode enum, dùng mặc định INVALID_KEY", enumKey);
        }

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(
                Objects.nonNull(attributes)
                        ? mapAttribute(errorCode.getMessage(), attributes)
                        : errorCode.getMessage()
        );

        return ResponseEntity.badRequest().body(apiResponse);
    }


//    @ExceptionHandler(value = AccessDeniedException.class)
//    ResponseEntity<ApiResponse> handlingAccessDeniedException(AccessDeniedException e) {
//        log.error("Đây là lỗi 403: " + e.getMessage());
//        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
//        return ResponseEntity.status(errorCode.getStatusCode()).body(
//                ApiResponse.builder()
//                        .code(errorCode.getCode())
//                        .message(errorCode.getMessage())
//                        .build()
//        );
//    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException e) {
        String errorMessage = "Đây là lỗi 403: " + e.getMessage();
        log.error(errorMessage);
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorMessage); // Gửi thông điệp lỗi
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    //thay đổi gtri min cua dob theo annotation size va co thuoc tinh min
    private String mapAttribute (String message, Map<String,Object> attributes)
    {
        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));

        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    }
}
