package com.efs.shared.exception;

public class RequestValidationException
        extends ValidationException {

    public RequestValidationException(
            String message) {

        super(message);
    }

    public RequestValidationException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}