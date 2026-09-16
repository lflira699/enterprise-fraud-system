package com.efs.modules.reporting.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReportExportRequest {

    private String format;

    private final Map<String, Object>
            unsupportedFields =
            new LinkedHashMap<>();

    public String getFormat() {
        return format;
    }

    public void setFormat(
            String format) {

        this.format = format;
    }

    @JsonAnySetter
    public void addUnsupportedField(
            String fieldName,
            Object value) {

        unsupportedFields.put(
                fieldName,
                value
        );
    }

    public Map<String, Object>
    getUnsupportedFields() {

        return Map.copyOf(
                unsupportedFields
        );
    }

    public boolean hasUnsupportedFields() {

        return !unsupportedFields.isEmpty();
    }
}