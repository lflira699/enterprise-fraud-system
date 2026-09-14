package com.efs.shared.exception;

public class DashboardDataUnavailableException
        extends RuntimeException {

    public DashboardDataUnavailableException(
            String message) {

        super(message);
    }
}
