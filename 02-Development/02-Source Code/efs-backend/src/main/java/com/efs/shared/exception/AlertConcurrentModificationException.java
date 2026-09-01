package com.efs.shared.exception;

public class AlertConcurrentModificationException
        extends BusinessException {

    public AlertConcurrentModificationException(
            String message) {

        super(message);
    }

    public AlertConcurrentModificationException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}