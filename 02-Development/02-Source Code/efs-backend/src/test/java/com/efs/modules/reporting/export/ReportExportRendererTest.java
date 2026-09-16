package com.efs.modules.reporting.export;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportExportRendererTest {

    private ReportExportDocument document() {

        Map<String, Object> row =
                new LinkedHashMap<>();

        row.put(
                "caseNumber",
                "CASE-0001"
        );

        row.put(
                "priority",
                "HIGH"
        );

        row.put(
                "assignedTeam",
                null
        );

        return new ReportExportDocument(
                UUID.randomUUID(),
                "INVESTIGATION_CASES",
                LocalDateTime.of(
                        2026,
                        9,
                        16,
                        6,
                        10
                ),
                Map.of(
                        "priority",
                        "HIGH"
                ),
                1L,
                List.of(
                        "caseNumber",
                        "priority",
                        "assignedTeam"
                ),
                List.of(
                        row
                )
        );
    }

    @Test
    void rendersCsv() {

        byte[] content =
                new CsvReportExportRenderer()
                        .render(
                                document()
                        );

        String csv =
                new String(
                        content,
                        StandardCharsets.UTF_8
                );

        assertTrue(
                csv.contains(
                        "INVESTIGATION_CASES"
                )
        );

        assertTrue(
                csv.contains(
                        "caseNumber,priority,assignedTeam"
                )
        );

        assertTrue(
                csv.contains(
                        "CASE-0001,HIGH"
                )
        );
    }

    @Test
    void rendersXlsx() throws Exception {

        byte[] content =
                new XlsxReportExportRenderer()
                        .render(
                                document()
                        );

        assertTrue(
                content.length > 0
        );

        try (
                XSSFWorkbook workbook =
                        new XSSFWorkbook(
                                new ByteArrayInputStream(
                                        content
                                )
                        )
        ) {

            assertEquals(
                    "Report",
                    workbook.getSheetAt(
                            0
                    ).getSheetName()
            );

            assertTrue(
                    workbook.getSheetAt(
                            0
                    ).getPhysicalNumberOfRows()
                            > 0
            );
        }
    }

    @Test
    void rendersPdf() {

        byte[] content =
                new PdfReportExportRenderer()
                        .render(
                                document()
                        );

        assertTrue(
                content.length > 4
        );

        String signature =
                new String(
                        content,
                        0,
                        4,
                        StandardCharsets.US_ASCII
                );

        assertEquals(
                "%PDF",
                signature
        );
    }
}