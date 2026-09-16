package com.efs.modules.reporting.export;

import com.efs.modules.reporting.dto.GeneratedReportResponse;
import com.efs.shared.exception.ReportException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class ReportExportDocumentAssembler {

    private static final String
            OPERATIONAL_SUMMARY =
            "OPERATIONAL_SUMMARY";

    private static final String
            INVESTIGATION_CASES =
            "INVESTIGATION_CASES";

    private static final List<String>
            OPERATIONAL_SUMMARY_COLUMNS =
            List.of(
                    "generatedAt",
                    "dataStatus",
                    "criticalAlerts",
                    "openAlerts",
                    "openCases",
                    "closedCases",
                    "averageRiskScore",
                    "activatedDetectionScenarios",
                    "unavailableComponents",
                    "effectiveTenantId",
                    "effectiveComponents",
                    "filterSource"
            );

    private static final List<String>
            INVESTIGATION_CASE_COLUMNS =
            List.of(
                    "caseId",
                    "caseNumber",
                    "organizationId",
                    "transactionId",
                    "customerId",
                    "caseType",
                    "category",
                    "severity",
                    "priority",
                    "currentStatus",
                    "assignedTeam",
                    "assignedUser",
                    "createdAt",
                    "updatedAt",
                    "dueDate",
                    "closedAt",
                    "tenantId"
            );

    public ReportExportDocument assemble(
            GeneratedReportResponse report) {

        Objects.requireNonNull(
                report,
                "report is required"
        );

        if (
                OPERATIONAL_SUMMARY.equals(
                        report.getReportCode()
                )
        ) {
            return operationalSummary(
                    report
            );
        }

        if (
                INVESTIGATION_CASES.equals(
                        report.getReportCode()
                )
        ) {
            return investigationCases(
                    report
            );
        }

        throw exportFailure(
                "Generated report type cannot be exported"
        );
    }

    private ReportExportDocument
    operationalSummary(
            GeneratedReportResponse report) {

        Map<String, Object> content =
                requireContent(
                        report
                );

        Map<String, Object> effectiveFilters =
                optionalMap(
                        content.get(
                                "effectiveFilters"
                        )
                );

        Map<String, Object> row =
                new LinkedHashMap<>();

        row.put(
                "generatedAt",
                content.get(
                        "generatedAt"
                )
        );

        row.put(
                "dataStatus",
                content.get(
                        "dataStatus"
                )
        );

        row.put(
                "criticalAlerts",
                content.get(
                        "criticalAlerts"
                )
        );

        row.put(
                "openAlerts",
                content.get(
                        "openAlerts"
                )
        );

        row.put(
                "openCases",
                content.get(
                        "openCases"
                )
        );

        row.put(
                "closedCases",
                content.get(
                        "closedCases"
                )
        );

        row.put(
                "averageRiskScore",
                content.get(
                        "averageRiskScore"
                )
        );

        row.put(
                "activatedDetectionScenarios",
                content.get(
                        "activatedDetectionScenarios"
                )
        );

        row.put(
                "unavailableComponents",
                content.get(
                        "unavailableComponents"
                )
        );

        row.put(
                "effectiveTenantId",
                effectiveFilters.get(
                        "tenantId"
                )
        );

        row.put(
                "effectiveComponents",
                effectiveFilters.get(
                        "components"
                )
        );

        row.put(
                "filterSource",
                effectiveFilters.get(
                        "filterSource"
                )
        );

        return new ReportExportDocument(
                report.getReportId(),
                report.getReportCode(),
                report.getGeneratedAt(),
                safeCriteria(
                        report
                ),
                1L,
                OPERATIONAL_SUMMARY_COLUMNS,
                List.of(
                        row
                )
        );
    }

    private ReportExportDocument
    investigationCases(
            GeneratedReportResponse report) {

        Map<String, Object> content =
                requireContent(
                        report
                );

        Object casesValue =
                content.get(
                        "cases"
                );

        if (!(casesValue instanceof List<?> rawCases)) {
            throw exportFailure(
                    "Investigation case snapshot is unavailable"
            );
        }

        List<Map<String, Object>> rows =
                new ArrayList<>();

        for (Object rawCase : rawCases) {

            if (!(rawCase instanceof Map<?, ?> caseMap)) {
                throw exportFailure(
                        "Investigation case snapshot is invalid"
                );
            }

            Map<String, Object> row =
                    new LinkedHashMap<>();

            for (
                    String column :
                    INVESTIGATION_CASE_COLUMNS
            ) {
                row.put(
                        column,
                        caseMap.get(
                                column
                        )
                );
            }

            rows.add(
                    row
            );
        }

        return new ReportExportDocument(
                report.getReportId(),
                report.getReportCode(),
                report.getGeneratedAt(),
                safeCriteria(
                        report
                ),
                rows.size(),
                INVESTIGATION_CASE_COLUMNS,
                rows
        );
    }

    private Map<String, Object>
    requireContent(
            GeneratedReportResponse report) {

        if (report.getContent() == null) {
            throw exportFailure(
                    "Generated report snapshot is unavailable"
            );
        }

        return report.getContent();
    }

    private Map<String, Object>
    safeCriteria(
            GeneratedReportResponse report) {

        if (report.getCriteria() == null) {
            return Map.of();
        }

        return report.getCriteria();
    }

    private Map<String, Object>
    optionalMap(
            Object value) {

        if (value == null) {
            return Map.of();
        }

        if (!(value instanceof Map<?, ?> rawMap)) {
            throw exportFailure(
                    "Generated report snapshot is invalid"
            );
        }

        Map<String, Object> result =
                new LinkedHashMap<>();

        for (
                Map.Entry<?, ?> entry :
                rawMap.entrySet()
        ) {

            if (!(entry.getKey() instanceof String key)) {
                throw exportFailure(
                        "Generated report snapshot is invalid"
                );
            }

            result.put(
                    key,
                    entry.getValue()
            );
        }

        return result;
    }

    private ReportException exportFailure(
            String message) {

        return new ReportException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "REPORT_EXPORT_FAILED",
                message
        );
    }
}