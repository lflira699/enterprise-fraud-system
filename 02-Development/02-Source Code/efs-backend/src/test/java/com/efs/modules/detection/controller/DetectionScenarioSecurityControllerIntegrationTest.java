package com.efs.modules.detection.controller;

import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DetectionScenarioSecurityControllerIntegrationTest {

    private static final UUID SECURITY_USER_ID =
            UUID.fromString(
                    "d178d178-d178-d178-d178-d178d178d178"
            );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SecurityContextProvider
            securityContextProvider;

    @Test
    void shouldAllowCreateWithScenarioCreatePermission()
            throws Exception {

        authorize(
                Set.of(
                        "scenario.create"
                )
        );

        Map<String, Object> request =
                validScenarioRequest(
                        "DS-SEC-CREATE-" + uniqueSuffix()
                );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/scenarios"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request
                                                )
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath("$.scenarioId")
                                .exists()
                );
    }

    @Test
    void shouldRejectCreateWithoutScenarioCreatePermission()
            throws Exception {

        authorize(
                Set.of(
                        "scenario.view"
                )
        );

        Map<String, Object> request =
                validScenarioRequest(
                        "DS-SEC-DENY-CREATE-" + uniqueSuffix()
                );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/scenarios"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request
                                                )
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void shouldAllowReadBoundaryWithScenarioViewPermission()
            throws Exception {

        authorize(
                Set.of(
                        "scenario.view"
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenarios/{scenarioId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldRejectReadWithoutScenarioViewPermission()
            throws Exception {

        authorize(
                Set.of(
                        "scenario.create"
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenarios/{scenarioId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void shouldAllowSearchWithScenarioViewPermission()
            throws Exception {

        authorize(
                Set.of(
                        "scenario.view"
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenarios"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.content")
                                .isArray()
                );
    }

    @Test
    void shouldRejectSearchWithoutScenarioViewPermission()
            throws Exception {

        authorize(
                Set.of(
                        "scenario.create"
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenarios"
                        )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    private void authorize(
            Set<String> permissions) {

        when(
                securityContextProvider
                        .getCurrentContext()
        ).thenReturn(
                new SecurityContext(
                        SECURITY_USER_ID,
                        null,
                        null,
                        Set.of(),
                        permissions,
                        Set.of()
                )
        );
    }

    private Map<String, Object> validScenarioRequest(
            String scenarioCode) {

        Map<String, Object> request =
                new LinkedHashMap<>();

        request.put(
                "scenarioCode",
                scenarioCode
        );

        request.put(
                "scenarioName",
                "Detection Scenario Security Test"
        );

        request.put(
                "objective",
                "Validate Detection Scenario authorization contract"
        );

        request.put(
                "category",
                "ATO"
        );

        request.put(
                "criticality",
                "HIGH"
        );

        request.put(
                "status",
                "ACTIVE"
        );

        request.put(
                "owner",
                "DetectionTeam"
        );

        request.put(
                "version",
                1
        );

        return request;
    }

    private String uniqueSuffix() {

        return UUID.randomUUID()
                .toString()
                .substring(
                        0,
                        8
                );
    }
}