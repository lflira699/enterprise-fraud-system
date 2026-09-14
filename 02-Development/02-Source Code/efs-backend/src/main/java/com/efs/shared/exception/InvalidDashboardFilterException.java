package com.efs.shared.exception;

public class InvalidDashboardFilterException
        extends RuntimeException {

    public InvalidDashboardFilterException(
            String message) {

        super(message);
    }
}
