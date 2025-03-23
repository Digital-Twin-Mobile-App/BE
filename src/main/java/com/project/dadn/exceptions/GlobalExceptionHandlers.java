package com.project.dadn.exceptions;

import com.project.dadn.dtos.responses.APIResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandlers {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<Void>> handleGenericException(Exception exception) {
        log.error("Unhandled exception: ", exception);

        APIResponse<Void> response = new APIResponse<>();
        response.setCode(ErrorCodes.UNCATEGORIZED_ERROR.getCode());
        response.setMessage(ErrorCodes.UNCATEGORIZED_ERROR.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<APIResponse<Void>> handleAppException(AppException exception) {
        ErrorCodes errorCode = exception.getErrorCode();
        APIResponse<Void> response = new APIResponse<>();

        response.setCode(errorCode.getCode());
        response.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<String>> handleValidationException(MethodArgumentNotValidException exception) {
        String enumKey = Objects.requireNonNullElse(
                exception.getFieldError(),
                new org.springframework.validation.FieldError("unknown", "unknown", "INVALID_KEY")
        ).getDefaultMessage();

        ErrorCodes errorCode = ErrorCodes.INVALID_KEY;
        try {
            errorCode = ErrorCodes.valueOf(enumKey);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid error code key provided: {}", enumKey);
        }

        APIResponse<String> apiResponse = new APIResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }
}
