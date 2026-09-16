package com.efs.modules.reporting.export;

import com.efs.modules.reporting.dto.GeneratedReportResponse;
import com.efs.shared.exception.ReportException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReportExportDocumentAssemblerTest {

    private final ReportExportDocumentAssembler
            assembler =
            new ReportExportDocumentAssembler();

    @Test
    void assemblesOperationalSummaryWithControlledColumns() {

        UUID reportId =
                UUID.randomUUID();

        UUID organizationId =
                UUID.randomUUID();

        UUID tenantId =
                UUID.randomUUID();

        LocalDateTime generatedAt =
                LocalDateTime.of(
                        2026,
                        9,
                        16,
                        6,
                        0
                );

        Map<String, Object> filters =
                new LinkedHashMap<>();

        filters.put(
                "tenantId",
                tenantId
        );

        filters.put(
                "components",
                List.of(
                        "ALERT",
                        "CASE"
                )
        );

        filters.put(
                "filterSource",
                "EXPLICIT"
        );

        Map<String, Object> content =
                new LinkedHashMap<>();

        content.put(
                "generatedAt",
                generatedAt
        );

        content.put(
                "dataStatus",
                "COMPLETE"
        );

        content.put(
                "criticalAlerts",
                3L
        );

        content.put(
                "openAlerts",
                7L
        );

        content.put(
                "openCases",
                5L
        );

        content.put(
                "closedCases",
                11L
        );

        content.put(
                "averageRiskScore",
                42.5
        );

        content.put(
                "activatedDetectionScenarios",
                4L
        );

        content.put(
                "unavailableComponents",
                List.of()
        );

        content.put(
                "effectiveFilters",
                filters
        );

        GeneratedReportResponse report =
                new GeneratedReportResponse(
                        reportId,
                        organizationId,
                        tenantId,
                        "OPERATIONAL_SUMMARY",
                        Map.of(
                                "tenantId",
                                tenantId
                        ),
                        content,
                        UUID.randomUUID(),
                        generatedAt
                );

        ReportExportDocument document =
                assembler.assemble(
                        report
                );

        assertEquals(
                reportId,
                document.reportId()
        );

        assertEquals(
                "OPERATIONAL_SUMMARY",
                document.reportCode()
        );

        assertEquals(
                1L,
                document.recordCount()
        );

        assertEquals(
                12,
                document.columns().size()
        );

        assertEquals(
                1,
                document.rows().size()
        );

        assertEquals(
                3L,
                document.rows()
                        .getFirst()
                        .get(
                                "criticalAlerts"
                        )
        );

        assertEquals(
                tenantId,
                document.rows()
                        .getFirst()
                        .get(
                                "effectiveTenantId"
                        )
        );

        assertEquals(
                List.of(
                        "ALERT",
                        "CASE"
                ),
                document.rows()
                        .getFirst()
                        .get(
                                "effectiveComponents"
                        )
        );
    }

    @Test
    void assemblesInvestigationCasesUsingPersistedSnapshotOnly() {

        UUID reportId =
                UUID.randomUUID();

        UUID organizationId =
                UUID.randomUUID();

        UUID tenantId =
                UUID.randomUUID();

        UUID caseId =
                UUID.randomUUID();

        LocalDateTime generatedAt =
                LocalDateTime.of(
                        2026,
                        9,
                        16,
                        6,
                        5
                );

        Map<String, Object> caseSnapshot =
                new LinkedHashMap<>();

        caseSnapshot.put(
                "caseId",
                caseId
        );

        caseSnapshot.put(
                "caseNumber",
                "CASE-0001"
        );

        caseSnapshot.put(
                "organizationId",
                organizationId
        );

        caseSnapshot.put(
                "transactionId",
                null
        );

        caseSnapshot.put(
                "customerId",
                null
        );

        caseSnapshot.put(
                "caseType",
                "FRAUD"
        );

        caseSnapshot.put(
                "category",
                "ACCOUNT"
        );

        caseSnapshot.put(
                "severity",
                "HIGH"
        );

        caseSnapshot.put(
                "priority",
                "HIGH"
        );

        caseSnapshot.put(
                "currentStatus",
                "OPEN"
        );

        caseSnapshot.put(
                "assignedTeam",
                null
        );

        caseSnapshot.put(
                "assignedUser",
                null
        );

        caseSnapshot.put(
                "createdAt",
                generatedAt.minusDays(1)
        );

        caseSnapshot.put(
                "updatedAt",
                generatedAt
        );

        caseSnapshot.put(
                "dueDate",
                null
        );

        caseSnapshot.put(
                "closedAt",
                null
        );

        caseSnapshot.put(
                "tenantId",
                tenantId
        );

        Map<String, Object> content =
                new LinkedHashMap<>();

        content.put(
                "recordCount",
                1
        );

        content.put(
                "cases",
                List.of(
                        caseSnapshot
                )
        );

        GeneratedReportResponse report =
                new GeneratedReportResponse(
                        reportId,
                        organizationId,
                        tenantId,
                        "INVESTIGATION_CASES",
                        Map.of(
                                "priority",
                                "HIGH"
                        ),
                        content,
                        UUID.randomUUID(),
                        generatedAt
                );

        ReportExportDocument document =
                assembler.assemble(
                        report
                );

        assertEquals(
                1L,
                document.recordCount()
        );

        assertEquals(
                17,
                document.columns().size()
        );

        assertEquals(
                caseId,
                document.rows()
                        .getFirst()
                        .get(
                                "caseId"
                        )
        );

        assertEquals(
                "CASE-0001",
                document.rows()
                        .getFirst()
                        .get(
                                "caseNumber"
                        )
        );

        assertEquals(
                null,
                document.rows()
                        .getFirst()
                        .get(
                                "closedAt"
                        )
        );
    }

    @Test
    void rejectsUnsupportedGeneratedReportType() {

        GeneratedReportResponse report =
                new GeneratedReportResponse(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        null,
                        "UNSUPPORTED_REPORT",
                        Map.of(),
                        Map.of(),
                        UUID.randomUUID(),
                        LocalDateTime.now()
                );

        ReportException exception =
                assertThrows(
                        ReportException.class,
                        () ->
                                assembler.assemble(
                                        report
                                )
                );

        assertEquals(
                "REPORT_EXPORT_FAILED",
                exception.getErrorCode()
        );
    }
}