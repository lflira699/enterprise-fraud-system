package com.efs.modules.reporting.dto;

import java.util.List;
import java.util.UUID;

public class ReportExportOptionsResponse {

    private final UUID reportId;

    private final String reportCode;

    private final List<String>
            formats;

    public ReportExportOptionsResponse(
            UUID reportId,
            String reportCode,
            List<String> formats) {

        this.reportId =
                reportId;

        this.reportCode =
                reportCode;

        this.formats =
                List.copyOf(
                        formats
                );
    }

    public UUID getReportId() {
        return reportId;
    }

    public String getReportCode() {
        return reportCode;
    }

    public List<String> getFormats() {
        return formats;
    }
}