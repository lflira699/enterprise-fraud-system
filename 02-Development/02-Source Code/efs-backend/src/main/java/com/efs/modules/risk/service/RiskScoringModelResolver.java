package com.efs.modules.risk.service;

import com.efs.modules.administration.entity.SystemConfiguration;
import com.efs.modules.administration.service.SystemConfigurationServiceInterface;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class RiskScoringModelResolver {

    private static final String ACTIVE_MODEL_KEY =
            "EFS.RISK.ACTIVE_MODEL";

    private static final String MODEL_PREFIX =
            "EFS.RISK.MODEL.";

    private static final List<String> FACTOR_CODES =
            List.of(
                    "RULES",
                    "BEHAVIORAL",
                    "CUSTOMER",
                    "GEOGRAPHIC",
                    "DEVICE"
            );

    private static final List<String> RISK_LEVELS =
            List.of(
                    "VERY_LOW",
                    "LOW",
                    "MEDIUM",
                    "HIGH",
                    "CRITICAL"
            );

    private final SystemConfigurationServiceInterface
            systemConfigurationService;

    public RiskScoringModelResolver(
            SystemConfigurationServiceInterface
                    systemConfigurationService) {

        this.systemConfigurationService =
                systemConfigurationService;
    }

    public RiskScoringModel resolve(
            UUID organizationId,
            UUID tenantId) {

        String modelVersion =
                requiredString(
                        ACTIVE_MODEL_KEY,
                        organizationId,
                        tenantId
                );

        String modelKeyPrefix =
                MODEL_PREFIX
                        + modelVersion;

        String modelName =
                requiredString(
                        modelKeyPrefix + ".NAME",
                        organizationId,
                        tenantId
                );

        BigDecimal scoreMinimum =
                requiredBigDecimal(
                        modelKeyPrefix + ".SCORE.MIN",
                        organizationId,
                        tenantId
                );

        BigDecimal scoreMaximum =
                requiredBigDecimal(
                        modelKeyPrefix + ".SCORE.MAX",
                        organizationId,
                        tenantId
                );

        List<RiskScoringModel.Factor> factors =
                FACTOR_CODES
                        .stream()
                        .map(
                                factorCode ->
                                        resolveFactor(
                                                modelKeyPrefix,
                                                factorCode,
                                                organizationId,
                                                tenantId
                                        )
                        )
                        .toList();

        List<RiskScoringModel.Threshold> thresholds =
                RISK_LEVELS
                        .stream()
                        .map(
                                riskLevel ->
                                        resolveThreshold(
                                                modelKeyPrefix,
                                                riskLevel,
                                                organizationId,
                                                tenantId
                                        )
                        )
                        .toList();

        return new RiskScoringModel(
                modelName,
                modelVersion,
                scoreMinimum,
                scoreMaximum,
                factors,
                thresholds
        );
    }

    private RiskScoringModel.Factor resolveFactor(
            String modelKeyPrefix,
            String factorCode,
            UUID organizationId,
            UUID tenantId) {

        String factorKeyPrefix =
                modelKeyPrefix
                        + ".FACTOR."
                        + factorCode;

        boolean enabled =
                requiredBoolean(
                        factorKeyPrefix + ".ENABLED",
                        organizationId,
                        tenantId
                );

        BigDecimal weight =
                requiredBigDecimal(
                        factorKeyPrefix + ".WEIGHT",
                        organizationId,
                        tenantId
                );

        return new RiskScoringModel.Factor(
                factorCode,
                enabled,
                weight
        );
    }

    private RiskScoringModel.Threshold resolveThreshold(
            String modelKeyPrefix,
            String riskLevel,
            UUID organizationId,
            UUID tenantId) {

        BigDecimal minimumScore =
                requiredBigDecimal(
                        modelKeyPrefix
                                + ".THRESHOLD."
                                + riskLevel
                                + ".MIN",
                        organizationId,
                        tenantId
                );

        return new RiskScoringModel.Threshold(
                riskLevel,
                minimumScore
        );
    }

    private String requiredString(
            String configurationKey,
            UUID organizationId,
            UUID tenantId) {

        SystemConfiguration configuration =
                requiredConfiguration(
                        configurationKey,
                        organizationId,
                        tenantId
                );

        validateConfigurationType(
                configurationKey,
                configuration
        );

        String value =
                configuration.getConfigurationValue();

        if (value == null
                || value.isBlank()) {
            throw new IllegalStateException(
                    "Required risk configuration is blank: "
                            + configurationKey
            );
        }

        return value.trim();
    }

    private BigDecimal requiredBigDecimal(
            String configurationKey,
            UUID organizationId,
            UUID tenantId) {

        String value =
                requiredString(
                        configurationKey,
                        organizationId,
                        tenantId
                );

        try {
            return new BigDecimal(value);
        } catch (NumberFormatException exception) {
            throw new IllegalStateException(
                    "Invalid decimal risk configuration: "
                            + configurationKey,
                    exception
            );
        }
    }

    private boolean requiredBoolean(
            String configurationKey,
            UUID organizationId,
            UUID tenantId) {

        String value =
                requiredString(
                        configurationKey,
                        organizationId,
                        tenantId
                );

        if ("true".equalsIgnoreCase(value)) {
            return true;
        }

        if ("false".equalsIgnoreCase(value)) {
            return false;
        }

        throw new IllegalStateException(
                "Invalid boolean risk configuration: "
                        + configurationKey
        );
    }

    private SystemConfiguration requiredConfiguration(
            String configurationKey,
            UUID organizationId,
            UUID tenantId) {

        return systemConfigurationService
                .resolveConfiguration(
                        configurationKey,
                        organizationId,
                        tenantId
                )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Required risk configuration is unavailable: "
                                                + configurationKey
                                )
                );
    }

    private void validateConfigurationType(
            String configurationKey,
            SystemConfiguration configuration) {

        if (!"STRING".equals(
                configuration.getConfigurationType())) {
            throw new IllegalStateException(
                    "Risk configuration must use STRING type: "
                            + configurationKey
            );
        }
    }
}