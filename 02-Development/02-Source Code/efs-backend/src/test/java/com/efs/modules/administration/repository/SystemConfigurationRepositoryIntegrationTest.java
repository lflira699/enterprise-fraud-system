package com.efs.modules.administration.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class SystemConfigurationRepositoryIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "33333333-3333-3333-3333-333333333333"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "44444444-4444-4444-4444-444444444444"
            );

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
                "EFS-V117-ORG",
                "EFS V117 Test Organization",
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
                "EFS-V117-TENANT",
                "EFS V117 Test Tenant",
                "ACTIVE",
                "TEST"
        );
    }

    @Test
    void shouldRejectTenantConfigurationWithoutOrganization() {

        assertThrows(
                DataIntegrityViolationException.class,
                () -> jdbcTemplate.update(
                        """
                        INSERT INTO administration.system_configuration (
                            configuration_key,
                            configuration_value,
                            configuration_type,
                            tenant_id
                        )
                        VALUES (?, ?, ?, ?)
                        """,
                        "EFS.V117.TENANT.WITHOUT.ORGANIZATION",
                        "VALUE",
                        "STRING",
                        TENANT_ID
                )
        );
    }

    @Test
    void shouldRejectDuplicateGlobalConfigurationKey() {

        String configurationKey =
                "EFS.V117.DUPLICATE.GLOBAL";

        insertConfiguration(
                configurationKey,
                "FIRST",
                null,
                null
        );

        assertThrows(
                DataIntegrityViolationException.class,
                () -> insertConfiguration(
                        configurationKey,
                        "SECOND",
                        null,
                        null
                )
        );
    }

    @Test
    void shouldRejectDuplicateOrganizationConfigurationKey() {

        String configurationKey =
                "EFS.V117.DUPLICATE.ORGANIZATION";

        insertConfiguration(
                configurationKey,
                "FIRST",
                ORGANIZATION_ID,
                null
        );

        assertThrows(
                DataIntegrityViolationException.class,
                () -> insertConfiguration(
                        configurationKey,
                        "SECOND",
                        ORGANIZATION_ID,
                        null
                )
        );
    }

    @Test
    void shouldRejectDuplicateTenantConfigurationKey() {

        String configurationKey =
                "EFS.V117.DUPLICATE.TENANT";

        insertConfiguration(
                configurationKey,
                "FIRST",
                ORGANIZATION_ID,
                TENANT_ID
        );

        assertThrows(
                DataIntegrityViolationException.class,
                () -> insertConfiguration(
                        configurationKey,
                        "SECOND",
                        ORGANIZATION_ID,
                        TENANT_ID
                )
        );
    }

    private void insertConfiguration(
            String configurationKey,
            String configurationValue,
            UUID organizationId,
            UUID tenantId) {

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
                configurationKey,
                configurationValue,
                "STRING",
                organizationId,
                tenantId
        );
    }
}