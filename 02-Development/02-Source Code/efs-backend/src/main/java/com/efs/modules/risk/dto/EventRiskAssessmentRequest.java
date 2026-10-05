package com.efs.modules.risk.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class EventRiskAssessmentRequest {

    @NotNull
    private UUID fraudEventId;

    @NotNull
    private Map<String, BigDecimal> factorScores;

    public UUID getFraudEventId() {
        return fraudEventId;
    }

    public void setFraudEventId(
            UUID fraudEventId) {

        this.fraudEventId =
                fraudEventId;
    }

    public Map<String, BigDecimal> getFactorScores() {
        return factorScores;
    }

    public void setFactorScores(
            Map<String, BigDecimal> factorScores) {

        this.factorScores =
                factorScores;
    }
}
