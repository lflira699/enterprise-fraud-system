package com.efs.modules.playbook.controller;

import com.efs.modules.playbook.dto.PlaybookExecutionRequest;
import com.efs.modules.playbook.dto.PlaybookExecutionResponse;
import com.efs.modules.playbook.dto.PlaybookRequest;
import com.efs.modules.playbook.dto.PlaybookResponse;
import com.efs.modules.playbook.dto.PlaybookStepRequest;
import com.efs.modules.playbook.dto.PlaybookStepResponse;
import com.efs.modules.playbook.dto.PlaybookVersionRequest;
import com.efs.modules.playbook.dto.PlaybookVersionResponse;
import com.efs.modules.playbook.service.PlaybookExecutionServiceInterface;
import com.efs.modules.playbook.service.PlaybookServiceInterface;
import com.efs.modules.playbook.service.PlaybookStepServiceInterface;
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
class PlaybookExecutionStepControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlaybookServiceInterface playbookService;

    @Autowired
    private PlaybookVersionServiceInterface playbookVersionService;

    @Autowired
    private PlaybookStepServiceInterface playbookStepService;

    @Autowired
    private PlaybookExecutionServiceInterface playbookExecutionService;

    @Autowired
    private PlaybookTestDataCleaner playbookTestDataCleaner;

    @AfterEach
    void cleanUp() {
        playbookTestDataCleaner.clean();
    }

    @Test
    void shouldCreatePlaybookExecutionStep()
            throws Exception {

        TestContext context =
                createContext(
                        "PB-EXEC-STEP-CTRL-" + UUID.randomUUID()
                );

        Map<String, Object> request =
                buildRequest(
                        context.execution().getPlaybookExecutionId(),
                        context.step().getPlaybookStepId(),
                        "TEST",
                        "Verification completed"
                );

        request.put(
                "startedAt",
                LocalDateTime.of(
                        2026,
                        8,
                        30,
                        10,
                        0
                )
        );

        request.put(
                "completedAt",
                LocalDateTime.of(
                        2026,
                        8,
                        30,
                        10,
                        10
                )
        );

        mockMvc.perform(
                        post(
                                "/api/v1/playbook-execution-steps"
                        )
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
                        jsonPath("$.playbookExecutionStepId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.playbookExecutionId")
                                .value(
                                        context.execution()
                                                .getPlaybookExecutionId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.playbookStepId")
                                .value(
                                        context.step()
                                                .getPlaybookStepId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("TEST")
                )
                .andExpect(
                        jsonPath("$.result")
                                .value(
                                        "Verification completed"
                                )
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
                                        "2026-08-30T10:10:00"
                                )
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
    void shouldGetPlaybookExecutionStepById()
            throws Exception {

        TestContext context =
                createContext(
                        "PB-EXEC-STEP-CTRL-" + UUID.randomUUID()
                );

        JsonNode created =
                createExecutionStep(
                        context,
                        "LOOKUP_TEST",
                        "Lookup result"
                );

        UUID playbookExecutionStepId =
                UUID.fromString(
                        created.get(
                                "playbookExecutionStepId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/playbook-execution-steps/{playbookExecutionStepId}",
                                playbookExecutionStepId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.playbookExecutionStepId")
                                .value(
                                        playbookExecutionStepId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.playbookExecutionId")
                                .value(
                                        context.execution()
                                                .getPlaybookExecutionId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.playbookStepId")
                                .value(
                                        context.step()
                                                .getPlaybookStepId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("LOOKUP_TEST")
                )
                .andExpect(
                        jsonPath("$.result")
                                .value("Lookup result")
                );
    }

    @Test
    void shouldGetExecutionStepsByPlaybookExecution()
            throws Exception {

        TestContext firstContext =
                createContext(
                        "PB-EXEC-STEP-CTRL-" + UUID.randomUUID()
                );

        JsonNode created =
                createExecutionStep(
                        firstContext,
                        "LIST_TEST",
                        "List result"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/playbook-execution-steps/execution/{playbookExecutionId}",
                                firstContext.execution()
                                        .getPlaybookExecutionId()
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].playbookExecutionStepId")
                                .value(
                                        hasItem(
                                                created.get(
                                                        "playbookExecutionStepId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].playbookExecutionId")
                                .value(
                                        hasItem(
                                                firstContext.execution()
                                                        .getPlaybookExecutionId()
                                                        .toString()
                                        )
                                )
                );
    }

    @Test
    void shouldUpdatePlaybookExecutionStep()
            throws Exception {

        TestContext context =
                createContext(
                        "PB-EXEC-STEP-CTRL-" + UUID.randomUUID()
                );

        JsonNode created =
                createExecutionStep(
                        context,
                        "STARTED",
                        "Initial result"
                );

        UUID playbookExecutionStepId =
                UUID.fromString(
                        created.get(
                                "playbookExecutionStepId"
                        ).asText()
                );

        Map<String, Object> updateRequest =
                buildRequest(
                        context.execution().getPlaybookExecutionId(),
                        context.step().getPlaybookStepId(),
                        "COMPLETED",
                        "Completed result"
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
                        10,
                        15
                )
        );

        mockMvc.perform(
                        put(
                                "/api/v1/playbook-execution-steps/{playbookExecutionStepId}",
                                playbookExecutionStepId
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
                        jsonPath("$.playbookExecutionStepId")
                                .value(
                                        playbookExecutionStepId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.playbookExecutionId")
                                .value(
                                        context.execution()
                                                .getPlaybookExecutionId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.playbookStepId")
                                .value(
                                        context.step()
                                                .getPlaybookStepId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("COMPLETED")
                )
                .andExpect(
                        jsonPath("$.result")
                                .value("Completed result")
                )
                .andExpect(
                        jsonPath("$.completedAt")
                                .value(
                                        "2026-08-30T10:15:00"
                                )
                )
                .andExpect(
                        jsonPath("$.updatedAt")
                                .exists()
                );
    }

    private JsonNode createExecutionStep(
            TestContext context,
            String status,
            String result)
            throws Exception {

        Map<String, Object> request =
                buildRequest(
                        context.execution().getPlaybookExecutionId(),
                        context.step().getPlaybookStepId(),
                        status,
                        result
                );

        MvcResult mvcResult =
                mockMvc.perform(
                                post(
                                        "/api/v1/playbook-execution-steps"
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
                mvcResult.getResponse()
                        .getContentAsString()
        );
    }

    private Map<String, Object> buildRequest(
            UUID playbookExecutionId,
            UUID playbookStepId,
            String status,
            String result) {

        Map<String, Object> request =
                new LinkedHashMap<>();

        request.put(
                "playbookExecutionId",
                playbookExecutionId
        );

        request.put(
                "playbookStepId",
                playbookStepId
        );

        request.put(
                "status",
                status
        );

        request.put(
                "result",
                result
        );

        return request;
    }

    private TestContext createContext(
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

        PlaybookVersionResponse version =
                playbookVersionService.create(
                        versionRequest
                );

        PlaybookStepRequest stepRequest =
                new PlaybookStepRequest();

        stepRequest.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );

        stepRequest.setStepOrder(
                1
        );

        stepRequest.setStepName(
                "Investigation Step"
        );

        stepRequest.setExpectedDurationMinutes(
                10
        );

        PlaybookStepResponse step =
                playbookStepService.create(
                        stepRequest
                );

        PlaybookExecutionRequest executionRequest =
                new PlaybookExecutionRequest();

        executionRequest.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );

        executionRequest.setStatus(
                "TEST"
        );

        PlaybookExecutionResponse execution =
                playbookExecutionService.create(
                        executionRequest
                );

        return new TestContext(
                step,
                execution
        );
    }

    private record TestContext(
            PlaybookStepResponse step,
            PlaybookExecutionResponse execution
    ) {
    }
}