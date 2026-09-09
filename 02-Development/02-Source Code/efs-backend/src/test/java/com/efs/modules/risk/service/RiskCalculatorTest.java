package com.efs.modules.risk.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RiskCalculatorTest {

    private final RiskCalculator calculator =
            new RiskCalculator();

    @Test
    void shouldCalculateRiskScoreWithEqualWeights() {

        RiskScoringModel model = standardModel();

        Map<String, BigDecimal> factorScores = Map.of(
                "RULES", new BigDecimal("80"),
                "BEHAVIORAL", new BigDecimal("60"),
                "CUSTOMER", new BigDecimal("30"),
                "GEOGRAPHIC", new BigDecimal("70"),
                "DEVICE", new BigDecimal("90")
        );

        RiskCalculationResult result =
                calculator.calculate(model, factorScores);

        assertEquals(
                new BigDecimal("66.00"),
                result.overallRiskScore()
        );

        assertEquals(
                "HIGH",
                result.riskLevel()
        );

        assertEquals(
                "EFS-RISK",
                result.modelName()
        );

        assertEquals(
                "1.1",
                result.modelVersion()
        );

        assertEquals(
                5,
                result.factorContributions().size()
        );
    }

    @Test
    void shouldCalculateRiskScoreWithConfiguredWeights() {

        RiskScoringModel model =
                new RiskScoringModel(
                        "EFS-RISK",
                        "1.1",
                        new BigDecimal("0"),
                        new BigDecimal("100"),
                        List.of(
                                factor("RULES", true, "4"),
                                factor("BEHAVIORAL", true, "2"),
                                factor("CUSTOMER", true, "1.5"),
                                factor("GEOGRAPHIC", true, "1"),
                                factor("DEVICE", true, "1.5")
                        ),
                        standardThresholds()
                );

        Map<String, BigDecimal> factorScores = Map.of(
                "RULES", new BigDecimal("80"),
                "BEHAVIORAL", new BigDecimal("60"),
                "CUSTOMER", new BigDecimal("30"),
                "GEOGRAPHIC", new BigDecimal("70"),
                "DEVICE", new BigDecimal("90")
        );

        RiskCalculationResult result =
                calculator.calculate(model, factorScores);

        assertEquals(
                new BigDecimal("69.00"),
                result.overallRiskScore()
        );

        assertEquals(
                "HIGH",
                result.riskLevel()
        );
    }

    @Test
    void shouldIgnoreDisabledFactor() {

        RiskScoringModel model =
                new RiskScoringModel(
                        "EFS-RISK",
                        "1.1",
                        new BigDecimal("0"),
                        new BigDecimal("100"),
                        List.of(
                                factor("RULES", true, "1"),
                                factor("MACHINE_LEARNING", false, "1")
                        ),
                        standardThresholds()
                );

        RiskCalculationResult result =
                calculator.calculate(
                        model,
                        Map.of(
                                "RULES",
                                new BigDecimal("80")
                        )
                );

        assertEquals(
                new BigDecimal("80.00"),
                result.overallRiskScore()
        );

        assertEquals(
                "CRITICAL",
                result.riskLevel()
        );

        assertEquals(
                1,
                result.factorContributions().size()
        );
    }

    @Test
    void shouldRejectMissingEnabledFactorScore() {

        RiskScoringModel model = standardModel();

        Map<String, BigDecimal> factorScores = Map.of(
                "RULES", new BigDecimal("80"),
                "BEHAVIORAL", new BigDecimal("60"),
                "CUSTOMER", new BigDecimal("30"),
                "GEOGRAPHIC", new BigDecimal("70")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculate(
                        model,
                        factorScores
                )
        );
    }

    @Test
    void shouldRejectFactorScoreOutsideConfiguredRange() {

        RiskScoringModel model =
                singleFactorModel();

        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculate(
                        model,
                        Map.of(
                                "RULES",
                                new BigDecimal("101")
                        )
                )
        );
    }

    @Test
    void shouldRejectEnabledFactorWithZeroWeight() {

        RiskScoringModel model =
                new RiskScoringModel(
                        "EFS-RISK",
                        "1.1",
                        new BigDecimal("0"),
                        new BigDecimal("100"),
                        List.of(
                                factor("RULES", true, "0")
                        ),
                        standardThresholds()
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculate(
                        model,
                        Map.of(
                                "RULES",
                                new BigDecimal("80")
                        )
                )
        );
    }

    @Test
    void shouldClassifyConfiguredThresholdBoundaries() {

        RiskScoringModel model =
                singleFactorModel();

        assertRiskLevel(model, "0", "VERY_LOW");
        assertRiskLevel(model, "19.99", "VERY_LOW");
        assertRiskLevel(model, "20", "LOW");
        assertRiskLevel(model, "39.99", "LOW");
        assertRiskLevel(model, "40", "MEDIUM");
        assertRiskLevel(model, "59.99", "MEDIUM");
        assertRiskLevel(model, "60", "HIGH");
        assertRiskLevel(model, "79.99", "HIGH");
        assertRiskLevel(model, "80", "CRITICAL");
        assertRiskLevel(model, "100", "CRITICAL");
    }

    private void assertRiskLevel(
            RiskScoringModel model,
            String score,
            String expectedRiskLevel) {

        RiskCalculationResult result =
                calculator.calculate(
                        model,
                        Map.of(
                                "RULES",
                                new BigDecimal(score)
                        )
                );

        assertEquals(
                expectedRiskLevel,
                result.riskLevel()
        );
    }

    private RiskScoringModel standardModel() {

        return new RiskScoringModel(
                "EFS-RISK",
                "1.1",
                new BigDecimal("0"),
                new BigDecimal("100"),
                List.of(
                        factor("RULES", true, "1"),
                        factor("BEHAVIORAL", true, "1"),
                        factor("CUSTOMER", true, "1"),
                        factor("GEOGRAPHIC", true, "1"),
                        factor("DEVICE", true, "1")
                ),
                standardThresholds()
        );
    }

    private RiskScoringModel singleFactorModel() {

        return new RiskScoringModel(
                "EFS-RISK",
                "1.1",
                new BigDecimal("0"),
                new BigDecimal("100"),
                List.of(
                        factor("RULES", true, "1")
                ),
                standardThresholds()
        );
    }

    private RiskScoringModel.Factor factor(
            String factorCode,
            boolean enabled,
            String weight) {

        return new RiskScoringModel.Factor(
                factorCode,
                enabled,
                new BigDecimal(weight)
        );
    }

    private List<RiskScoringModel.Threshold> standardThresholds() {

        return List.of(
                threshold("VERY_LOW", "0"),
                threshold("LOW", "20"),
                threshold("MEDIUM", "40"),
                threshold("HIGH", "60"),
                threshold("CRITICAL", "80")
        );
    }

    private RiskScoringModel.Threshold threshold(
            String riskLevel,
            String minimumScore) {

        return new RiskScoringModel.Threshold(
                riskLevel,
                new BigDecimal(minimumScore)
        );
    }
}