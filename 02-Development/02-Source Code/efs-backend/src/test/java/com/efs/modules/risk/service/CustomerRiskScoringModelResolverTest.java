package com.efs.modules.risk.service;

import com.efs.modules.administration.entity.SystemConfiguration;
import com.efs.modules.administration.service.SystemConfigurationServiceInterface;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerRiskScoringModelResolverTest {

    private final SystemConfigurationServiceInterface
            systemConfigurationService =
            mock(SystemConfigurationServiceInterface.class);

    private final RiskScoringModelResolver resolver =
            new RiskScoringModelResolver(
                    systemConfigurationService
            );

    @Test
    void shouldResolveCustomerRiskModelFromGlobalConfiguration() {

        Map<String, String> configurations =
                customerConfigurations();

        stubGlobalConfigurations(configurations);

        RiskScoringModel model =
                resolver.resolveCustomer();

        assertEquals(
                "EFS-CUSTOMER-RISK",
                model.modelName()
        );

        assertEquals(
                "1.0",
                model.modelVersion()
        );

        assertEquals(
                new BigDecimal("0"),
                model.scoreMinimum()
        );

        assertEquals(
                new BigDecimal("100"),
                model.scoreMaximum()
        );

        assertEquals(
                8,
                model.factors().size()
        );

        assertFactor(model, "BEHAVIOR", true, "1");
        assertFactor(model, "FRAUD", true, "1");
        assertFactor(model, "AML", true, "1");
        assertFactor(model, "KYC", true, "1");
        assertFactor(model, "DEVICE", true, "1");
        assertFactor(model, "SANCTIONS", true, "1");
        assertFactor(model, "PEP", true, "1");
        assertFactor(model, "WATCHLIST", true, "1");

        assertEquals(
                5,
                model.thresholds().size()
        );

        assertThreshold(model, "VERY_LOW", "0");
        assertThreshold(model, "LOW", "20");
        assertThreshold(model, "MEDIUM", "40");
        assertThreshold(model, "HIGH", "60");
        assertThreshold(model, "CRITICAL", "80");
    }

    @Test
    void shouldRejectMissingCustomerRiskFactorConfiguration() {

        Map<String, String> configurations =
                customerConfigurations();

        configurations.remove(
                "EFS.RISK.CUSTOMER.MODEL.1.0.FACTOR.WATCHLIST.WEIGHT"
        );

        stubGlobalConfigurations(configurations);

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        resolver::resolveCustomer
                );

        assertEquals(
                "Required risk configuration is unavailable: "
                        + "EFS.RISK.CUSTOMER.MODEL.1.0."
                        + "FACTOR.WATCHLIST.WEIGHT",
                exception.getMessage()
        );
    }

    private void stubGlobalConfigurations(
            Map<String, String> configurations) {

        when(
                systemConfigurationService
                        .resolveConfiguration(
                                anyString(),
                                isNull(),
                                isNull()
                        )
        ).thenAnswer(invocation -> {

            String configurationKey =
                    invocation.getArgument(0);

            String value =
                    configurations.get(configurationKey);

            if (value == null) {
                return Optional.empty();
            }

            SystemConfiguration configuration =
                    new SystemConfiguration();

            configuration.setConfigurationValue(value);
            configuration.setConfigurationType("STRING");

            return Optional.of(configuration);
        });
    }

    private Map<String, String> customerConfigurations() {

        Map<String, String> configurations =
                new HashMap<>();

        configurations.put(
                "EFS.RISK.CUSTOMER.ACTIVE_MODEL",
                "1.0"
        );

        configurations.put(
                "EFS.RISK.CUSTOMER.MODEL.1.0.NAME",
                "EFS-CUSTOMER-RISK"
        );

        configurations.put(
                "EFS.RISK.CUSTOMER.MODEL.1.0.SCORE.MIN",
                "0"
        );

        configurations.put(
                "EFS.RISK.CUSTOMER.MODEL.1.0.SCORE.MAX",
                "100"
        );

        addFactor(configurations, "BEHAVIOR");
        addFactor(configurations, "FRAUD");
        addFactor(configurations, "AML");
        addFactor(configurations, "KYC");
        addFactor(configurations, "DEVICE");
        addFactor(configurations, "SANCTIONS");
        addFactor(configurations, "PEP");
        addFactor(configurations, "WATCHLIST");

        configurations.put(
                "EFS.RISK.CUSTOMER.MODEL.1.0.THRESHOLD.VERY_LOW.MIN",
                "0"
        );

        configurations.put(
                "EFS.RISK.CUSTOMER.MODEL.1.0.THRESHOLD.LOW.MIN",
                "20"
        );

        configurations.put(
                "EFS.RISK.CUSTOMER.MODEL.1.0.THRESHOLD.MEDIUM.MIN",
                "40"
        );

        configurations.put(
                "EFS.RISK.CUSTOMER.MODEL.1.0.THRESHOLD.HIGH.MIN",
                "60"
        );

        configurations.put(
                "EFS.RISK.CUSTOMER.MODEL.1.0.THRESHOLD.CRITICAL.MIN",
                "80"
        );

        return configurations;
    }

    private void addFactor(
            Map<String, String> configurations,
            String factorCode) {

        String prefix =
                "EFS.RISK.CUSTOMER.MODEL.1.0.FACTOR."
                        + factorCode;

        configurations.put(
                prefix + ".ENABLED",
                "true"
        );

        configurations.put(
                prefix + ".WEIGHT",
                "1"
        );
    }

    private void assertFactor(
            RiskScoringModel model,
            String factorCode,
            boolean enabled,
            String weight) {

        RiskScoringModel.Factor factor =
                model.factors()
                        .stream()
                        .filter(
                                candidate ->
                                        factorCode.equals(
                                                candidate.factorCode()
                                        )
                        )
                        .findFirst()
                        .orElseThrow();

        assertEquals(
                enabled,
                factor.enabled()
        );

        assertEquals(
                new BigDecimal(weight),
                factor.weight()
        );
    }

    private void assertThreshold(
            RiskScoringModel model,
            String riskLevel,
            String minimumScore) {

        RiskScoringModel.Threshold threshold =
                model.thresholds()
                        .stream()
                        .filter(
                                candidate ->
                                        riskLevel.equals(
                                                candidate.riskLevel()
                                        )
                        )
                        .findFirst()
                        .orElseThrow();

        assertEquals(
                new BigDecimal(minimumScore),
                threshold.minimumScore()
        );
    }
}