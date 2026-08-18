package com.efs.modules.transaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

public class TransactionMetadataRequest {

    @NotBlank
    @Size(max = 60)
    private String metadataType;

    @NotNull
    private Map<String, Object> metadataJson;

    public String getMetadataType() {
        return metadataType;
    }

    public void setMetadataType(String metadataType) {
        this.metadataType = metadataType;
    }

    public Map<String, Object> getMetadataJson() {
        return metadataJson;
    }

    public void setMetadataJson(Map<String, Object> metadataJson) {
        this.metadataJson = metadataJson;
    }
}