package com.efs.modules.administration.dto;

import jakarta.validation.constraints.NotBlank;

public class ConfigurationRejectRequest {

    @NotBlank
    private String rejectionReason;

    public ConfigurationRejectRequest() {
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(
            String rejectionReason) {
        this.rejectionReason =
                rejectionReason;
    }
}