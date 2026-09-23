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
class DeviceAnalysisSecurityControllerIntegrationTest {

    private static final UUID SECURITY_USER_ID =
            UUID.fromString(
                    "d179d179-d179-d179-d179-d179d179d179"
            );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SecurityContextProvider
            securityContextProvider;

    @Test
    void shouldAllowCreateWithDeviceAnalysisCreatePermission()
            throws Exception {

        authorize(
                Set.of(
                        "device.analysis.create"
                )
        );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/device-analyses"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                validRequest()
                                        )
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath("$.deviceAnalysisId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.analysisStatus")
                                .value("COMPLETED")
                );
    }

    @Test
    void shouldRejectCreateWithoutDeviceAnalysisCreatePermission()
            throws Exception {

        authorize(
                Set.of(
                        "device.analysis.view"
                )
        );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/device-analyses"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                validRequest()
                                        )
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void shouldAllowReadBoundaryWithDeviceAnalysisViewPermission()
            throws Exception {

        authorize(
                Set.of(
                        "device.analysis.view"
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/device-analyses/{deviceAnalysisId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldRejectReadWithoutDeviceAnalysisViewPermission()
            throws Exception {

        authorize(
                Set.of(
                        "device.analysis.create"
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/device-analyses/{deviceAnalysisId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void shouldAllowStatusQueryWithDeviceAnalysisViewPermission()
            throws Exception {

        authorize(
                Set.of(
                        "device.analysis.view"
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/device-analyses/status/{analysisStatus}",
                                "COMPLETED"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$").isArray()
                );
    }

    @Test
    void shouldRejectAllReadRoutesWithoutDeviceAnalysisViewPermission()
            throws Exception {

        authorize(
                Set.of(
                        "device.analysis.create"
                )
        );

        UUID identifier =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/detection/device-analyses/{deviceAnalysisId}",
                                identifier
                        )
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(
                        get(
                                "/api/v1/detection/device-analyses/customer/{customerId}",
                                identifier
                        )
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(
                        get(
                                "/api/v1/detection/device-analyses/transaction/{transactionId}",
                                identifier
                        )
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(
                        get(
                                "/api/v1/detection/device-analyses/correlation/{correlationId}",
                                identifier
                        )
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(
                        get(
                                "/api/v1/detection/device-analyses/device/{deviceId}",
                                "DEVICE-SECURITY"
                        )
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(
                        get(
                                "/api/v1/detection/device-analyses/fingerprint/{deviceFingerprint}",
                                "FP-SECURITY"
                        )
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(
                        get(
                                "/api/v1/detection/device-analyses/ip/{ipAddress}",
                                "10.179.0.1"
                        )
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(
                        get(
                                "/api/v1/detection/device-analyses/status/{analysisStatus}",
                                "COMPLETED"
                        )
                )
                .andExpect(status().isForbidden());
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

    private Map<String, Object> validRequest() {

        Map<String, Object> request =
                new LinkedHashMap<>();

        request.put(
                "analysisStatus",
                "COMPLETED"
        );

        return request;
    }
}