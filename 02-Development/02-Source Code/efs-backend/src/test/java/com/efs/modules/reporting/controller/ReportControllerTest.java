package com.efs.modules.reporting.controller;

import com.efs.modules.reporting.dto.GeneratedReportResponse;
import com.efs.modules.reporting.dto.ReportDefinitionResponse;
import com.efs.modules.reporting.dto.ReportGenerationRequest;
import com.efs.modules.reporting.service.ReportServiceInterface;
import com.efs.shared.exception.GlobalExceptionHandler;
import com.efs.shared.exception.ReportException;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    private ReportServiceInterface
            reportService;

    @Mock
    private SecurityContextProvider
            securityContextProvider;

    @Mock
    private SecurityContext
            securityContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        ReportController controller =
                new ReportController(
                        reportService,
                        securityContextProvider
                );

        mockMvc =
                MockMvcBuilders
                        .standaloneSetup(
                                controller
                        )
                        .setControllerAdvice(
                                new GlobalExceptionHandler()
                        )
                        .build();

        when(
                securityContextProvider
                        .getCurrentContext()
        ).thenReturn(
                securityContext
        );
    }

    @Test
    void shouldExposeAvailableDefinitions()
            throws Exception {

        when(
                reportService
                        .getAvailableDefinitions(
                                securityContext
                        )
        ).thenReturn(
                List.of(
                        new ReportDefinitionResponse(
                                "OPERATIONAL_SUMMARY",
                                "OPERATIONAL",
                                List.of(
                                        "tenantId",
                                        "components"
                                )
                        )
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/reports/definitions"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$[0].reportCode"
                        ).value(
                                "OPERATIONAL_SUMMARY"
                        )
                )
                .andExpect(
                        jsonPath(
                                "$[0].category"
                        ).value(
                                "OPERATIONAL"
                        )
                );
    }

    @Test
    void shouldGenerateReportWith201()
            throws Exception {

        UUID reportId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(
                reportService.generateReport(
                        any(ReportGenerationRequest.class),
                        eq(securityContext)
                )
        ).thenReturn(
                new GeneratedReportResponse(
                        reportId,
                        organizationId,
                        tenantId,
                        "OPERATIONAL_SUMMARY",
                        Map.of(
                                "components",
                                List.of("ALERT")
                        ),
                        Map.of(
                                "openAlerts",
                                5
                        ),
                        userId,
                        LocalDateTime.now()
                )
        );

        mockMvc.perform(
                        post(
                                "/api/v1/reports"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "reportCode": "OPERATIONAL_SUMMARY",
                                          "criteria": {
                                            "components": ["ALERT"]
                                          }
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath("$.reportId")
                                .value(
                                        reportId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.reportCode")
                                .value(
                                        "OPERATIONAL_SUMMARY"
                                )
                );
    }

    @Test
    void shouldRetrieveGeneratedReport()
            throws Exception {

        UUID reportId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(
                reportService.getReport(
                        reportId,
                        securityContext
                )
        ).thenReturn(
                new GeneratedReportResponse(
                        reportId,
                        organizationId,
                        null,
                        "INVESTIGATION_CASES",
                        Map.of(),
                        Map.of(
                                "recordCount",
                                3
                        ),
                        userId,
                        LocalDateTime.now()
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/reports/{reportId}",
                                reportId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.reportId")
                                .value(
                                        reportId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.reportCode")
                                .value(
                                        "INVESTIGATION_CASES"
                                )
                );
    }

    @Test
    void reportNoDataShouldReturnUniform422()
            throws Exception {

        when(
                reportService.generateReport(
                        any(ReportGenerationRequest.class),
                        eq(securityContext)
                )
        ).thenThrow(
                new ReportException(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "REPORT_NO_DATA",
                        "No data is available for the requested report"
                )
        );

        mockMvc.perform(
                        post(
                                "/api/v1/reports"
                        )
                                .header(
                                        "X-Correlation-ID",
                                        "uc041-no-data"
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "reportCode": "INVESTIGATION_CASES",
                                          "criteria": {}
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isUnprocessableEntity()
                )
                .andExpect(
                        jsonPath("$.errorCode")
                                .value(
                                        "REPORT_NO_DATA"
                                )
                )
                .andExpect(
                        jsonPath("$.correlationId")
                                .value(
                                        "uc041-no-data"
                                )
                )
                .andExpect(
                        jsonPath("$.path")
                                .value(
                                        "/api/v1/reports"
                                )
                );
    }
}
