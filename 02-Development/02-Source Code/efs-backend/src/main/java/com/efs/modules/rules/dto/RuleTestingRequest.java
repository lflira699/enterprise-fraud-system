package com.efs.modules.rules.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class RuleTestingRequest {

    @NotBlank
    @Size(max = 180)
    private String simulationName;

    @NotBlank
    @Size(max = 250)
    private String datasetReference;

    @NotNull
    private UUID executedBy;

    private UUID correlationId;

    public String getSimulationName() {
        return simulationName;
    }

    public void setSimulationName(
            String simulationName) {

        this.simulationName =
                simulationName;
    }

    public String getDatasetReference() {
        return datasetReference;
    }

    public void setDatasetReference(
            String datasetReference) {

        this.datasetReference =
                datasetReference;
    }

    public UUID getExecutedBy() {
        return executedBy;
    }

    public void setExecutedBy(
            UUID executedBy) {

        this.executedBy =
                executedBy;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(
            UUID correlationId) {

        this.correlationId =
                correlationId;
    }
}