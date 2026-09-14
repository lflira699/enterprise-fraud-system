package com.efs.modules.dashboard.controller;

import com.efs.modules.dashboard.dto.DashboardDataStatus;
import com.efs.modules.dashboard.dto.DashboardResponse;
import com.efs.modules.dashboard.service.DashboardServiceInterface;
import com.efs.shared.exception.DashboardDataUnavailableException;
import com.efs.shared.exception.GlobalExceptionHandler;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private DashboardServiceInterface
            dashboardService;

    @Mock
    private SecurityContextProvider
            securityContextProvider;

    @Mock
    private SecurityContext
            securityContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        DashboardController controller =
                new DashboardController(
                        dashboardService,
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
    }

    @Test
    void shouldReturnDashboardThroughApprovedGetEndpoint()
            throws Exception {

        DashboardResponse response =
                new DashboardResponse(
                        LocalDateTime.of(
                                2026,
                                9,
                                14,
                                14,
                                30
                        ),
                        DashboardDataStatus.COMPLETE,
                        2L,
                        7L,
                        4L,
                        1L,
                        new BigDecimal("55.25"),
                        3L,
                        List.of()
                );

        when(
                securityContextProvider
                        .getCurrentContext()
        ).thenReturn(
                securityContext
        );

        when(
                dashboardService.getDashboard(
                        securityContext
                )
        ).thenReturn(
                response
        );

        mockMvc.perform(
                        get(
                                "/api/v1/dashboard"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.dataStatus")
                                .value("COMPLETE")
                )
                .andExpect(
                        jsonPath("$.criticalAlerts")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$.openAlerts")
                                .value(7)
                )
                .andExpect(
                        jsonPath("$.openCases")
                                .value(4)
                )
                .andExpect(
                        jsonPath("$.closedCases")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.averageRiskScore")
                                .value(55.25)
                )
                .andExpect(
                        jsonPath("$.activatedDetectionScenarios")
                                .value(3)
                )
                .andExpect(
                        jsonPath("$.unavailableComponents")
                                .isArray()
                );

        verify(
                dashboardService
        ).getDashboard(
                securityContext
        );
    }

    @Test
    void shouldReturnUniform503WhenDashboardDataUnavailable()
            throws Exception {

        when(
                securityContextProvider
                        .getCurrentContext()
        ).thenReturn(
                securityContext
        );

        when(
                dashboardService.getDashboard(
                        securityContext
                )
        ).thenThrow(
                new DashboardDataUnavailableException(
                        "Dashboard data is unavailable"
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/dashboard"
                        )
                                .header(
                                        "X-Correlation-ID",
                                        "dashboard-test-correlation"
                                )
                )
                .andExpect(
                        status().isServiceUnavailable()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(503)
                )
                .andExpect(
                        jsonPath("$.errorCode")
                                .value(
                                        "DASHBOARD_DATA_UNAVAILABLE"
                                )
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Dashboard data is unavailable"
                                )
                )
                .andExpect(
                        jsonPath("$.correlationId")
                                .value(
                                        "dashboard-test-correlation"
                                )
                )
                .andExpect(
                        jsonPath("$.path")
                                .value(
                                        "/api/v1/dashboard"
                                )
                );
    }
}
