package com.efs.modules.risk.service;

import com.efs.modules.administration.entity.SystemConfiguration;
import com.efs.modules.administration.service.SystemConfigurationServiceInterface;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RiskScoringModelResolverTest {

    private final SystemConfigurationServiceInterface
            systemConfigurationService =
            mock(SystemConfigurationServiceInterface.class);

    private final RiskScoringModelResolver resolver =
            new RiskScoringModelResolver(
                    systemConfigurationService
            );

    @Test
    void shouldResolveConfiguredRiskScoringModel() {

        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        Map<String, String> configurations =
                standardConfigurations();

        stubConfigurations(
                configurations,
                organizationId,
                tenantId
        );

        RiskScoringModel model =
                resolver.resolve(
                        organizationId,
                        tenantId
                );

        assertEquals(
                "EFS-RISK",
                model.modelName()
        );

        assertEquals(
                "1.1",
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
                5,
                model.factors().size()
        );

        assertFactor(
                model,
                "RULES",
                true,
                "1"
        );

        assertFactor(
                model,
                "BEHAVIORAL",
                true,
                "1"
        );

        assertFactor(
                model,
                "CUSTOMER",
                true,
                "1"
        );

        assertFactor(
                model,
                "GEOGRAPHIC",
                true,
                "1"
        );

        assertFactor(
                model,
                "DEVICE",
                true,
                "1"
        );

        assertEquals(
                5,
                model.thresholds().size()
        );

        assertThreshold(
                model,
                "VERY_LOW",
                "0"
        );

        assertThreshold(
                model,
                "LOW",
                "20"
        );

        assertThreshold(
                model,
                "MEDIUM",
                "40"
        );

        assertThreshold(
                model,
                "HIGH",
                "60"
        );

        assertThreshold(
                model,
                "CRITICAL",
                "80"
        );
    }

    @Test
    void shouldRejectMissingRequiredConfiguration() {

        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        Map<String, String> configurations =
                standardConfigurations();

        configurations.remove(
                "EFS.RISK.MODEL.1.1.FACTOR.DEVICE.WEIGHT"
        );

        stubConfigurations(
                configurations,
                organizationId,
                tenantId
        );

        assertThrows(
                IllegalStateException.class,
                () ->
                        resolver.resolve(
                                organizationId,
                                tenantId
                        )
        );
    }

    @Test
    void shouldRejectInvalidDecimalConfiguration() {

        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        Map<String, String> configurations =
                standardConfigurations();

        configurations.put(
                "EFS.RISK.MODEL.1.1.SCORE.MAX",
                "invalid"
        );

        stubConfigurations(
                configurations,
                organizationId,
                tenantId
        );

        assertThrows(
                IllegalStateException.class,
                () ->
                        resolver.resolve(
                                organizationId,
                                tenantId
                        )
        );
    }

    @Test
    void shouldRejectInvalidBooleanConfiguration() {

        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        Map<String, String> configurations =
                standardConfigurations();

        configurations.put(
                "EFS.RISK.MODEL.1.1.FACTOR.RULES.ENABLED",
                "yes"
        );

        stubConfigurations(
                configurations,
                organizationId,
                tenantId
        );

        assertThrows(
                IllegalStateException.class,
                () ->
                        resolver.resolve(
                                organizationId,
                                tenantId
                        )
        );
    }

    @Test
    void shouldRejectNonStringConfigurationType() {

        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        when(
                systemConfigurationService
                        .resolveConfiguration(
                                "EFS.RISK.ACTIVE_MODEL",
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                Optional.of(
                        configuration(
                                "1.1",
                                "NUMBER"
                        )
                )
        );

        assertThrows(
                IllegalStateException.class,
                () ->
                        resolver.resolve(
                                organizationId,
                                tenantId
                        )
        );
    }

    private void stubConfigurations(
            Map<String, String> configurations,
            UUID organizationId,
            UUID tenantId) {

        when(
                systemConfigurationService
                        .resolveConfiguration(
                                anyString(),
                                eq(organizationId),
                                eq(tenantId)
                        )
        ).thenAnswer(
                invocation -> {

                    String configurationKey =
                            invocation.getArgument(0);

                    String configurationValue =
                            configurations.get(
                                    configurationKey
                            );

                    if (configurationValue == null) {
                        return Optional.empty();
                    }

                    return Optional.of(
                            configuration(
                                    configurationValue,
                                    "STRING"
                            )
                    );
                }
        );
    }

    private SystemConfiguration configuration(
            String value,
            String type) {

        SystemConfiguration configuration =
                new SystemConfiguration();

        configuration.setConfigurationValue(value);
        configuration.setConfigurationType(type);

        return configuration;
    }

    private Map<String, String>
    standardConfigurations() {

        Map<String, String> configurations =
                new HashMap<>();

        configurations.put(
                "EFS.RISK.ACTIVE_MODEL",
                "1.1"
        );

        configurations.put(
                "EFS.RISK.MODEL.1.1.NAME",
                "EFS-RISK"
        );

        configurations.put(
                "EFS.RISK.MODEL.1.1.SCORE.MIN",
                "0"
        );

        configurations.put(
                "EFS.RISK.MODEL.1.1.SCORE.MAX",
                "100"
        );

        addFactor(
                configurations,
                "RULES",
                "true",
                "1"
        );

        addFactor(
                configurations,
                "BEHAVIORAL",
                "true",
                "1"
        );

        addFactor(
                configurations,
                "CUSTOMER",
                "true",
                "1"
        );

        addFactor(
                configurations,
                "GEOGRAPHIC",
                "true",
                "1"
        );

        addFactor(
                configurations,
                "DEVICE",
                "true",
                "1"
        );

        configurations.put(
                "EFS.RISK.MODEL.1.1.THRESHOLD.VERY_LOW.MIN",
                "0"
        );

        configurations.put(
                "EFS.RISK.MODEL.1.1.THRESHOLD.LOW.MIN",
                "20"
        );

        configurations.put(
                "EFS.RISK.MODEL.1.1.THRESHOLD.MEDIUM.MIN",
                "40"
        );

        configurations.put(
                "EFS.RISK.MODEL.1.1.THRESHOLD.HIGH.MIN",
                "60"
        );

        configurations.put(
                "EFS.RISK.MODEL.1.1.THRESHOLD.CRITICAL.MIN",
                "80"
        );

        return configurations;
    }

    private void addFactor(
            Map<String, String> configurations,
            String factorCode,
            String enabled,
            String weight) {

        String prefix =
                "EFS.RISK.MODEL.1.1.FACTOR."
                        + factorCode;

        configurations.put(
                prefix + ".ENABLED",
                enabled
        );

        configurations.put(
                prefix + ".WEIGHT",
                weight
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