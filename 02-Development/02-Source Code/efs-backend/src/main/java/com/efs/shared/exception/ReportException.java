package com.efs.shared.exception;

import org.springframework.http.HttpStatus;

public class ReportException
        extends RuntimeException {

    private final HttpStatus status;

    private final String errorCode;

    public ReportException(
            HttpStatus status,
            String errorCode,
            String message) {

        super(message);

        this.status = status;

        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
