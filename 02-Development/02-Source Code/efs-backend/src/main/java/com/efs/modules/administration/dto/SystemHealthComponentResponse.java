package com.efs.modules.administration.dto;

public class SystemHealthComponentResponse {

    private final String name;
    private final String status;

    public SystemHealthComponentResponse(
            String name,
            String status) {

        this.name =
                name;

        this.status =
                status;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }
}