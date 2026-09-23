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
class NetworkAnalysisSecurityControllerIntegrationTest {

    private static final UUID SECURITY_USER_ID =
            UUID.fromString(
                    "d181d181-d181-d181-d181-d181d181d181"
            );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SecurityContextProvider
            securityContextProvider;

    @Test
    void shouldAllowCreateWithNetworkAnalysisCreatePermission()
            throws Exception {

        authorize(
                Set.of(
                        "network.analysis.create"
                )
        );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/network-analyses"
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
                        jsonPath("$.networkAnalysisId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.analysisStatus")
                                .value("COMPLETED")
                )
                .andExpect(
                        jsonPath("$.networkType")
                                .value("STANDALONE_NETWORK")
                );
    }

    @Test
    void shouldRejectCreateWithoutNetworkAnalysisCreatePermission()
            throws Exception {

        authorize(
                Set.of(
                        "network.analysis.view"
                )
        );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/network-analyses"
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
    void shouldAllowReadBoundaryWithNetworkAnalysisViewPermission()
            throws Exception {

        authorize(
                Set.of(
                        "network.analysis.view"
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/network-analyses/{networkAnalysisId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldRejectReadWithoutNetworkAnalysisViewPermission()
            throws Exception {

        authorize(
                Set.of(
                        "network.analysis.create"
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/network-analyses/{networkAnalysisId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void shouldAllowCollectionReadWithNetworkAnalysisViewPermission()
            throws Exception {

        authorize(
                Set.of(
                        "network.analysis.view"
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/network-analyses/status/{analysisStatus}",
                                "COMPLETED"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$")
                                .isArray()
                );
    }

    @Test
    void shouldRejectAllReadEndpointsWithoutNetworkAnalysisViewPermission()
            throws Exception {

        authorize(
                Set.of(
                        "network.analysis.create"
                )
        );

        UUID randomId =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/detection/network-analyses/{networkAnalysisId}",
                                randomId
                        )
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(
                        get(
                                "/api/v1/detection/network-analyses/customer/{customerId}",
                                randomId
                        )
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(
                        get(
                                "/api/v1/detection/network-analyses/transaction/{transactionId}",
                                randomId
                        )
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(
                        get(
                                "/api/v1/detection/network-analyses/correlation/{correlationId}",
                                randomId
                        )
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(
                        get(
                                "/api/v1/detection/network-analyses/type/{networkType}",
                                "CUSTOMER_NETWORK"
                        )
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(
                        get(
                                "/api/v1/detection/network-analyses/status/{analysisStatus}",
                                "COMPLETED"
                        )
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(
                        get(
                                "/api/v1/detection/network-analyses/key/{networkKey}",
                                "NETWORK-KEY"
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

        request.put(
                "networkType",
                "STANDALONE_NETWORK"
        );

        return request;
    }
}