package com.efs.modules.reporting.export;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public enum ReportExportFormat {

    PDF(
            "application/pdf",
            "pdf"
    ),

    XLSX(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "xlsx"
    ),

    CSV(
            "text/csv; charset=UTF-8",
            "csv"
    );

    private final String mediaType;

    private final String extension;

    ReportExportFormat(
            String mediaType,
            String extension) {

        this.mediaType =
                mediaType;

        this.extension =
                extension;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getExtension() {
        return extension;
    }

    public static Optional<ReportExportFormat>
    from(
            String value) {

        if (
                value == null
                        || value.isBlank()
        ) {
            return Optional.empty();
        }

        String normalized =
                value.trim()
                        .toUpperCase(
                                Locale.ROOT
                        );

        return Arrays.stream(
                        values()
                )
                .filter(
                        format ->
                                format.name()
                                        .equals(
                                                normalized
                                        )
                )
                .findFirst();
    }

    public static List<String>
    supportedNames() {

        return Arrays.stream(
                        values()
                )
                .map(Enum::name)
                .toList();
    }
}