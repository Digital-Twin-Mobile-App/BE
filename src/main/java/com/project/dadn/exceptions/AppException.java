package com.project.dadn.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppException extends  RuntimeException {
    private final ErrorCodes errorCode;

    public AppException(ErrorCodes errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
