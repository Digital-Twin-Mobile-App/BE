package com.project.dadn.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCodes {
        UNCATEGORIZED_ERROR(HttpStatus.BAD_GATEWAY, "Uncategorized errors", 400),
        INVALID_PARAMS(HttpStatus.BAD_GATEWAY, "Invalid parameters", 400),
        INVALID_REQUEST(HttpStatus.BAD_GATEWAY, "Min 3 characters, max 250 characters", 400),
        INVALID_ENUM(HttpStatus.BAD_GATEWAY, "Check models list", 400),
        INVALID_ID(HttpStatus.BAD_GATEWAY, "Check id list", 400),
        INVALID_BUYDATE(HttpStatus.BAD_GATEWAY, "Error Date", 400),
        INVALID_KEY(HttpStatus.BAD_GATEWAY, "Invalid key", 400),
        UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "Unauthenticated", 401),
        USER_NOT_EXISTED(HttpStatus.NOT_FOUND, "User does not exist", 404),
        USER_EXISTED(HttpStatus.CONFLICT, "User already exists", 409),
        INVALID_USERNAME(HttpStatus.BAD_REQUEST, "Invalid username, min 3 words", 400),
        INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "Invalid password, min 8 words", 400),
        ROLES_NOT_FOUND(HttpStatus.NOT_FOUND, "Roles not found", 404),
        ADMIN_REGISTERED(HttpStatus.CONFLICT, "Cannot register as admin", 409),
        ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "Role not found", 404),
        INVALID_DOB(HttpStatus.BAD_REQUEST, "Must be over 2 years old", 400),
        RABBITMQ_ERROR(HttpStatus.BAD_REQUEST, "RabbitMQ error", 400),
        INVALID_TOKEN_VERSION(HttpStatus.BAD_REQUEST, "Invalid token version", 400),
        OTP_IN_USED(HttpStatus.CONFLICT, "OTP in use", 409),
        OTP_EXPIRED(HttpStatus.CONFLICT, "OTP expired", 409),
        OTP_INVALID(HttpStatus.CONFLICT, "OTP invalid", 409),
        INVALID_EMAIL(HttpStatus.BAD_REQUEST, "Invalid email", 400),
        FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "File not found", 404),
        FILE_UPLOAD_FAILED(HttpStatus.CONFLICT, "File upload failed", 409),
        USER_CREATION_FAILED(HttpStatus.CONFLICT, "User creation failed", 409),
        OTP_NOT_VERIFIED(HttpStatus.CONFLICT, "OTP not verified", 409),
    ;

    private HttpStatus status;
    private String message;
    private Integer code;

}
