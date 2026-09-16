package com.efs.modules.administration.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.efs.modules.administration.service.ConfigurationDefinitionCatalog.Scope.GLOBAL;
import static com.efs.modules.administration.service.ConfigurationDefinitionCatalog.Scope.ORGANIZATION;
import static com.efs.modules.administration.service.ConfigurationDefinitionCatalog.Scope.TENANT;
import static com.efs.modules.administration.service.ConfigurationDefinitionCatalog.ValueKind.BOOLEAN;
import static com.efs.modules.administration.service.ConfigurationDefinitionCatalog.ValueKind.DECIMAL;
import static com.efs.modules.administration.service.ConfigurationDefinitionCatalog.ValueKind.POSITIVE_INTEGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigurationDefinitionCatalogTest {

    private ConfigurationDefinitionCatalog catalog;

    @BeforeEach
    void setUp() {
        catalog =
                new ConfigurationDefinitionCatalog();
    }

    @Test
    void shouldExposeVersionedCatalogWithExactManageableKeyCount() {

        assertEquals(
                "v1",
                catalog.getCatalogVersion()
        );

        assertEquals(
                19,
                catalog.getDefinitions()
                        .size()
        );
    }

    @Test
    void reportMaxRecordsShouldUseIntegerInstitutionalContract() {

        ConfigurationDefinitionCatalog.Definition definition =
                catalog.requireDefinition(
                        "EFS.REPORT.MAX_RECORDS"
                );

        assertEquals(
                "INTEGER",
                definition.configurationType()
        );

        assertEquals(
                POSITIVE_INTEGER,
                definition.valueKind()
        );

        assertEquals(
                Set.of(
                        ORGANIZATION,
                        TENANT
                ),
                definition.allowedScopes()
        );

        assertFalse(
                definition.encrypted()
        );

        assertTrue(
                definition.critical()
        );
    }

    @Test
    void riskModelDefinitionsShouldPreserveStringStorageContract() {

        ConfigurationDefinitionCatalog.Definition decimalDefinition =
                catalog.requireDefinition(
                        "EFS.RISK.MODEL.1.1.SCORE.MAX"
                );

        assertEquals(
                "STRING",
                decimalDefinition.configurationType()
        );

        assertEquals(
                DECIMAL,
                decimalDefinition.valueKind()
        );

        ConfigurationDefinitionCatalog.Definition booleanDefinition =
                catalog.requireDefinition(
                        "EFS.RISK.MODEL.1.1.FACTOR.RULES.ENABLED"
                );

        assertEquals(
                "STRING",
                booleanDefinition.configurationType()
        );

        assertEquals(
                BOOLEAN,
                booleanDefinition.valueKind()
        );
    }

    @Test
    void shouldRejectGlobalAdministrationForManageableKeys() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        catalog.requireManageableDefinition(
                                "EFS.REPORT.MAX_RECORDS",
                                GLOBAL
                        )
        );
    }

    @Test
    void shouldRejectPlatformControlledAndGlobalOnlyKeys() {

        assertTrue(
                catalog.findDefinition(
                        "EFS.RISK.ACTIVE_MODEL"
                ).isEmpty()
        );

        assertTrue(
                catalog.findDefinition(
                        "EFS.RISK.MODEL.1.0.SCORE.MAX"
                ).isEmpty()
        );

        assertTrue(
                catalog.findDefinition(
                        "EFS.RISK.CUSTOMER.ACTIVE_MODEL"
                ).isEmpty()
        );

        assertTrue(
                catalog.findDefinition(
                        "EFS.RISK.CUSTOMER.MODEL.1.0.SCORE.MAX"
                ).isEmpty()
        );
    }

    @Test
    void shouldValidatePositiveReportLimit() {

        ConfigurationDefinitionCatalog.Definition definition =
                catalog.requireManageableDefinition(
                        "EFS.REPORT.MAX_RECORDS",
                        ORGANIZATION
                );

        catalog.validateValue(
                definition,
                "100"
        );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        catalog.validateValue(
                                definition,
                                "0"
                        )
        );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        catalog.validateValue(
                                definition,
                                "-1"
                        )
        );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        catalog.validateValue(
                                definition,
                                "invalid"
                        )
        );
    }

    @Test
    void shouldValidateRiskBooleanExactlyAsRuntimeContract() {

        ConfigurationDefinitionCatalog.Definition definition =
                catalog.requireManageableDefinition(
                        "EFS.RISK.MODEL.1.1.FACTOR.DEVICE.ENABLED",
                        TENANT
                );

        catalog.validateValue(
                definition,
                "true"
        );

        catalog.validateValue(
                definition,
                "FALSE"
        );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        catalog.validateValue(
                                definition,
                                "yes"
                        )
        );
    }

    @Test
    void shouldValidateRiskDecimalAndNonBlankValues() {

        ConfigurationDefinitionCatalog.Definition decimalDefinition =
                catalog.requireManageableDefinition(
                        "EFS.RISK.MODEL.1.1.THRESHOLD.HIGH.MIN",
                        ORGANIZATION
                );

        catalog.validateValue(
                decimalDefinition,
                "60"
        );

        catalog.validateValue(
                decimalDefinition,
                "60.5"
        );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        catalog.validateValue(
                                decimalDefinition,
                                "invalid"
                        )
        );

        ConfigurationDefinitionCatalog.Definition nameDefinition =
                catalog.requireManageableDefinition(
                        "EFS.RISK.MODEL.1.1.NAME",
                        TENANT
                );

        catalog.validateValue(
                nameDefinition,
                "Institution Risk Model"
        );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        catalog.validateValue(
                                nameDefinition,
                                "   "
                        )
        );
    }
}