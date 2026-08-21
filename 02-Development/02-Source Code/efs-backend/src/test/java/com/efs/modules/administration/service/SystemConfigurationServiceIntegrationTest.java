package com.efs.modules.administration.service;

import com.efs.modules.administration.entity.SystemConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class SystemConfigurationServiceIntegrationTest {

    private static final String CONFIGURATION_KEY =
            "EFS.CONFIG.RESOLUTION.TEST";

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "11111111-1111-1111-1111-111111111111"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "22222222-2222-2222-2222-222222222222"
            );

    @Autowired
    private SystemConfigurationServiceInterface service;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        jdbcTemplate.update(
                """
                INSERT INTO administration.organization (
                    organization_id,
                    organization_code,
                    legal_name,
                    country_code,
                    timezone,
                    status
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                ORGANIZATION_ID,
                "EFS-CONFIG-TEST-ORG",
                "EFS Configuration Test Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );

        jdbcTemplate.update(
                """
                INSERT INTO administration.tenant (
                    tenant_id,
                    organization_id,
                    tenant_code,
                    tenant_name,
                    status,
                    environment
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                TENANT_ID,
                ORGANIZATION_ID,
                "EFS-CONFIG-TEST-TENANT",
                "EFS Configuration Test Tenant",
                "ACTIVE",
                "TEST"
        );

        jdbcTemplate.update(
                """
                INSERT INTO administration.system_configuration (
                    configuration_key,
                    configuration_value,
                    configuration_type
                )
                VALUES (?, ?, ?)
                """,
                CONFIGURATION_KEY,
                "GLOBAL",
                "STRING"
        );

        jdbcTemplate.update(
                """
                INSERT INTO administration.system_configuration (
                    configuration_key,
                    configuration_value,
                    configuration_type,
                    organization_id
                )
                VALUES (?, ?, ?, ?)
                """,
                CONFIGURATION_KEY,
                "ORGANIZATION",
                "STRING",
                ORGANIZATION_ID
        );

        jdbcTemplate.update(
                """
                INSERT INTO administration.system_configuration (
                    configuration_key,
                    configuration_value,
                    configuration_type,
                    organization_id,
                    tenant_id
                )
                VALUES (?, ?, ?, ?, ?)
                """,
                CONFIGURATION_KEY,
                "TENANT",
                "STRING",
                ORGANIZATION_ID,
                TENANT_ID
        );
    }

    @Test
    void shouldResolveTenantConfigurationFirst() {

        Optional<SystemConfiguration> result =
                service.resolveConfiguration(
                        CONFIGURATION_KEY,
                        ORGANIZATION_ID,
                        TENANT_ID
                );

        assertTrue(result.isPresent());
        assertEquals(
                "TENANT",
                result.get().getConfigurationValue()
        );
    }

    @Test
    void shouldFallbackToOrganizationConfiguration() {

        Optional<SystemConfiguration> result =
                service.resolveConfiguration(
                        CONFIGURATION_KEY,
                        ORGANIZATION_ID,
                        null
                );

        assertTrue(result.isPresent());
        assertEquals(
                "ORGANIZATION",
                result.get().getConfigurationValue()
        );
    }

    @Test
    void shouldFallbackToGlobalConfiguration() {

        Optional<SystemConfiguration> result =
                service.resolveConfiguration(
                        CONFIGURATION_KEY,
                        null,
                        null
                );

        assertTrue(result.isPresent());
        assertEquals(
                "GLOBAL",
                result.get().getConfigurationValue()
        );
    }
}