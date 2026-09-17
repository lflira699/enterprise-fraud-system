package com.efs.modules.administration.controller;

import com.efs.modules.administration.dto.SystemHealthComponentResponse;
import com.efs.modules.administration.dto.SystemHealthResponse;
import com.efs.modules.administration.service.SystemHealthServiceInterface;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SystemHealthControllerTest {

    @Mock
    private SystemHealthServiceInterface
            systemHealthService;

    @Mock
    private SecurityContextProvider
            securityContextProvider;

    @Mock
    private SecurityContext
            securityContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        SystemHealthController controller =
                new SystemHealthController(
                        systemHealthService,
                        securityContextProvider
                );

        mockMvc =
                MockMvcBuilders
                        .standaloneSetup(
                                controller
                        )
                        .build();
    }

    @Test
    void shouldExposeSystemHealthThroughVersionedHealthApi()
            throws Exception {

        when(
                securityContextProvider
                        .getCurrentContext()
        ).thenReturn(
                securityContext
        );

        when(
                systemHealthService
                        .getSystemHealth(
                                securityContext
                        )
        ).thenReturn(
                new SystemHealthResponse(
                        "UP",
                        "COMPLETE",
                        List.of(
                                new SystemHealthComponentResponse(
                                        "database",
                                        "UP"
                                )
                        ),
                        LocalDateTime.of(
                                2026,
                                9,
                                17,
                                7,
                                0
                        )
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/health"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("UP")
                )
                .andExpect(
                        jsonPath("$.informationStatus")
                                .value("COMPLETE")
                )
                .andExpect(
                        jsonPath("$.components[0].name")
                                .value("database")
                )
                .andExpect(
                        jsonPath("$.components[0].status")
                                .value("UP")
                )
                .andExpect(
                        jsonPath("$.checkedAt")
                                .exists()
                );

        verify(
                systemHealthService
        ).getSystemHealth(
                securityContext
        );
    }
}