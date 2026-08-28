package com.efs.modules.audit.service;

import com.efs.modules.audit.dto.AuditExportRequest;
import com.efs.modules.audit.dto.AuditExportResponse;
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
class AuditExportServiceIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "97979797-9797-9797-9797-979797979797"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "98989898-9898-9898-9898-989898989898"
            );

    private static final UUID RESOURCE_ID =
            UUID.fromString(
                    "99999999-9999-9999-9999-999999999999"
            );

    @Autowired
    private AuditExportServiceInterface auditExportService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertUser();
    }

    @Test
    void shouldCreateAndRetrieveAuditExportById() {

        AuditExportRequest request =
                new AuditExportRequest();

        request.setUserId(
                USER_ID
        );

        request.setOrganizationId(
                ORGANIZATION_ID
        );

        request.setExportType(
                "CASE_EXPORT"
        );

        request.setResourceType(
                "CASE"
        );

        request.setResourceId(
                RESOURCE_ID
        );

        request.setFileFormat(
                "CSV"
        );

        request.setRecordCount(
                25L
        );

        request.setExportReason(
                "Investigation evidence export"
        );

        AuditExportResponse created =
                auditExportService.createAuditExport(
                        request
                );

        assertNotNull(
                created
        );

        assertNotNull(
                created.getExportId()
        );

        assertEquals(
                USER_ID,
                created.getUserId()
        );

        assertEquals(
                ORGANIZATION_ID,
                created.getOrganizationId()
        );

        assertEquals(
                "CASE_EXPORT",
                created.getExportType()
        );

        assertEquals(
                "CASE",
                created.getResourceType()
        );

        assertEquals(
                RESOURCE_ID,
                created.getResourceId()
        );

        assertEquals(
                "CSV",
                created.getFileFormat()
        );

        assertEquals(
                25L,
                created.getRecordCount()
        );

        assertEquals(
                "Investigation evidence export",
                created.getExportReason()
        );

        assertNotNull(
                created.getExportedAt()
        );

        AuditExportResponse retrieved =
                auditExportService.getAuditExportById(
                        created.getExportId()
                );

        assertEquals(
                created.getExportId(),
                retrieved.getExportId()
        );
    }

    @Test
    void shouldAllowOptionalFieldsToBeNull() {

        AuditExportRequest request =
                new AuditExportRequest();

        request.setUserId(
                USER_ID
        );

        request.setOrganizationId(
                ORGANIZATION_ID
        );

        request.setExportType(
                "REPORT_EXPORT"
        );

        request.setResourceType(
                "REPORT"
        );

        request.setFileFormat(
                "PDF"
        );

        request.setRecordCount(
                1L
        );

        AuditExportResponse created =
                auditExportService.createAuditExport(
                        request
                );

        assertNotNull(
                created.getExportId()
        );

        assertNull(
                created.getResourceId()
        );

        assertNull(
                created.getExportReason()
        );

        assertNotNull(
                created.getExportedAt()
        );
    }

    @Test
    void shouldReturnAuditExportsByUserId() {

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

        List<AuditExportResponse> results =
                auditExportService.getAuditExportsByUserId(
                        USER_ID
                );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                results.stream()
                        .allMatch(
                                result ->
                                        USER_ID.equals(
                                                result.getUserId()
                                        )
                        )
        );
    }

    @Test
    void shouldReturnAuditExportsByOrganizationId() {

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

        List<AuditExportResponse> results =
                auditExportService.getAuditExportsByOrganizationId(
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
    void shouldReturnAuditExportsByExportType() {

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

        List<AuditExportResponse> results =
                auditExportService.getAuditExportsByExportType(
                        "CASE_EXPORT"
                );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                results.stream()
                        .allMatch(
                                result ->
                                        "CASE_EXPORT".equals(
                                                result.getExportType()
                                        )
                        )
        );
    }

    @Test
    void shouldReturnAuditExportsByResource() {

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

        List<AuditExportResponse> results =
                auditExportService.getAuditExportsByResource(
                        "CASE",
                        RESOURCE_ID
                );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                results.stream()
                        .allMatch(
                                result ->
                                        "CASE".equals(
                                                result.getResourceType()
                                        )
                                                &&
                                        RESOURCE_ID.equals(
                                                result.getResourceId()
                                        )
                        )
        );
    }

    @Test
    void shouldRejectUnknownAuditExportId() {

        UUID unknownExportId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        auditExportService.getAuditExportById(
                                unknownExportId
                        )
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
                "Integration test export"
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
                "EFS-AUDIT-EXPORT-ORG",
                "EFS Audit Export Organization",
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
                "efs.audit.export",
                "EFS Audit Export User",
                "efs.audit.export@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }
}