package com.efs.modules.reporting.export;

public interface ReportExportRenderer {

    ReportExportFormat getFormat();

    byte[] render(
            ReportExportDocument document
    );
}