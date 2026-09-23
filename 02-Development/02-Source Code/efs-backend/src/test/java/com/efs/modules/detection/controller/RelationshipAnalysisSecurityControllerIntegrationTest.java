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
class RelationshipAnalysisSecurityControllerIntegrationTest {

    private static final UUID SECURITY_USER_ID =
            UUID.fromString(
                    "d180d180-d180-d180-d180-d180d180d180"
            );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SecurityContextProvider
            securityContextProvider;

    @Test
    void shouldAllowCreateWithRelationshipAnalysisCreatePermission()
            throws Exception {

        authorize(
                Set.of(
                        "relationship.analysis.create"
                )
        );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/relationship-analyses"
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
                        jsonPath("$.relationshipAnalysisId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.analysisStatus")
                                .value("COMPLETED")
                )
                .andExpect(
                        jsonPath("$.relationshipType")
                                .value("STANDALONE_LINK")
                );
    }

    @Test
    void shouldRejectCreateWithoutRelationshipAnalysisCreatePermission()
            throws Exception {

        authorize(
                Set.of(
                        "relationship.analysis.view"
                )
        );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/relationship-analyses"
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
    void shouldAllowReadBoundaryWithRelationshipAnalysisViewPermission()
            throws Exception {

        authorize(
                Set.of(
                        "relationship.analysis.view"
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/relationship-analyses/{relationshipAnalysisId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldRejectReadWithoutRelationshipAnalysisViewPermission()
            throws Exception {

        authorize(
                Set.of(
                        "relationship.analysis.create"
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/relationship-analyses/{relationshipAnalysisId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void shouldAllowCollectionReadWithRelationshipAnalysisViewPermission()
            throws Exception {

        authorize(
                Set.of(
                        "relationship.analysis.view"
                )
        );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/relationship-analyses/status/{analysisStatus}",
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
    void shouldRejectAllReadEndpointsWithoutRelationshipAnalysisViewPermission()
            throws Exception {

        authorize(
                Set.of(
                        "relationship.analysis.create"
                )
        );

        UUID randomId =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/detection/relationship-analyses/{relationshipAnalysisId}",
                                randomId
                        )
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(
                        get(
                                "/api/v1/detection/relationship-analyses/customer/{customerId}",
                                randomId
                        )
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(
                        get(
                                "/api/v1/detection/relationship-analyses/transaction/{transactionId}",
                                randomId
                        )
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(
                        get(
                                "/api/v1/detection/relationship-analyses/correlation/{correlationId}",
                                randomId
                        )
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(
                        get(
                                "/api/v1/detection/relationship-analyses/type/{relationshipType}",
                                "CUSTOMER_TO_DEVICE"
                        )
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(
                        get(
                                "/api/v1/detection/relationship-analyses/source/{sourceEntityKey}",
                                "SOURCE"
                        )
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(
                        get(
                                "/api/v1/detection/relationship-analyses/target/{targetEntityKey}",
                                "TARGET"
                        )
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(
                        get(
                                "/api/v1/detection/relationship-analyses/status/{analysisStatus}",
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

        request.put(
                "relationshipType",
                "STANDALONE_LINK"
        );

        request.put(
                "sourceEntityType",
                "CUSTOMER"
        );

        request.put(
                "sourceEntityKey",
                "SRC-" + UUID.randomUUID()
        );

        request.put(
                "targetEntityType",
                "DEVICE"
        );

        request.put(
                "targetEntityKey",
                "TGT-" + UUID.randomUUID()
        );

        return request;
    }
}