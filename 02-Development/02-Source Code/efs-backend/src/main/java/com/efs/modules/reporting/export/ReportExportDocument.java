package com.efs.modules.reporting.export;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record ReportExportDocument(
        UUID reportId,
        String reportCode,
        LocalDateTime generatedAt,
        Map<String, Object> criteria,
        long recordCount,
        List<String> columns,
        List<Map<String, Object>> rows) {

    public ReportExportDocument {

        Objects.requireNonNull(
                reportId,
                "reportId is required"
        );

        Objects.requireNonNull(
                reportCode,
                "reportCode is required"
        );

        Objects.requireNonNull(
                generatedAt,
                "generatedAt is required"
        );

        Objects.requireNonNull(
                criteria,
                "criteria is required"
        );

        Objects.requireNonNull(
                columns,
                "columns are required"
        );

        Objects.requireNonNull(
                rows,
                "rows are required"
        );

        criteria =
                Collections.unmodifiableMap(
                        new LinkedHashMap<>(
                                criteria
                        )
                );

        columns =
                List.copyOf(
                        columns
                );

        rows =
                rows.stream()
                        .map(
                                row ->
                                        Collections.unmodifiableMap(
                                                new LinkedHashMap<>(
                                                        Objects.requireNonNull(
                                                                row,
                                                                "row is required"
                                                        )
                                                )
                                        )
                        )
                        .toList();
    }
}