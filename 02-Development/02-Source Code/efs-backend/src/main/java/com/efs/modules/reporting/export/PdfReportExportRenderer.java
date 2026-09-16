package com.efs.modules.reporting.export;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PdfReportExportRenderer
        implements ReportExportRenderer {

    private static final float
            MARGIN =
            48.0f;

    private static final float
            FONT_SIZE =
            9.0f;

    private static final float
            TITLE_FONT_SIZE =
            14.0f;

    private static final float
            LEADING =
            13.0f;

    private static final int
            MAX_LINE_CHARACTERS =
            95;

    private final PDFont regularFont =
            new PDType1Font(
                    Standard14Fonts.FontName.HELVETICA
            );

    private final PDFont boldFont =
            new PDType1Font(
                    Standard14Fonts.FontName.HELVETICA_BOLD
            );

    @Override
    public ReportExportFormat getFormat() {

        return ReportExportFormat.PDF;
    }

    @Override
    public byte[] render(
            ReportExportDocument document) {

        try (
                PDDocument pdf =
                        new PDDocument();

                ByteArrayOutputStream output =
                        new ByteArrayOutputStream()
        ) {

            PdfWriter writer =
                    new PdfWriter(
                            pdf,
                            regularFont,
                            boldFont
                    );

            writer.writeTitle(
                    "Enterprise Fraud System - Report Export"
            );

            writer.writeField(
                    "Report ID",
                    document.reportId()
            );

            writer.writeField(
                    "Report Code",
                    document.reportCode()
            );

            writer.writeField(
                    "Generated At",
                    document.generatedAt()
            );

            writer.writeField(
                    "Record Count",
                    document.recordCount()
            );

            if (!document.criteria().isEmpty()) {

                writer.writeSection(
                        "Criteria"
                );

                for (
                        Map.Entry<String, Object> entry :
                        document.criteria()
                                .entrySet()
                ) {

                    writer.writeField(
                            entry.getKey(),
                            formatValue(
                                    entry.getValue()
                            )
                    );
                }
            }

            writer.writeSection(
                    "Report Data"
            );

            int recordNumber = 1;

            for (
                    Map<String, Object> row :
                    document.rows()
            ) {

                if (document.rows().size() > 1) {

                    writer.writeSubsection(
                            "Record "
                                    + recordNumber
                    );
                }

                for (
                        String column :
                        document.columns()
                ) {

                    writer.writeField(
                            column,
                            formatValue(
                                    row.get(
                                            column
                                    )
                            )
                    );
                }

                writer.writeSpacer();

                recordNumber++;
            }

            writer.close();

            pdf.save(
                    output
            );

            return output.toByteArray();

        }
        catch (IOException exception) {

            throw new IllegalStateException(
                    "PDF report export could not be rendered",
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

    private static final class PdfWriter {

        private final PDDocument document;

        private final PDFont regularFont;

        private final PDFont boldFont;

        private PDPageContentStream stream;

        private float y;

        private PdfWriter(
                PDDocument document,
                PDFont regularFont,
                PDFont boldFont)
                throws IOException {

            this.document =
                    document;

            this.regularFont =
                    regularFont;

            this.boldFont =
                    boldFont;

            newPage();
        }

        private void writeTitle(
                String title)
                throws IOException {

            writeWrapped(
                    title,
                    boldFont,
                    TITLE_FONT_SIZE
            );

            writeSpacer();
        }

        private void writeSection(
                String title)
                throws IOException {

            writeSpacer();

            writeWrapped(
                    title,
                    boldFont,
                    FONT_SIZE + 2.0f
            );
        }

        private void writeSubsection(
                String title)
                throws IOException {

            writeWrapped(
                    title,
                    boldFont,
                    FONT_SIZE
            );
        }

        private void writeField(
                String label,
                Object value)
                throws IOException {

            writeWrapped(
                    label
                            + ": "
                            + sanitize(
                                    String.valueOf(
                                            value == null
                                                    ? ""
                                                    : value
                                    )
                            ),
                    regularFont,
                    FONT_SIZE
            );
        }

        private void writeSpacer()
                throws IOException {

            ensureSpace(
                    LEADING
            );

            y -= LEADING / 2.0f;
        }

        private void writeWrapped(
                String text,
                PDFont font,
                float fontSize)
                throws IOException {

            List<String> lines =
                    wrap(
                            sanitize(
                                    text
                            )
                    );

            for (String line : lines) {

                ensureSpace(
                        LEADING
                );

                stream.beginText();

                stream.setFont(
                        font,
                        fontSize
                );

                stream.newLineAtOffset(
                        MARGIN,
                        y
                );

                stream.showText(
                        line
                );

                stream.endText();

                y -= LEADING;
            }
        }

        private void ensureSpace(
                float required)
                throws IOException {

            if (
                    y - required
                            < MARGIN
            ) {
                newPage();
            }
        }

        private void newPage()
                throws IOException {

            if (stream != null) {
                stream.close();
            }

            PDPage page =
                    new PDPage(
                            PDRectangle.LETTER
                    );

            document.addPage(
                    page
            );

            stream =
                    new PDPageContentStream(
                            document,
                            page
                    );

            y =
                    page.getMediaBox()
                            .getHeight()
                            - MARGIN;
        }

        private void close()
                throws IOException {

            if (stream != null) {
                stream.close();
                stream = null;
            }
        }

        private List<String> wrap(
                String text) {

            List<String> lines =
                    new ArrayList<>();

            if (
                    text == null
                            || text.isEmpty()
            ) {
                lines.add(
                        ""
                );

                return lines;
            }

            String[] words =
                    text.split(
                            "\\s+"
                    );

            StringBuilder current =
                    new StringBuilder();

            for (String word : words) {

                if (
                        current.length() == 0
                                && word.length()
                                > MAX_LINE_CHARACTERS
                ) {

                    int position = 0;

                    while (
                            position
                                    < word.length()
                    ) {

                        int end =
                                Math.min(
                                        position
                                                + MAX_LINE_CHARACTERS,
                                        word.length()
                                );

                        lines.add(
                                word.substring(
                                        position,
                                        end
                                )
                        );

                        position = end;
                    }

                    continue;
                }

                int candidateLength =
                        current.length()
                                + (
                                current.length() == 0
                                        ? 0
                                        : 1
                        )
                                + word.length();

                if (
                        candidateLength
                                > MAX_LINE_CHARACTERS
                ) {

                    lines.add(
                            current.toString()
                    );

                    current =
                            new StringBuilder(
                                    word
                            );
                }
                else {

                    if (current.length() > 0) {
                        current.append(
                                ' '
                        );
                    }

                    current.append(
                            word
                    );
                }
            }

            if (current.length() > 0) {
                lines.add(
                        current.toString()
                );
            }

            return lines;
        }

        private String sanitize(
                String value) {

            if (value == null) {
                return "";
            }

            String normalized =
                    value.replace(
                                    '\r',
                                    ' '
                            )
                            .replace(
                                    '\n',
                                    ' '
                            )
                            .replace(
                                    '\t',
                                    ' '
                            );

            StringBuilder result =
                    new StringBuilder(
                            normalized.length()
                    );

            for (
                    int index = 0;
                    index < normalized.length();
                    index++
            ) {

                char character =
                        normalized.charAt(
                                index
                        );

                if (
                        character >= 32
                                && character <= 255
                ) {
                    result.append(
                            character
                    );
                }
                else {
                    result.append(
                            '?'
                    );
                }
            }

            return result.toString();
        }
    }
}