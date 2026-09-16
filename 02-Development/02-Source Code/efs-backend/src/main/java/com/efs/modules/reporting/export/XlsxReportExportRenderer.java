package com.efs.modules.reporting.export;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class XlsxReportExportRenderer
        implements ReportExportRenderer {

    @Override
    public ReportExportFormat getFormat() {

        return ReportExportFormat.XLSX;
    }

    @Override
    public byte[] render(
            ReportExportDocument document) {

        try (
                XSSFWorkbook workbook =
                        new XSSFWorkbook();

                ByteArrayOutputStream output =
                        new ByteArrayOutputStream()
        ) {

            Sheet sheet =
                    workbook.createSheet(
                            "Report"
                    );

            CellStyle boldStyle =
                    workbook.createCellStyle();

            Font boldFont =
                    workbook.createFont();

            boldFont.setBold(
                    true
            );

            boldStyle.setFont(
                    boldFont
            );

            int rowIndex = 0;

            rowIndex =
                    metadataRow(
                            sheet,
                            rowIndex,
                            "reportId",
                            document.reportId(),
                            boldStyle
                    );

            rowIndex =
                    metadataRow(
                            sheet,
                            rowIndex,
                            "reportCode",
                            document.reportCode(),
                            boldStyle
                    );

            rowIndex =
                    metadataRow(
                            sheet,
                            rowIndex,
                            "generatedAt",
                            document.generatedAt(),
                            boldStyle
                    );

            rowIndex =
                    metadataRow(
                            sheet,
                            rowIndex,
                            "recordCount",
                            document.recordCount(),
                            boldStyle
                    );

            for (
                    Map.Entry<String, Object> entry :
                    document.criteria()
                            .entrySet()
            ) {

                rowIndex =
                        metadataRow(
                                sheet,
                                rowIndex,
                                "criteria."
                                        + entry.getKey(),
                                entry.getValue(),
                                boldStyle
                        );
            }

            rowIndex++;

            Row header =
                    sheet.createRow(
                            rowIndex++
                    );

            for (
                    int index = 0;
                    index < document.columns().size();
                    index++
            ) {

                Cell cell =
                        header.createCell(
                                index
                        );

                cell.setCellValue(
                        document.columns()
                                .get(
                                        index
                                )
                );

                cell.setCellStyle(
                        boldStyle
                );
            }

            for (
                    Map<String, Object> sourceRow :
                    document.rows()
            ) {

                Row row =
                        sheet.createRow(
                                rowIndex++
                        );

                for (
                        int index = 0;
                        index < document.columns().size();
                        index++
                ) {

                    String column =
                            document.columns()
                                    .get(
                                            index
                                    );

                    row.createCell(
                                    index
                            )
                            .setCellValue(
                                    formatValue(
                                            sourceRow.get(
                                                    column
                                            )
                                    )
                            );
                }
            }

            for (
                    int index = 0;
                    index < document.columns().size();
                    index++
            ) {

                sheet.autoSizeColumn(
                        index
                );

                int currentWidth =
                        sheet.getColumnWidth(
                                index
                        );

                int maximumWidth =
                        80 * 256;

                if (currentWidth > maximumWidth) {
                    sheet.setColumnWidth(
                            index,
                            maximumWidth
                    );
                }
            }

            workbook.write(
                    output
            );

            return output.toByteArray();

        }
        catch (IOException exception) {

            throw new IllegalStateException(
                    "XLSX report export could not be rendered",
                    exception
            );
        }
    }

    private int metadataRow(
            Sheet sheet,
            int rowIndex,
            String label,
            Object value,
            CellStyle boldStyle) {

        Row row =
                sheet.createRow(
                        rowIndex
                );

        Cell labelCell =
                row.createCell(
                        0
                );

        labelCell.setCellValue(
                label
        );

        labelCell.setCellStyle(
                boldStyle
        );

        row.createCell(
                        1
                )
                .setCellValue(
                        formatValue(
                                value
                        )
                );

        return rowIndex + 1;
    }

    private String formatValue(
            Object value) {

        if (value == null) {
            return "";
        }

        if (value instanceof Collection<?> collection) {

            return collection.stream()
                    .map(String::valueOf)
                    .collect(
                            Collectors.joining(
                                    ", "
                            )
                    );
        }

        if (value instanceof Map<?, ?> map) {

            return map.entrySet()
                    .stream()
                    .map(
                            entry ->
                                    String.valueOf(
                                            entry.getKey()
                                    )
                                            + "="
                                            + String.valueOf(
                                            entry.getValue()
                                    )
                    )
                    .collect(
                            Collectors.joining(
                                    ", "
                            )
                    );
        }

        return String.valueOf(
                value
        );
    }
}