package com.efs.modules.reporting.dto;

import java.util.List;

public class ReportDefinitionResponse {

    private final String reportCode;

    private final String category;

    private final List<String> allowedCriteria;

    public ReportDefinitionResponse(
            String reportCode,
            String category,
            List<String> allowedCriteria) {

        this.reportCode = reportCode;

        this.category = category;

        this.allowedCriteria =
                List.copyOf(
                        allowedCriteria
                );
    }

    public String getReportCode() {
        return reportCode;
    }

    public String getCategory() {
        return category;
    }

    public List<String> getAllowedCriteria() {
        return allowedCriteria;
    }
}
