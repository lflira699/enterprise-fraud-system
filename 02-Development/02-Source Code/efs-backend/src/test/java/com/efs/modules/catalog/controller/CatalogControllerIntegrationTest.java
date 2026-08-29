package com.efs.modules.catalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CatalogControllerIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "e2e2e2e2-e2e2-e2e2-e2e2-e2e2e2e2e2e2"
            );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertTenant();
    }

    @Test
    void shouldCreateCatalog() throws Exception {

        String requestBody =
                """
                {
                  "catalogCode": "FRAUD_REASON",
                  "catalogName": "Fraud Reason",
                  "description": "Controlled fraud reason catalog",
                  "organizationId": "%s",
                  "tenantId": "%s",
                  "status": "ACTIVE"
                }
                """.formatted(
                        ORGANIZATION_ID,
                        TENANT_ID
                );

        mockMvc.perform(
                        post("/api/v1/catalogs")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath("$.catalogId").exists()
                )
                .andExpect(
                        jsonPath("$.catalogCode")
                                .value("FRAUD_REASON")
                )
                .andExpect(
                        jsonPath("$.catalogName")
                                .value("Fraud Reason")
                )
                .andExpect(
                        jsonPath("$.organizationId")
                                .value(
                                        ORGANIZATION_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.tenantId")
                                .value(
                                        TENANT_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ACTIVE")
                )
                .andExpect(
                        jsonPath("$.createdAt").exists()
                );
    }

    @Test
    void shouldRejectInvalidCreateRequest()
            throws Exception {

        String requestBody =
                """
                {
                  "catalogCode": "",
                  "catalogName": "",
                  "status": ""
                }
                """;

        mockMvc.perform(
                        post("/api/v1/catalogs")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void shouldGetCatalogById()
            throws Exception {

        UUID catalogId =
                UUID.randomUUID();

        insertCatalog(
                catalogId,
                "CASE_STATUS",
                "Case Status",
                ORGANIZATION_ID,
                TENANT_ID,
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/catalogs/{catalogId}",
                                catalogId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.catalogId")
                                .value(
                                        catalogId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.catalogCode")
                                .value("CASE_STATUS")
                );
    }

    @Test
    void shouldGetCatalogByCode()
            throws Exception {

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

        mockMvc.perform(
                        get(
                                "/api/v1/catalogs/code/{catalogCode}",
                                "RISK_LEVEL"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.catalogId")
                                .value(
                                        catalogId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.catalogCode")
                                .value("RISK_LEVEL")
                );
    }

    @Test
    void shouldGetCatalogsByStatus()
            throws Exception {

        insertCatalog(
                UUID.randomUUID(),
                "STATUS_A",
                "Status A",
                ORGANIZATION_ID,
                TENANT_ID,
                "ACTIVE"
        );

        insertCatalog(
                UUID.randomUUID(),
                "STATUS_B",
                "Status B",
                ORGANIZATION_ID,
                TENANT_ID,
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/catalogs/status/{status}",
                                "ACTIVE"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].status")
                                .value("ACTIVE")
                )
                .andExpect(
                        jsonPath("$[1].status")
                                .value("ACTIVE")
                );
    }

    @Test
    void shouldGetCatalogsByOrganization()
            throws Exception {

        insertCatalog(
                UUID.randomUUID(),
                "ORG_A",
                "Organization A",
                ORGANIZATION_ID,
                TENANT_ID,
                "ACTIVE"
        );

        insertCatalog(
                UUID.randomUUID(),
                "ORG_B",
                "Organization B",
                ORGANIZATION_ID,
                TENANT_ID,
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/catalogs/organization/{organizationId}",
                                ORGANIZATION_ID
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].organizationId")
                                .value(
                                        ORGANIZATION_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$[1].organizationId")
                                .value(
                                        ORGANIZATION_ID.toString()
                                )
                );
    }

    @Test
    void shouldGetCatalogsByTenant()
            throws Exception {

        insertCatalog(
                UUID.randomUUID(),
                "TENANT_A",
                "Tenant A",
                ORGANIZATION_ID,
                TENANT_ID,
                "ACTIVE"
        );

        insertCatalog(
                UUID.randomUUID(),
                "TENANT_B",
                "Tenant B",
                ORGANIZATION_ID,
                TENANT_ID,
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/catalogs/tenant/{tenantId}",
                                TENANT_ID
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].tenantId")
                                .value(
                                        TENANT_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$[1].tenantId")
                                .value(
                                        TENANT_ID.toString()
                                )
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownCatalogId()
            throws Exception {

        UUID unknownCatalogId =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/catalogs/{catalogId}",
                                unknownCatalogId
                        )
                )
                .andExpect(
                        status().isNotFound()
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
                VALUES (?, ?, ?, ?, ?, ?, ?, clock_timestamp())
                """,
                catalogId,
                catalogCode,
                catalogName,
                "Controller integration test catalog",
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
                "EFS-CATALOG-CTRL-ORG",
                "EFS Catalog Controller Organization",
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
                "EFS-CATALOG-CTRL-TENANT",
                "EFS Catalog Controller Tenant",
                "ACTIVE",
                "TEST"
        );
    }
}