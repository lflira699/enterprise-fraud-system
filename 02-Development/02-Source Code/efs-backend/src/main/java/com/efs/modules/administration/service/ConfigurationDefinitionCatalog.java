package com.efs.modules.administration.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class ConfigurationDefinitionCatalog {

    public static final String CATALOG_VERSION = "v1";

    public enum Scope {
        GLOBAL,
        ORGANIZATION,
        TENANT
    }

    public enum ValueKind {
        NON_BLANK,
        POSITIVE_INTEGER,
        DECIMAL,
        BOOLEAN
    }

    public record Definition(
            String configurationKey,
            String configurationType,
            Set<Scope> allowedScopes,
            ValueKind valueKind,
            boolean encrypted,
            boolean critical
    ) {
    }

    private static final Set<Scope>
            INSTITUTIONAL_SCOPES =
            Set.of(
                    Scope.ORGANIZATION,
                    Scope.TENANT
            );

    private final Map<String, Definition> definitions;

    public ConfigurationDefinitionCatalog() {

        Map<String, Definition> catalog =
                new LinkedHashMap<>();

        register(
                catalog,
                "EFS.REPORT.MAX_RECORDS",
                "INTEGER",
                ValueKind.POSITIVE_INTEGER
        );

        registerRiskString(
                catalog,
                "EFS.RISK.MODEL.1.1.NAME",
                ValueKind.NON_BLANK
        );

        registerRiskString(
                catalog,
                "EFS.RISK.MODEL.1.1.SCORE.MIN",
                ValueKind.DECIMAL
        );

        registerRiskString(
                catalog,
                "EFS.RISK.MODEL.1.1.SCORE.MAX",
                ValueKind.DECIMAL
        );

        registerRiskFactor(
                catalog,
                "RULES"
        );

        registerRiskFactor(
                catalog,
                "BEHAVIORAL"
        );

        registerRiskFactor(
                catalog,
                "CUSTOMER"
        );

        registerRiskFactor(
                catalog,
                "GEOGRAPHIC"
        );

        registerRiskFactor(
                catalog,
                "DEVICE"
        );

        registerRiskThreshold(
                catalog,
                "VERY_LOW"
        );

        registerRiskThreshold(
                catalog,
                "LOW"
        );

        registerRiskThreshold(
                catalog,
                "MEDIUM"
        );

        registerRiskThreshold(
                catalog,
                "HIGH"
        );

        registerRiskThreshold(
                catalog,
                "CRITICAL"
        );

        definitions =
                Map.copyOf(
                        catalog
                );
    }

    public String getCatalogVersion() {
        return CATALOG_VERSION;
    }

    public List<Definition> getDefinitions() {

        List<Definition> result =
                new ArrayList<>(
                        definitions.values()
                );

        result.sort(
                Comparator.comparing(
                        Definition::configurationKey
                )
        );

        return List.copyOf(
                result
        );
    }

    public Optional<Definition> findDefinition(
            String configurationKey) {

        if (
                configurationKey == null
                        || configurationKey.isBlank()
        ) {
            return Optional.empty();
        }

        return Optional.ofNullable(
                definitions.get(
                        configurationKey.trim()
                )
        );
    }

    public Definition requireDefinition(
            String configurationKey) {

        return findDefinition(
                configurationKey
        ).orElseThrow(
                () ->
                        new IllegalArgumentException(
                                "Configuration key is not administrable: "
                                        + configurationKey
                        )
        );
    }

    public Definition requireManageableDefinition(
            String configurationKey,
            Scope scope) {

        if (scope == null) {
            throw new IllegalArgumentException(
                    "Configuration scope is required"
            );
        }

        Definition definition =
                requireDefinition(
                        configurationKey
                );

        if (
                !definition.allowedScopes()
                        .contains(scope)
        ) {
            throw new IllegalArgumentException(
                    "Configuration key is not administrable for scope "
                            + scope
                            + ": "
                            + definition.configurationKey()
            );
        }

        return definition;
    }

    public void validateValue(
            Definition definition,
            String proposedValue) {

        if (definition == null) {
            throw new IllegalArgumentException(
                    "Configuration definition is required"
            );
        }

        if (
                proposedValue == null
                        || proposedValue.isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Configuration value must not be null or blank: "
                            + definition.configurationKey()
            );
        }

        String normalizedValue =
                proposedValue.trim();

        switch (definition.valueKind()) {

            case NON_BLANK -> {
                // Blank values are rejected above.
            }

            case POSITIVE_INTEGER ->
                    validatePositiveInteger(
                            definition,
                            normalizedValue
                    );

            case DECIMAL ->
                    validateDecimal(
                            definition,
                            normalizedValue
                    );

            case BOOLEAN ->
                    validateBoolean(
                            definition,
                            normalizedValue
                    );
        }
    }

    private void registerRiskFactor(
            Map<String, Definition> catalog,
            String factorCode) {

        String prefix =
                "EFS.RISK.MODEL.1.1.FACTOR."
                        + factorCode;

        registerRiskString(
                catalog,
                prefix + ".ENABLED",
                ValueKind.BOOLEAN
        );

        registerRiskString(
                catalog,
                prefix + ".WEIGHT",
                ValueKind.DECIMAL
        );
    }

    private void registerRiskThreshold(
            Map<String, Definition> catalog,
            String riskLevel) {

        registerRiskString(
                catalog,
                "EFS.RISK.MODEL.1.1.THRESHOLD."
                        + riskLevel
                        + ".MIN",
                ValueKind.DECIMAL
        );
    }

    private void registerRiskString(
            Map<String, Definition> catalog,
            String configurationKey,
            ValueKind valueKind) {

        register(
                catalog,
                configurationKey,
                "STRING",
                valueKind
        );
    }

    private void register(
            Map<String, Definition> catalog,
            String configurationKey,
            String configurationType,
            ValueKind valueKind) {

        Definition existing =
                catalog.put(
                        configurationKey,
                        new Definition(
                                configurationKey,
                                configurationType,
                                INSTITUTIONAL_SCOPES,
                                valueKind,
                                false,
                                true
                        )
                );

        if (existing != null) {
            throw new IllegalStateException(
                    "Duplicate configuration definition: "
                            + configurationKey
            );
        }
    }

    private void validatePositiveInteger(
            Definition definition,
            String value) {

        final int parsedValue;

        try {
            parsedValue =
                    Integer.parseInt(
                            value
                    );
        }
        catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Configuration value must be a positive integer: "
                            + definition.configurationKey(),
                    exception
            );
        }

        if (parsedValue <= 0) {
            throw new IllegalArgumentException(
                    "Configuration value must be greater than zero: "
                            + definition.configurationKey()
            );
        }
    }

    private void validateDecimal(
            Definition definition,
            String value) {

        try {
            new BigDecimal(
                    value
            );
        }
        catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Configuration value must be a decimal: "
                            + definition.configurationKey(),
                    exception
            );
        }
    }

    private void validateBoolean(
            Definition definition,
            String value) {

        if (
                "true".equalsIgnoreCase(value)
                        || "false".equalsIgnoreCase(value)
        ) {
            return;
        }

        throw new IllegalArgumentException(
                "Configuration value must be true or false: "
                        + definition.configurationKey()
        );
    }
}