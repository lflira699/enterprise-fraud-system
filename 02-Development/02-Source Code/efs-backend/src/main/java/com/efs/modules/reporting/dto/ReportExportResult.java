package com.efs.modules.reporting.dto;

import java.util.Objects;

public class ReportExportResult {

    private final byte[] content;

    private final String mediaType;

    private final String fileName;

    private final String format;

    private final long recordCount;

    public ReportExportResult(
            byte[] content,
            String mediaType,
            String fileName,
            String format,
            long recordCount) {

        this.content =
                Objects.requireNonNull(
                        content,
                        "content is required"
                ).clone();

        this.mediaType =
                Objects.requireNonNull(
                        mediaType,
                        "mediaType is required"
                );

        this.fileName =
                Objects.requireNonNull(
                        fileName,
                        "fileName is required"
                );

        this.format =
                Objects.requireNonNull(
                        format,
                        "format is required"
                );

        this.recordCount =
                recordCount;
    }

    public byte[] getContent() {
        return content.clone();
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFormat() {
        return format;
    }

    public long getRecordCount() {
        return recordCount;
    }
}