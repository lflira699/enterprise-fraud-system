package com.efs.modules.playbook.controller;

import com.efs.modules.playbook.dto.PlaybookRequest;
import com.efs.modules.playbook.dto.PlaybookResponse;
import com.efs.modules.playbook.dto.PlaybookVersionRequest;
import com.efs.modules.playbook.dto.PlaybookVersionResponse;
import com.efs.modules.playbook.service.PlaybookServiceInterface;
import com.efs.modules.playbook.service.PlaybookVersionServiceInterface;
import com.efs.modules.playbook.support.PlaybookTestDataCleaner;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PlaybookStepControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlaybookServiceInterface playbookService;

    @Autowired
    private PlaybookVersionServiceInterface playbookVersionService;

    @Autowired
    private PlaybookTestDataCleaner playbookTestDataCleaner;

    @AfterEach
    void cleanUp() {
        playbookTestDataCleaner.clean();
    }

    @Test
    void shouldCreatePlaybookStep()
            throws Exception {

        PlaybookVersionResponse version =
                createPlaybookVersion(
                        "PB-STEP-CTRL-" + UUID.randomUUID()
                );

        Map<String, Object> request =
                buildRequest(
                        version.getPlaybookVersionId(),
                        1,
                        "Verify customer identity",
                        "Verify the available customer identity evidence",
                        "Customer identity verification completed",
                        10
                );

        mockMvc.perform(
                        post("/api/v1/playbook-steps")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.playbookStepId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.playbookVersionId")
                                .value(
                                        version.getPlaybookVersionId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.stepOrder")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.stepName")
                                .value(
                                        "Verify customer identity"
                                )
                )
                .andExpect(
                        jsonPath("$.description")
                                .value(
                                        "Verify the available customer identity evidence"
                                )
                )
                .andExpect(
                        jsonPath("$.expectedResult")
                                .value(
                                        "Customer identity verification completed"
                                )
                )
                .andExpect(
                        jsonPath("$.expectedDurationMinutes")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$.createdAt")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.updatedAt")
                                .exists()
                );
    }

    @Test
    void shouldGetPlaybookStepById()
            throws Exception {

        PlaybookVersionResponse version =
                createPlaybookVersion(
                        "PB-STEP-CTRL-" + UUID.randomUUID()
                );

        JsonNode created =
                createStep(
                        version.getPlaybookVersionId(),
                        1,
                        "Lookup Step"
                );

        UUID playbookStepId =
                UUID.fromString(
                        created.get(
                                "playbookStepId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/playbook-steps/{playbookStepId}",
                                playbookStepId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.playbookStepId")
                                .value(
                                        playbookStepId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.playbookVersionId")
                                .value(
                                        version.getPlaybookVersionId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.stepOrder")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.stepName")
                                .value("Lookup Step")
                );
    }

    @Test
    void shouldGetStepsByPlaybookVersionOrderedAscending()
            throws Exception {

        PlaybookVersionResponse version =
                createPlaybookVersion(
                        "PB-STEP-CTRL-" + UUID.randomUUID()
                );

        JsonNode second =
                createStep(
                        version.getPlaybookVersionId(),
                        2,
                        "Second Step"
                );

        JsonNode first =
                createStep(
                        version.getPlaybookVersionId(),
                        1,
                        "First Step"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/playbook-steps/version/{playbookVersionId}",
                                version.getPlaybookVersionId()
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[0].playbookStepId")
                                .value(
                                        first.get(
                                                "playbookStepId"
                                        ).asText()
                                )
                )
                .andExpect(
                        jsonPath("$[0].stepOrder")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].stepName")
                                .value("First Step")
                )
                .andExpect(
                        jsonPath("$[1].playbookStepId")
                                .value(
                                        second.get(
                                                "playbookStepId"
                                        ).asText()
                                )
                )
                .andExpect(
                        jsonPath("$[1].stepOrder")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[1].stepName")
                                .value("Second Step")
                );
    }

    @Test
    void shouldUpdatePlaybookStep()
            throws Exception {

        PlaybookVersionResponse version =
                createPlaybookVersion(
                        "PB-STEP-CTRL-" + UUID.randomUUID()
                );

        JsonNode created =
                createStep(
                        version.getPlaybookVersionId(),
                        1,
                        "Original Step"
                );

        UUID playbookStepId =
                UUID.fromString(
                        created.get(
                                "playbookStepId"
                        ).asText()
                );

        Map<String, Object> updateRequest =
                buildRequest(
                        version.getPlaybookVersionId(),
                        2,
                        "Updated Step",
                        "Updated step description",
                        "Updated expected result",
                        20
                );

        mockMvc.perform(
                        put(
                                "/api/v1/playbook-steps/{playbookStepId}",
                                playbookStepId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                updateRequest
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.playbookStepId")
                                .value(
                                        playbookStepId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.playbookVersionId")
                                .value(
                                        version.getPlaybookVersionId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.stepOrder")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$.stepName")
                                .value("Updated Step")
                )
                .andExpect(
                        jsonPath("$.description")
                                .value(
                                        "Updated step description"
                                )
                )
                .andExpect(
                        jsonPath("$.expectedResult")
                                .value(
                                        "Updated expected result"
                                )
                )
                .andExpect(
                        jsonPath("$.expectedDurationMinutes")
                                .value(20)
                )
                .andExpect(
                        jsonPath("$.updatedAt")
                                .exists()
                );
    }

    private JsonNode createStep(
            UUID playbookVersionId,
            Integer stepOrder,
            String stepName)
            throws Exception {

        Map<String, Object> request =
                buildRequest(
                        playbookVersionId,
                        stepOrder,
                        stepName,
                        "Controller integration test step",
                        "Step completed",
                        10
                );

        MvcResult result =
                mockMvc.perform(
                                post("/api/v1/playbook-steps")
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
                        .andExpect(status().isCreated())
                        .andReturn();

        return objectMapper.readTree(
                result.getResponse()
                        .getContentAsString()
        );
    }

    private Map<String, Object> buildRequest(
            UUID playbookVersionId,
            Integer stepOrder,
            String stepName,
            String description,
            String expectedResult,
            Integer expectedDurationMinutes) {

        Map<String, Object> request =
                new LinkedHashMap<>();

        request.put(
                "playbookVersionId",
                playbookVersionId
        );

        request.put(
                "stepOrder",
                stepOrder
        );

        request.put(
                "stepName",
                stepName
        );

        request.put(
                "description",
                description
        );

        request.put(
                "expectedResult",
                expectedResult
        );

        request.put(
                "expectedDurationMinutes",
                expectedDurationMinutes
        );

        return request;
    }

    private PlaybookVersionResponse createPlaybookVersion(
            String code) {

        PlaybookRequest playbookRequest =
                new PlaybookRequest();

        playbookRequest.setPlaybookCode(
                code
        );

        playbookRequest.setPlaybookName(
                code
        );

        playbookRequest.setStatus(
                "TEST"
        );

        PlaybookResponse playbook =
                playbookService.create(
                        playbookRequest
                );

        PlaybookVersionRequest versionRequest =
                new PlaybookVersionRequest();

        versionRequest.setPlaybookId(
                playbook.getPlaybookId()
        );

        versionRequest.setVersionNumber(
                1
        );

        versionRequest.setStatus(
                "TEST"
        );

        return playbookVersionService.create(
                versionRequest
        );
    }
}