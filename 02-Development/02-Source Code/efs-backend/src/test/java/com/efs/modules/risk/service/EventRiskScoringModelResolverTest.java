package com.efs.modules.risk.service;

import com.efs.modules.administration.service.SystemConfigurationServiceInterface;
import com.efs.modules.administration.service.SystemConfigurationServiceInterface.ResolvedConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventRiskScoringModelResolverTest {

    @Mock
    private SystemConfigurationServiceInterface
            systemConfigurationService;

    private RiskScoringModelResolver resolver;

    private UUID organizationId;
    private UUID tenantId;

    @BeforeEach
    void setUp() {

        resolver =
                new RiskScoringModelResolver(
                        systemConfigurationService
                );

        organizationId =
                UUID.fromString(
                        "11111111-1111-1111-1111-111111111111"
                );

        tenantId =
                UUID.fromString(
                        "22222222-2222-2222-2222-222222222222"
                );
    }

    @Test
    void resolvesConfiguredEventModel() {

        Map<String, String> configurations =
                validEventConfigurations();

        stubConfigurations(
                configurations
        );

        RiskScoringModel model =
                resolver.resolveEvent(
                        organizationId,
                        tenantId
                );

        assertEquals(
                "EVENT_MODEL",
                model.modelName()
        );

        assertEquals(
                "v1",
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
                2,
                model.factors().size()
        );

        assertEquals(
                "VELOCITY",
                model.factors().get(0).factorCode()
        );

        assertEquals(
                new BigDecimal("0.40"),
                model.factors().get(0).weight()
        );

        assertEquals(
                "DEVICE",
                model.factors().get(1).factorCode()
        );

        assertEquals(
                new BigDecimal("0.60"),
                model.factors().get(1).weight()
        );

        assertEquals(
                5,
                model.thresholds().size()
        );
    }

    @Test
    void rejectsEmptyFactorCatalog() {

        Map<String, String> configurations =
                validEventConfigurations();

        configurations.put(
                "EFS.RISK.EVENT.MODEL.v1.FACTOR.CODES",
                ""
        );

        stubConfigurations(
                configurations
        );

        assertThrows(
                IllegalStateException.class,
                () ->
                        resolver.resolveEvent(
                                organizationId,
                                tenantId
                        )
        );
    }

    @Test
    void rejectsBlankFactorCode() {

        Map<String, String> configurations =
                validEventConfigurations();

        configurations.put(
                "EFS.RISK.EVENT.MODEL.v1.FACTOR.CODES",
                "VELOCITY, ,DEVICE"
        );

        stubConfigurations(
                configurations
        );

        assertThrows(
                IllegalStateException.class,
                () ->
                        resolver.resolveEvent(
                                organizationId,
                                tenantId
                        )
        );
    }

    @Test
    void rejectsDuplicateFactorCode() {

        Map<String, String> configurations =
                validEventConfigurations();

        configurations.put(
                "EFS.RISK.EVENT.MODEL.v1.FACTOR.CODES",
                "VELOCITY,VELOCITY"
        );

        stubConfigurations(
                configurations
        );

        assertThrows(
                IllegalStateException.class,
                () ->
                        resolver.resolveEvent(
                                organizationId,
                                tenantId
                        )
        );
    }

    @Test
    void rejectsMissingActiveEventModel() {

        when(
                systemConfigurationService
                        .resolveConfigurationDetails(
                                "EFS.RISK.EVENT.ACTIVE_MODEL",
                                organizationId,
                                tenantId
                        )
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                IllegalStateException.class,
                () ->
                        resolver.resolveEvent(
                                organizationId,
                                tenantId
                        )
        );
    }

    private void stubConfigurations(
            Map<String, String> configurations) {

        when(
                systemConfigurationService
                        .resolveConfigurationDetails(
                                anyString(),
                                eq(organizationId),
                                eq(tenantId)
                        )
        ).thenAnswer(
                invocation -> {

                    String key =
                            invocation.getArgument(0);

                    String value =
                            configurations.get(
                                    key
                            );

                    if (value == null) {
                        return Optional.empty();
                    }

                    return Optional.of(
                            new ResolvedConfiguration(
                                    value,
                                    "STRING"
                            )
                    );
                }
        );
    }

    private Map<String, String>
    validEventConfigurations() {

        Map<String, String> configurations =
                new HashMap<>();

        configurations.put(
                "EFS.RISK.EVENT.ACTIVE_MODEL",
                "v1"
        );

        configurations.put(
                "EFS.RISK.EVENT.MODEL.v1.FACTOR.CODES",
                "VELOCITY,DEVICE"
        );

        configurations.put(
                "EFS.RISK.EVENT.MODEL.v1.NAME",
                "EVENT_MODEL"
        );

        configurations.put(
                "EFS.RISK.EVENT.MODEL.v1.SCORE.MIN",
                "0"
        );

        configurations.put(
                "EFS.RISK.EVENT.MODEL.v1.SCORE.MAX",
                "100"
        );

        configurations.put(
                "EFS.RISK.EVENT.MODEL.v1.FACTOR.VELOCITY.ENABLED",
                "true"
        );

        configurations.put(
                "EFS.RISK.EVENT.MODEL.v1.FACTOR.VELOCITY.WEIGHT",
                "0.40"
        );

        configurations.put(
                "EFS.RISK.EVENT.MODEL.v1.FACTOR.DEVICE.ENABLED",
                "true"
        );

        configurations.put(
                "EFS.RISK.EVENT.MODEL.v1.FACTOR.DEVICE.WEIGHT",
                "0.60"
        );

        configurations.put(
                "EFS.RISK.EVENT.MODEL.v1.THRESHOLD.VERY_LOW.MIN",
                "0"
        );

        configurations.put(
                "EFS.RISK.EVENT.MODEL.v1.THRESHOLD.LOW.MIN",
                "20"
        );

        configurations.put(
                "EFS.RISK.EVENT.MODEL.v1.THRESHOLD.MEDIUM.MIN",
                "40"
        );

        configurations.put(
                "EFS.RISK.EVENT.MODEL.v1.THRESHOLD.HIGH.MIN",
                "60"
        );

        configurations.put(
                "EFS.RISK.EVENT.MODEL.v1.THRESHOLD.CRITICAL.MIN",
                "80"
        );

        return configurations;
    }
}