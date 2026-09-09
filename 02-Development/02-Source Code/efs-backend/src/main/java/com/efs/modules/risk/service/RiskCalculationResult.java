package com.efs.modules.risk.service;

import java.math.BigDecimal;
import java.util.List;

public record RiskCalculationResult(
        String modelName,
        String modelVersion,
        BigDecimal overallRiskScore,
        String riskLevel,
        List<FactorContribution> factorContributions) {

    public RiskCalculationResult {

        factorContributions =
                factorContributions == null
                        ? List.of()
                        : List.copyOf(factorContributions);
    }

    public record FactorContribution(
            String factorCode,
            BigDecimal score,
            BigDecimal weight,
            BigDecimal weightedContribution) {
    }
}