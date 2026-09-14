package com.efs.modules.dashboard.controller;

import com.efs.modules.dashboard.dto.DashboardComponent;
import com.efs.modules.dashboard.dto.DashboardDataStatus;
import com.efs.modules.dashboard.dto.DashboardEffectiveFilters;
import com.efs.modules.dashboard.dto.DashboardFilterCriteria;
import com.efs.modules.dashboard.dto.DashboardFilterOptionsResponse;
import com.efs.modules.dashboard.dto.DashboardFilterSource;
import com.efs.modules.dashboard.dto.DashboardResponse;
import com.efs.modules.dashboard.service.DashboardServiceInterface;
import com.efs.shared.exception.GlobalExceptionHandler;
import com.efs.shared.exception.InvalidDashboardFilterException;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DashboardFilterControllerTest {

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

        when(
                securityContextProvider
                        .getCurrentContext()
        ).thenReturn(
                securityContext
        );
    }

    @Test
    void shouldForwardExplicitTenantAndComponents()
            throws Exception {

        UUID tenantId = UUID.randomUUID();

        DashboardResponse response =
                new DashboardResponse(
                        LocalDateTime.now(),
                        DashboardDataStatus.COMPLETE,
                        2L,
                        5L,
                        null,
                        null,
                        null,
                        null,
                        List.of(),
                        new DashboardEffectiveFilters(
                                tenantId,
                                List.of(
                                        DashboardComponent.ALERT
                                ),
                                DashboardFilterSource.EXPLICIT
                        )
                );

        when(
                dashboardService.getDashboard(
                        eq(securityContext),
                        any(DashboardFilterCriteria.class)
                )
        ).thenReturn(
                response
        );

        mockMvc.perform(
                        get(
                                "/api/v1/dashboard"
                        )
                                .param(
                                        "tenantId",
                                        tenantId.toString()
                                )
                                .param(
                                        "components",
                                        "ALERT"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.effectiveFilters.tenantId"
                        ).value(
                                tenantId.toString()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.effectiveFilters.components[0]"
                        ).value(
                                "ALERT"
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.effectiveFilters.filterSource"
                        ).value(
                                "EXPLICIT"
                        )
                );

        ArgumentCaptor<DashboardFilterCriteria>
                captor =
                ArgumentCaptor.forClass(
                        DashboardFilterCriteria.class
                );

        verify(
                dashboardService
        ).getDashboard(
                eq(securityContext),
                captor.capture()
        );

        assertEquals(
                tenantId,
                captor.getValue()
                        .tenantId()
        );

        assertEquals(
                "ALERT",
                captor.getValue()
                        .components()
        );
    }

    @Test
    void shouldExposeDashboardFilterOptions()
            throws Exception {

        UUID tenantId = UUID.randomUUID();

        when(
                dashboardService.getFilterOptions(
                        securityContext,
                        tenantId
                )
        ).thenReturn(
                new DashboardFilterOptionsResponse(
                        true,
                        tenantId,
                        List.of(
                                DashboardComponent.ALERT,
                                DashboardComponent.CASE
                        ),
                        List.of(
                                DashboardComponent.ALERT
                        )
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/dashboard/filters"
                        )
                                .param(
                                        "tenantId",
                                        tenantId.toString()
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.tenantSelectionAllowed"
                        ).value(true)
                )
                .andExpect(
                        jsonPath(
                                "$.effectiveTenantId"
                        ).value(
                                tenantId.toString()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.availableComponents[0]"
                        ).value(
                                "ALERT"
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.defaultComponents[0]"
                        ).value(
                                "ALERT"
                        )
                );
    }

    @Test
    void invalidDashboardFilterShouldReturnUniform400()
            throws Exception {

        when(
                dashboardService.getDashboard(
                        eq(securityContext),
                        any(DashboardFilterCriteria.class)
                )
        ).thenThrow(
                new InvalidDashboardFilterException(
                        "Unknown Dashboard component: UNKNOWN"
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/dashboard"
                        )
                                .param(
                                        "components",
                                        "UNKNOWN"
                                )
                                .header(
                                        "X-Correlation-ID",
                                        "uc040-filter-correlation"
                                )
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.errorCode")
                                .value(
                                        "INVALID_DASHBOARD_FILTER"
                                )
                )
                .andExpect(
                        jsonPath("$.correlationId")
                                .value(
                                        "uc040-filter-correlation"
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
