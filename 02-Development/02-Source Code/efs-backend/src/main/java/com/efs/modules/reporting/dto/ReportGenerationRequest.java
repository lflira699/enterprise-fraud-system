package com.efs.modules.reporting.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReportGenerationRequest {

    private String reportCode;

    private ReportCriteriaRequest criteria;

    private final Map<String, Object>
            unsupportedFields =
            new LinkedHashMap<>();

    public String getReportCode() {
        return reportCode;
    }

    public void setReportCode(
            String reportCode) {

        this.reportCode = reportCode;
    }

    public ReportCriteriaRequest getCriteria() {
        return criteria;
    }

    public void setCriteria(
            ReportCriteriaRequest criteria) {

        this.criteria = criteria;
    }

    @JsonAnySetter
    public void captureUnsupportedField(
            String name,
            Object value) {

        unsupportedFields.put(
                name,
                value
        );
    }

    @JsonIgnore
    public Map<String, Object>
    getUnsupportedFields() {

        return unsupportedFields;
    }
}
