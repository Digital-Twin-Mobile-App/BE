package com.project.dadn.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCodes {
        CAR_NOT_FOUND(HttpStatus.NOT_FOUND, "Car not found", 404),
        MANUFACTURER_NOT_FOUND(HttpStatus.NOT_FOUND, "Manufacture not found", 404),
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
    ;

    private HttpStatus status;
    private String message;
    private Integer code;

}
