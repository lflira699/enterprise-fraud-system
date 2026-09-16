package com.efs.modules.reporting.export;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CsvReportExportRenderer
        implements ReportExportRenderer {

    @Override
    public ReportExportFormat getFormat() {

        return ReportExportFormat.CSV;
    }

    @Override
    public byte[] render(
            ReportExportDocument document) {

        try (
                StringWriter writer =
                        new StringWriter();

                CSVPrinter printer =
                        new CSVPrinter(
                                writer,
                                CSVFormat.DEFAULT
                        )
        ) {

            printer.printRecord(
                    "reportId",
                    document.reportId()
            );

            printer.printRecord(
                    "reportCode",
                    document.reportCode()
            );

            printer.printRecord(
                    "generatedAt",
                    document.generatedAt()
            );

            printer.printRecord(
                    "recordCount",
                    document.recordCount()
            );

            for (
                    Map.Entry<String, Object> entry :
                    document.criteria()
                            .entrySet()
            ) {

                printer.printRecord(
                        "criteria."
                                + entry.getKey(),
                        formatValue(
                                entry.getValue()
                        )
                );
            }

            printer.println();

            printer.printRecord(
                    document.columns()
            );

            for (
                    Map<String, Object> row :
                    document.rows()
            ) {

                printer.printRecord(
                        document.columns()
                                .stream()
                                .map(
                                        column ->
                                                formatValue(
                                                        row.get(
                                                                column
                                                        )
                                                )
                                )
                                .toList()
                );
            }

            printer.flush();

            return writer.toString()
                    .getBytes(
                            StandardCharsets.UTF_8
                    );

        }
        catch (IOException exception) {

            throw new IllegalStateException(
                    "CSV report export could not be rendered",
                    exception
            );
        }
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