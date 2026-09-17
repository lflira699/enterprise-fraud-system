package com.efs.modules.administration.dto;

import java.time.LocalDateTime;
import java.util.List;

public class SystemHealthResponse {

    private final String status;
    private final String informationStatus;
    private final List<SystemHealthComponentResponse> components;
    private final LocalDateTime checkedAt;

    public SystemHealthResponse(
            String status,
            String informationStatus,
            List<SystemHealthComponentResponse> components,
            LocalDateTime checkedAt) {

        this.status =
                status;

        this.informationStatus =
                informationStatus;

        this.components =
                List.copyOf(
                        components
                );

        this.checkedAt =
                checkedAt;
    }

    public String getStatus() {
        return status;
    }

    public String getInformationStatus() {
        return informationStatus;
    }

    public List<SystemHealthComponentResponse> getComponents() {
        return components;
    }

    public LocalDateTime getCheckedAt() {
        return checkedAt;
    }
}