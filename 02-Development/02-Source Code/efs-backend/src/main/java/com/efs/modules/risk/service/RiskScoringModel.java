package com.efs.modules.risk.service;

import java.math.BigDecimal;
import java.util.List;

public record RiskScoringModel(
        String modelName,
        String modelVersion,
        BigDecimal scoreMinimum,
        BigDecimal scoreMaximum,
        List<Factor> factors,
        List<Threshold> thresholds) {

    public RiskScoringModel {

        factors =
                factors == null
                        ? List.of()
                        : List.copyOf(factors);

        thresholds =
                thresholds == null
                        ? List.of()
                        : List.copyOf(thresholds);
    }

    public record Factor(
            String factorCode,
            boolean enabled,
            BigDecimal weight) {
    }

    public record Threshold(
            String riskLevel,
            BigDecimal minimumScore) {
    }
}