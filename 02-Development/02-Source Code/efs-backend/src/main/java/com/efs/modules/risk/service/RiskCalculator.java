package com.efs.modules.risk.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class RiskCalculator {

    private static final int RESULT_SCALE = 2;

    private static final RoundingMode RESULT_ROUNDING =
            RoundingMode.HALF_UP;

    public RiskCalculationResult calculate(
            RiskScoringModel model,
            Map<String, BigDecimal> factorScores) {

        validateModel(model);

        if (factorScores == null) {
            throw new IllegalArgumentException(
                    "Risk factor scores are required"
            );
        }

        BigDecimal weightedScoreSum = BigDecimal.ZERO;
        BigDecimal activeWeightSum = BigDecimal.ZERO;

        List<RiskCalculationResult.FactorContribution>
                contributions = new ArrayList<>();

        for (RiskScoringModel.Factor factor : model.factors()) {
            if (!factor.enabled()) {
                continue;
            }

            BigDecimal score =
                    factorScores.get(factor.factorCode());

            if (score == null) {
                throw new IllegalArgumentException(
                        "Risk score is required for enabled factor: "
                                + factor.factorCode()
                );
            }

            validateFactorScore(
                    factor.factorCode(),
                    score,
                    model.scoreMinimum(),
                    model.scoreMaximum()
            );

            BigDecimal weightedContribution =
                    score.multiply(factor.weight());

            weightedScoreSum =
                    weightedScoreSum.add(weightedContribution);

            activeWeightSum =
                    activeWeightSum.add(factor.weight());

            contributions.add(
                    new RiskCalculationResult.FactorContribution(
                            factor.factorCode(),
                            score,
                            factor.weight(),
                            weightedContribution
                    )
            );
        }

        if (activeWeightSum.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Risk scoring model must contain at least one enabled factor"
            );
        }

        BigDecimal overallRiskScore =
                weightedScoreSum.divide(
                        activeWeightSum,
                        RESULT_SCALE,
                        RESULT_ROUNDING
                );

        String riskLevel =
                resolveRiskLevel(model, overallRiskScore);

        return new RiskCalculationResult(
                model.modelName(),
                model.modelVersion(),
                overallRiskScore,
                riskLevel,
                contributions
        );
    }

    private void validateModel(RiskScoringModel model) {

        if (model == null) {
            throw new IllegalArgumentException(
                    "Risk scoring model is required"
            );
        }

        if (model.modelName() == null
                || model.modelName().isBlank()) {
            throw new IllegalArgumentException(
                    "Risk scoring model name is required"
            );
        }

        if (model.modelVersion() == null
                || model.modelVersion().isBlank()) {
            throw new IllegalArgumentException(
                    "Risk scoring model version is required"
            );
        }

        if (model.scoreMinimum() == null
                || model.scoreMaximum() == null) {
            throw new IllegalArgumentException(
                    "Risk scoring model score range is required"
            );
        }

        if (model.scoreMinimum()
                .compareTo(model.scoreMaximum()) >= 0) {
            throw new IllegalArgumentException(
                    "Risk scoring model minimum score must be lower than maximum score"
            );
        }

        validateFactors(model);
        validateThresholds(model);
    }

    private void validateFactors(RiskScoringModel model) {

        if (model.factors().isEmpty()) {
            throw new IllegalArgumentException(
                    "Risk scoring model factors are required"
            );
        }

        Set<String> factorCodes = new HashSet<>();
        boolean hasEnabledFactor = false;

        for (RiskScoringModel.Factor factor : model.factors()) {

            if (factor == null) {
                throw new IllegalArgumentException(
                        "Risk scoring model factor is required"
                );
            }

            if (factor.factorCode() == null
                    || factor.factorCode().isBlank()) {
                throw new IllegalArgumentException(
                        "Risk scoring factor code is required"
                );
            }

            if (!factorCodes.add(factor.factorCode())) {
                throw new IllegalArgumentException(
                        "Duplicate risk scoring factor: "
                                + factor.factorCode()
                );
            }

            if (!factor.enabled()) {
                continue;
            }

            hasEnabledFactor = true;

            if (factor.weight() == null
                    || factor.weight()
                    .compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                        "Enabled risk scoring factor weight must be greater than zero: "
                                + factor.factorCode()
                );
            }
        }

        if (!hasEnabledFactor) {
            throw new IllegalArgumentException(
                    "Risk scoring model must contain at least one enabled factor"
            );
        }
    }

    private void validateThresholds(RiskScoringModel model) {

        if (model.thresholds().isEmpty()) {
            throw new IllegalArgumentException(
                    "Risk scoring model thresholds are required"
            );
        }

        Set<String> riskLevels = new HashSet<>();
        Set<BigDecimal> minimumScores = new HashSet<>();

        for (RiskScoringModel.Threshold threshold
                : model.thresholds()) {

            if (threshold == null) {
                throw new IllegalArgumentException(
                        "Risk scoring threshold is required"
                );
            }

            if (threshold.riskLevel() == null
                    || threshold.riskLevel().isBlank()) {
                throw new IllegalArgumentException(
                        "Risk scoring threshold risk level is required"
                );
            }

            if (!riskLevels.add(threshold.riskLevel())) {
                throw new IllegalArgumentException(
                        "Duplicate risk scoring risk level: "
                                + threshold.riskLevel()
                );
            }

            if (threshold.minimumScore() == null) {
                throw new IllegalArgumentException(
                        "Risk scoring threshold minimum score is required: "
                                + threshold.riskLevel()
                );
            }

            if (threshold.minimumScore()
                    .compareTo(model.scoreMinimum()) < 0
                    || threshold.minimumScore()
                    .compareTo(model.scoreMaximum()) > 0) {
                throw new IllegalArgumentException(
                        "Risk scoring threshold is outside model score range: "
                                + threshold.riskLevel()
                );
            }

            if (!minimumScores.add(
                    threshold.minimumScore()
                            .stripTrailingZeros())) {
                throw new IllegalArgumentException(
                        "Duplicate risk scoring threshold minimum score: "
                                + threshold.minimumScore()
                );
            }
        }

        BigDecimal firstMinimum =
                model.thresholds()
                        .stream()
                        .map(
                                RiskScoringModel.Threshold
                                        ::minimumScore
                        )
                        .min(Comparator.naturalOrder())
                        .orElseThrow();

        if (firstMinimum.compareTo(
                model.scoreMinimum()) != 0) {
            throw new IllegalArgumentException(
                    "Risk scoring thresholds must begin at model minimum score"
            );
        }
    }

    private void validateFactorScore(
            String factorCode,
            BigDecimal score,
            BigDecimal minimum,
            BigDecimal maximum) {

        if (score.compareTo(minimum) < 0
                || score.compareTo(maximum) > 0) {
            throw new IllegalArgumentException(
                    "Risk score for factor "
                            + factorCode
                            + " must be between "
                            + minimum
                            + " and "
                            + maximum
            );
        }
    }

    private String resolveRiskLevel(
            RiskScoringModel model,
            BigDecimal overallRiskScore) {

        return model.thresholds()
                .stream()
                .sorted(
                        Comparator.comparing(
                                RiskScoringModel.Threshold
                                        ::minimumScore
                        )
                )
                .filter(
                        threshold ->
                                overallRiskScore.compareTo(
                                        threshold.minimumScore()
                                ) >= 0
                )
                .reduce((first, second) -> second)
                .map(
                        RiskScoringModel.Threshold
                                ::riskLevel
                )
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Risk score cannot be classified by configured thresholds"
                        )
                );
    }
}