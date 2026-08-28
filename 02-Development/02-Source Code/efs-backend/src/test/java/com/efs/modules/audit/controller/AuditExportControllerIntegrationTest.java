package com.efs.modules.audit.controller;

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

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuditExportControllerIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "a1a1a1a1-a1a1-a1a1-a1a1-a1a1a1a1a1a1"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "a2a2a2a2-a2a2-a2a2-a2a2-a2a2a2a2a2a2"
            );

    private static final UUID RESOURCE_ID =
            UUID.fromString(
                    "a3a3a3a3-a3a3-a3a3-a3a3-a3a3a3a3a3a3"
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
        insertUser();
    }

    @Test
    void shouldCreateAuditExportThroughApi()
            throws Exception {

        Map<String, Object> request =
                Map.ofEntries(
                        Map.entry(
                                "userId",
                                USER_ID.toString()
                        ),
                        Map.entry(
                                "organizationId",
                                ORGANIZATION_ID.toString()
                        ),
                        Map.entry(
                                "exportType",
                                "CASE_EXPORT"
                        ),
                        Map.entry(
                                "resourceType",
                                "CASE"
                        ),
                        Map.entry(
                                "resourceId",
                                RESOURCE_ID.toString()
                        ),
                        Map.entry(
                                "fileFormat",
                                "CSV"
                        ),
                        Map.entry(
                                "recordCount",
                                25L
                        ),
                        Map.entry(
                                "exportReason",
                                "Investigation evidence export"
                        )
                );

        mockMvc.perform(
                        post(
                                "/api/v1/audit/exports"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath("$.exportId").exists()
                )
                .andExpect(
                        jsonPath("$.userId")
                                .value(
                                        USER_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.organizationId")
                                .value(
                                        ORGANIZATION_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.exportType")
                                .value(
                                        "CASE_EXPORT"
                                )
                )
                .andExpect(
                        jsonPath("$.resourceType")
                                .value(
                                        "CASE"
                                )
                )
                .andExpect(
                        jsonPath("$.resourceId")
                                .value(
                                        RESOURCE_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.fileFormat")
                                .value(
                                        "CSV"
                                )
                )
                .andExpect(
                        jsonPath("$.recordCount")
                                .value(
                                        25
                                )
                )
                .andExpect(
                        jsonPath("$.exportReason")
                                .value(
                                        "Investigation evidence export"
                                )
                )
                .andExpect(
                        jsonPath("$.exportedAt").exists()
                );
    }

    @Test
    void shouldRetrieveAuditExportByIdThroughApi()
            throws Exception {

        UUID exportId =
                UUID.randomUUID();

        insertAuditExport(
                exportId,
                USER_ID,
                ORGANIZATION_ID,
                "CASE_EXPORT",
                "CASE",
                RESOURCE_ID
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/exports/{exportId}",
                                exportId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.exportId")
                                .value(
                                        exportId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.userId")
                                .value(
                                        USER_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.organizationId")
                                .value(
                                        ORGANIZATION_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.exportType")
                                .value(
                                        "CASE_EXPORT"
                                )
                )
                .andExpect(
                        jsonPath("$.resourceType")
                                .value(
                                        "CASE"
                                )
                )
                .andExpect(
                        jsonPath("$.resourceId")
                                .value(
                                        RESOURCE_ID.toString()
                                )
                );
    }

    @Test
    void shouldRetrieveAuditExportsByUserIdThroughApi()
            throws Exception {

        insertAuditExport(
                UUID.randomUUID(),
                USER_ID,
                ORGANIZATION_ID,
                "CASE_EXPORT",
                "CASE",
                RESOURCE_ID
        );

        insertAuditExport(
                UUID.randomUUID(),
                USER_ID,
                ORGANIZATION_ID,
                "REPORT_EXPORT",
                "REPORT",
                UUID.randomUUID()
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/exports/user/{userId}",
                                USER_ID
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
                        jsonPath("$[0].userId")
                                .value(
                                        USER_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$[1].userId")
                                .value(
                                        USER_ID.toString()
                                )
                );
    }

    @Test
    void shouldRetrieveAuditExportsByOrganizationIdThroughApi()
            throws Exception {

        insertAuditExport(
                UUID.randomUUID(),
                USER_ID,
                ORGANIZATION_ID,
                "CASE_EXPORT",
                "CASE",
                RESOURCE_ID
        );

        insertAuditExport(
                UUID.randomUUID(),
                USER_ID,
                ORGANIZATION_ID,
                "REPORT_EXPORT",
                "REPORT",
                UUID.randomUUID()
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/exports/organization/{organizationId}",
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
    void shouldRetrieveAuditExportsByExportTypeThroughApi()
            throws Exception {

        insertAuditExport(
                UUID.randomUUID(),
                USER_ID,
                ORGANIZATION_ID,
                "CASE_EXPORT",
                "CASE",
                RESOURCE_ID
        );

        insertAuditExport(
                UUID.randomUUID(),
                USER_ID,
                ORGANIZATION_ID,
                "CASE_EXPORT",
                "CASE",
                UUID.randomUUID()
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/exports/type/{exportType}",
                                "CASE_EXPORT"
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
                        jsonPath("$[0].exportType")
                                .value(
                                        "CASE_EXPORT"
                                )
                )
                .andExpect(
                        jsonPath("$[1].exportType")
                                .value(
                                        "CASE_EXPORT"
                                )
                );
    }

    @Test
    void shouldRetrieveAuditExportsByResourceThroughApi()
            throws Exception {

        insertAuditExport(
                UUID.randomUUID(),
                USER_ID,
                ORGANIZATION_ID,
                "CASE_EXPORT",
                "CASE",
                RESOURCE_ID
        );

        insertAuditExport(
                UUID.randomUUID(),
                USER_ID,
                ORGANIZATION_ID,
                "CASE_EXPORT",
                "CASE",
                RESOURCE_ID
        );

        mockMvc.perform(
                        get(
                                "/api/v1/audit/exports/resource"
                        )
                                .param(
                                        "resourceType",
                                        "CASE"
                                )
                                .param(
                                        "resourceId",
                                        RESOURCE_ID.toString()
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
                        jsonPath("$[0].resourceType")
                                .value(
                                        "CASE"
                                )
                )
                .andExpect(
                        jsonPath("$[0].resourceId")
                                .value(
                                        RESOURCE_ID.toString()
                                )
                )
                .andExpect(
                        jsonPath("$[1].resourceType")
                                .value(
                                        "CASE"
                                )
                )
                .andExpect(
                        jsonPath("$[1].resourceId")
                                .value(
                                        RESOURCE_ID.toString()
                                )
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownAuditExportId()
            throws Exception {

        UUID unknownExportId =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/audit/exports/{exportId}",
                                unknownExportId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    private void insertAuditExport(
            UUID exportId,
            UUID userId,
            UUID organizationId,
            String exportType,
            String resourceType,
            UUID resourceId) {

        jdbcTemplate.update(
                """
                INSERT INTO audit.audit_export (
                    export_id,
                    user_id,
                    organization_id,
                    export_type,
                    resource_type,
                    resource_id,
                    file_format,
                    record_count,
                    export_reason,
                    exported_at
                )
                VALUES (
                    ?,
                    ?,
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
                exportId,
                userId,
                organizationId,
                exportType,
                resourceType,
                resourceId,
                "CSV",
                10L,
                "Controller integration test export"
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
                "EFS-AUDIT-EXPORT-API-ORG",
                "EFS Audit Export API Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );
    }

    private void insertUser() {

        jdbcTemplate.update(
                """
                INSERT INTO administration.user_account (
                    user_id,
                    organization_id,
                    username,
                    full_name,
                    email,
                    authentication_provider,
                    mfa_enabled,
                    account_status,
                    failed_login_attempts
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                USER_ID,
                ORGANIZATION_ID,
                "efs.audit.export.api",
                "EFS Audit Export API User",
                "efs.audit.export.api@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }
}