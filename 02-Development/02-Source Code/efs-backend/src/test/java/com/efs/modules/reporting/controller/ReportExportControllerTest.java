package com.efs.modules.reporting.controller;

import com.efs.modules.reporting.dto.ReportExportRequest;
import com.efs.modules.reporting.dto.ReportExportResult;
import com.efs.modules.reporting.service.ReportExportServiceInterface;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReportExportControllerTest {

    @Test
    void returnsAttachmentWithSelectedMediaType() {

        ReportExportServiceInterface service =
                mock(
                        ReportExportServiceInterface.class
                );

        SecurityContextProvider provider =
                mock(
                        SecurityContextProvider.class
                );

        SecurityContext securityContext =
                new SecurityContext(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        Set.of(),
                        Set.of(
                                "report.export",
                                "report.view",
                                "case.view"
                        ),
                        Set.of()
                );

        UUID reportId =
                UUID.randomUUID();

        ReportExportRequest request =
                new ReportExportRequest();

        request.setFormat(
                "CSV"
        );

        byte[] bytes =
                "report".getBytes(
                        StandardCharsets.UTF_8
                );

        ReportExportResult result =
                new ReportExportResult(
                        bytes,
                        "text/csv; charset=UTF-8",
                        "efs-report-investigation-cases-"
                                + reportId
                                + ".csv",
                        "CSV",
                        1L
                );

        when(
                provider.getCurrentContext()
        ).thenReturn(
                securityContext
        );

        when(
                service.exportReport(
                        reportId,
                        request,
                        securityContext
                )
        ).thenReturn(
                result
        );

        ReportExportController controller =
                new ReportExportController(
                        service,
                        provider
                );

        ResponseEntity<byte[]> response =
                controller.exportReport(
                        reportId,
                        request
                );

        assertEquals(
                200,
                response.getStatusCode()
                        .value()
        );

        assertEquals(
                "text/csv;charset=UTF-8",
                response.getHeaders()
                        .getContentType()
                        .toString()
        );

        assertEquals(
                result.getFileName(),
                response.getHeaders()
                        .getContentDisposition()
                        .getFilename()
        );

        assertArrayEquals(
                bytes,
                response.getBody()
        );
    }
}