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

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PlaybookExecutionControllerIntegrationTest {

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
    void shouldCreatePlaybookExecution()
            throws Exception {

        PlaybookVersionResponse version =
                createPlaybookVersion(
                        "PB-EXEC-CTRL-" + UUID.randomUUID()
                );

        Map<String, Object> request =
                buildRequest(
                        version.getPlaybookVersionId(),
                        "TEST"
                );

        mockMvc.perform(
                        post("/api/v1/playbook-executions")
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
                        jsonPath("$.playbookExecutionId")
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
                        jsonPath("$.status")
                                .value("TEST")
                )
                .andExpect(
                        jsonPath("$.startedAt")
                                .exists()
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
    void shouldGetPlaybookExecutionById()
            throws Exception {

        PlaybookVersionResponse version =
                createPlaybookVersion(
                        "PB-EXEC-CTRL-" + UUID.randomUUID()
                );

        JsonNode created =
                createExecution(
                        version.getPlaybookVersionId(),
                        "LOOKUP_TEST",
                        null
                );

        UUID playbookExecutionId =
                UUID.fromString(
                        created.get(
                                "playbookExecutionId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/playbook-executions/{playbookExecutionId}",
                                playbookExecutionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.playbookExecutionId")
                                .value(
                                        playbookExecutionId.toString()
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
                        jsonPath("$.status")
                                .value("LOOKUP_TEST")
                );
    }

    @Test
    void shouldGetExecutionsByPlaybookVersion()
            throws Exception {

        PlaybookVersionResponse version =
                createPlaybookVersion(
                        "PB-EXEC-CTRL-" + UUID.randomUUID()
                );

        LocalDateTime firstStartedAt =
                LocalDateTime.of(
                        2026,
                        8,
                        30,
                        10,
                        0
                );

        LocalDateTime secondStartedAt =
                LocalDateTime.of(
                        2026,
                        8,
                        30,
                        11,
                        0
                );

        JsonNode first =
                createExecution(
                        version.getPlaybookVersionId(),
                        "VERSION_TEST",
                        firstStartedAt
                );

        JsonNode second =
                createExecution(
                        version.getPlaybookVersionId(),
                        "VERSION_TEST",
                        secondStartedAt
                );

        mockMvc.perform(
                        get(
                                "/api/v1/playbook-executions/version/{playbookVersionId}",
                                version.getPlaybookVersionId()
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].playbookExecutionId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "playbookExecutionId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].playbookExecutionId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "playbookExecutionId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[0].startedAt")
                                .value(
                                        "2026-08-30T11:00:00"
                                )
                )
                .andExpect(
                        jsonPath("$[1].startedAt")
                                .value(
                                        "2026-08-30T10:00:00"
                                )
                );
    }

    @Test
    void shouldGetExecutionsByAlert()
            throws Exception {

        UUID unknownAlertId =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/playbook-executions/alert/{alertId}",
                                unknownAlertId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$")
                                .isEmpty()
                );
    }

    @Test
    void shouldGetExecutionsByStatus()
            throws Exception {

        PlaybookVersionResponse version =
                createPlaybookVersion(
                        "PB-EXEC-CTRL-" + UUID.randomUUID()
                );

        String targetStatus =
                "STATUS_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        JsonNode matching =
                createExecution(
                        version.getPlaybookVersionId(),
                        targetStatus,
                        null
                );

        createExecution(
                version.getPlaybookVersionId(),
                "OTHER_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8),
                null
        );

        mockMvc.perform(
                        get("/api/v1/playbook-executions")
                                .param(
                                        "status",
                                        targetStatus
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].playbookExecutionId")
                                .value(
                                        hasItem(
                                                matching.get(
                                                        "playbookExecutionId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].status")
                                .value(
                                        hasItem(
                                                targetStatus
                                        )
                                )
                );
    }

    @Test
    void shouldUpdatePlaybookExecution()
            throws Exception {

        PlaybookVersionResponse version =
                createPlaybookVersion(
                        "PB-EXEC-CTRL-" + UUID.randomUUID()
                );

        JsonNode created =
                createExecution(
                        version.getPlaybookVersionId(),
                        "STARTED",
                        LocalDateTime.of(
                                2026,
                                8,
                                30,
                                10,
                                0
                        )
                );

        UUID playbookExecutionId =
                UUID.fromString(
                        created.get(
                                "playbookExecutionId"
                        ).asText()
                );

        Map<String, Object> updateRequest =
                buildRequest(
                        version.getPlaybookVersionId(),
                        "COMPLETED"
                );

        updateRequest.put(
                "startedAt",
                LocalDateTime.of(
                        2026,
                        8,
                        30,
                        10,
                        0
                )
        );

        updateRequest.put(
                "completedAt",
                LocalDateTime.of(
                        2026,
                        8,
                        30,
                        12,
                        0
                )
        );

        mockMvc.perform(
                        put(
                                "/api/v1/playbook-executions/{playbookExecutionId}",
                                playbookExecutionId
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
                        jsonPath("$.playbookExecutionId")
                                .value(
                                        playbookExecutionId.toString()
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
                        jsonPath("$.status")
                                .value("COMPLETED")
                )
                .andExpect(
                        jsonPath("$.startedAt")
                                .value(
                                        "2026-08-30T10:00:00"
                                )
                )
                .andExpect(
                        jsonPath("$.completedAt")
                                .value(
                                        "2026-08-30T12:00:00"
                                )
                )
                .andExpect(
                        jsonPath("$.updatedAt")
                                .exists()
                );
    }

    private JsonNode createExecution(
            UUID playbookVersionId,
            String status,
            LocalDateTime startedAt)
            throws Exception {

        Map<String, Object> request =
                buildRequest(
                        playbookVersionId,
                        status
                );

        if (startedAt != null) {
            request.put(
                    "startedAt",
                    startedAt
            );
        }

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/playbook-executions"
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
                        .andExpect(status().isCreated())
                        .andReturn();

        return objectMapper.readTree(
                result.getResponse()
                        .getContentAsString()
        );
    }

    private Map<String, Object> buildRequest(
            UUID playbookVersionId,
            String status) {

        Map<String, Object> request =
                new LinkedHashMap<>();

        request.put(
                "playbookVersionId",
                playbookVersionId
        );

        request.put(
                "status",
                status
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