package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.CatalogRequest;
import com.efs.modules.catalog.dto.CatalogResponse;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class CatalogServiceIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "d1d1d1d1-d1d1-d1d1-d1d1-d1d1d1d1d1d1"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "d2d2d2d2-d2d2-d2d2-d2d2-d2d2d2d2d2d2"
            );

    @Autowired
    private CatalogServiceInterface catalogService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertTenant();
    }

    @Test
    void shouldCreateAndRetrieveCatalogById() {

        CatalogRequest request =
                new CatalogRequest();

        request.setCatalogCode(
                "FRAUD_CASE_STATUS"
        );

        request.setCatalogName(
                "Fraud Case Status"
        );

        request.setDescription(
                "Controlled catalog for fraud case status"
        );

        request.setOrganizationId(
                ORGANIZATION_ID
        );

        request.setTenantId(
                TENANT_ID
        );

        request.setStatus(
                "ACTIVE"
        );

        CatalogResponse created =
                catalogService.createCatalog(
                        request
                );

        assertNotNull(
                created
        );

        assertNotNull(
                created.getCatalogId()
        );

        assertEquals(
                "FRAUD_CASE_STATUS",
                created.getCatalogCode()
        );

        assertEquals(
                "Fraud Case Status",
                created.getCatalogName()
        );

        assertEquals(
                ORGANIZATION_ID,
                created.getOrganizationId()
        );

        assertEquals(
                TENANT_ID,
                created.getTenantId()
        );

        assertEquals(
                "ACTIVE",
                created.getStatus()
        );

        assertNotNull(
                created.getCreatedAt()
        );

        CatalogResponse retrieved =
                catalogService.getCatalogById(
                        created.getCatalogId()
                );

        assertEquals(
                created.getCatalogId(),
                retrieved.getCatalogId()
        );
    }

    @Test
    void shouldAllowOptionalFieldsToBeNull() {

        CatalogRequest request =
                new CatalogRequest();

        request.setCatalogCode(
                "GLOBAL_REFERENCE"
        );

        request.setCatalogName(
                "Global Reference"
        );

        request.setStatus(
                "ACTIVE"
        );

        CatalogResponse created =
                catalogService.createCatalog(
                        request
                );

        assertNotNull(
                created.getCatalogId()
        );

        assertNull(
                created.getDescription()
        );

        assertNull(
                created.getOrganizationId()
        );

        assertNull(
                created.getTenantId()
        );

        assertNotNull(
                created.getCreatedAt()
        );
    }

    @Test
    void shouldRetrieveCatalogByCode() {

        UUID catalogId =
                UUID.randomUUID();

        insertCatalog(
                catalogId,
                "RISK_LEVEL",
                "Risk Level",
                ORGANIZATION_ID,
                TENANT_ID,
                "ACTIVE"
        );

        CatalogResponse result =
                catalogService.getCatalogByCode(
                        "RISK_LEVEL"
                );

        assertEquals(
                catalogId,
                result.getCatalogId()
        );

        assertEquals(
                "RISK_LEVEL",
                result.getCatalogCode()
        );
    }

    @Test
    void shouldReturnCatalogsByStatus() {

        insertCatalog(
                UUID.randomUUID(),
                "CASE_STATUS",
                "Case Status",
                ORGANIZATION_ID,
                TENANT_ID,
                "ACTIVE"
        );

        insertCatalog(
                UUID.randomUUID(),
                "DECISION_TYPE",
                "Decision Type",
                ORGANIZATION_ID,
                TENANT_ID,
                "ACTIVE"
        );

        List<CatalogResponse> results =
                catalogService.getCatalogsByStatus(
                        "ACTIVE"
                );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                results.stream()
                        .allMatch(
                                result ->
                                        "ACTIVE".equals(
                                                result.getStatus()
                                        )
                        )
        );
    }

    @Test
    void shouldReturnCatalogsByOrganizationId() {

        insertCatalog(
                UUID.randomUUID(),
                "CASE_STATUS",
                "Case Status",
                ORGANIZATION_ID,
                TENANT_ID,
                "ACTIVE"
        );

        insertCatalog(
                UUID.randomUUID(),
                "RISK_LEVEL",
                "Risk Level",
                ORGANIZATION_ID,
                TENANT_ID,
                "ACTIVE"
        );

        List<CatalogResponse> results =
                catalogService.getCatalogsByOrganizationId(
                        ORGANIZATION_ID
                );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                results.stream()
                        .allMatch(
                                result ->
                                        ORGANIZATION_ID.equals(
                                                result.getOrganizationId()
                                        )
                        )
        );
    }

    @Test
    void shouldReturnCatalogsByTenantId() {

        insertCatalog(
                UUID.randomUUID(),
                "CASE_STATUS",
                "Case Status",
                ORGANIZATION_ID,
                TENANT_ID,
                "ACTIVE"
        );

        insertCatalog(
                UUID.randomUUID(),
                "RISK_LEVEL",
                "Risk Level",
                ORGANIZATION_ID,
                TENANT_ID,
                "ACTIVE"
        );

        List<CatalogResponse> results =
                catalogService.getCatalogsByTenantId(
                        TENANT_ID
                );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                results.stream()
                        .allMatch(
                                result ->
                                        TENANT_ID.equals(
                                                result.getTenantId()
                                        )
                        )
        );
    }

    @Test
    void shouldRejectUnknownCatalogId() {

        UUID unknownCatalogId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        catalogService.getCatalogById(
                                unknownCatalogId
                        )
        );
    }

    @Test
    void shouldRejectUnknownCatalogCode() {

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        catalogService.getCatalogByCode(
                                "UNKNOWN_CATALOG"
                        )
        );
    }

    private void insertCatalog(
            UUID catalogId,
            String catalogCode,
            String catalogName,
            UUID organizationId,
            UUID tenantId,
            String status) {

        jdbcTemplate.update(
                """
                INSERT INTO catalog.catalog (
                    catalog_id,
                    catalog_code,
                    catalog_name,
                    description,
                    organization_id,
                    tenant_id,
                    status,
                    created_at
                )
                VALUES (
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    clock_timestamp()
                )
                """,
                catalogId,
                catalogCode,
                catalogName,
                "Integration test catalog",
                organizationId,
                tenantId,
                status
        );
    }

    private void insertOrganization() {

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
                "EFS-CATALOG-ORG",
                "EFS Catalog Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );
    }

    private void insertTenant() {

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
                "EFS-CATALOG-TENANT",
                "EFS Catalog Tenant",
                "ACTIVE",
                "TEST"
        );
    }
}