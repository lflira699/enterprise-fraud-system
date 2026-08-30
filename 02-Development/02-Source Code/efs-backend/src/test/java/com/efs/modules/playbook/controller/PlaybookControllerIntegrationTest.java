package com.efs.modules.playbook.controller;

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

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PlaybookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlaybookTestDataCleaner playbookTestDataCleaner;

    @AfterEach
    void cleanUp() {
        playbookTestDataCleaner.clean();
    }

    @Test
    void shouldCreatePlaybook()
            throws Exception {

        String playbookCode =
                "PB-CTRL-" + UUID.randomUUID();

        Map<String, Object> request =
                buildRequest(
                        playbookCode,
                        "Controller Test Playbook",
                        "Playbook created by controller integration test",
                        "TEST"
                );

        mockMvc.perform(
                        post("/api/v1/playbooks")
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
                        jsonPath("$.playbookId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.playbookCode")
                                .value(playbookCode)
                )
                .andExpect(
                        jsonPath("$.playbookName")
                                .value(
                                        "Controller Test Playbook"
                                )
                )
                .andExpect(
                        jsonPath("$.description")
                                .value(
                                        "Playbook created by controller integration test"
                                )
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("TEST")
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
    void shouldGetPlaybookById()
            throws Exception {

        JsonNode created =
                createPlaybook(
                        "PB-CTRL-" + UUID.randomUUID(),
                        "Lookup By Id",
                        "TEST"
                );

        UUID playbookId =
                UUID.fromString(
                        created.get(
                                "playbookId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/playbooks/{playbookId}",
                                playbookId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.playbookId")
                                .value(playbookId.toString())
                )
                .andExpect(
                        jsonPath("$.playbookCode")
                                .value(
                                        created.get(
                                                "playbookCode"
                                        ).asText()
                                )
                )
                .andExpect(
                        jsonPath("$.playbookName")
                                .value("Lookup By Id")
                );
    }

    @Test
    void shouldGetPlaybookByCode()
            throws Exception {

        String playbookCode =
                "PB-CTRL-" + UUID.randomUUID();

        createPlaybook(
                playbookCode,
                "Lookup By Code",
                "TEST"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/playbooks/code/{playbookCode}",
                                playbookCode
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.playbookCode")
                                .value(playbookCode)
                )
                .andExpect(
                        jsonPath("$.playbookName")
                                .value("Lookup By Code")
                );
    }

    @Test
    void shouldGetAllPlaybooks()
            throws Exception {

        JsonNode first =
                createPlaybook(
                        "PB-CTRL-" + UUID.randomUUID(),
                        "Alpha Controller Playbook",
                        "TEST"
                );

        JsonNode second =
                createPlaybook(
                        "PB-CTRL-" + UUID.randomUUID(),
                        "Beta Controller Playbook",
                        "OTHER"
                );

        mockMvc.perform(
                        get("/api/v1/playbooks")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].playbookId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "playbookId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].playbookId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "playbookId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldFilterPlaybooksByStatus()
            throws Exception {

        String targetStatus =
                "FILTER_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        JsonNode matching =
                createPlaybook(
                        "PB-CTRL-" + UUID.randomUUID(),
                        "Matching Playbook",
                        targetStatus
                );

        createPlaybook(
                "PB-CTRL-" + UUID.randomUUID(),
                "Non Matching Playbook",
                "OTHER_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8)
        );

        mockMvc.perform(
                        get("/api/v1/playbooks")
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
                        jsonPath("$[*].playbookId")
                                .value(
                                        hasItem(
                                                matching.get(
                                                        "playbookId"
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
    void shouldUpdatePlaybook()
            throws Exception {

        JsonNode created =
                createPlaybook(
                        "PB-CTRL-" + UUID.randomUUID(),
                        "Original Name",
                        "DRAFT"
                );

        UUID playbookId =
                UUID.fromString(
                        created.get(
                                "playbookId"
                        ).asText()
                );

        String updatedCode =
                "PB-CTRL-UPD-" + UUID.randomUUID();

        Map<String, Object> updateRequest =
                buildRequest(
                        updatedCode,
                        "Updated Playbook",
                        "Updated through controller integration test",
                        "ACTIVE"
                );

        mockMvc.perform(
                        put(
                                "/api/v1/playbooks/{playbookId}",
                                playbookId
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
                        jsonPath("$.playbookId")
                                .value(playbookId.toString())
                )
                .andExpect(
                        jsonPath("$.playbookCode")
                                .value(updatedCode)
                )
                .andExpect(
                        jsonPath("$.playbookName")
                                .value("Updated Playbook")
                )
                .andExpect(
                        jsonPath("$.description")
                                .value(
                                        "Updated through controller integration test"
                                )
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ACTIVE")
                )
                .andExpect(
                        jsonPath("$.updatedAt")
                                .exists()
                );
    }

    private JsonNode createPlaybook(
            String playbookCode,
            String playbookName,
            String status)
            throws Exception {

        Map<String, Object> request =
                buildRequest(
                        playbookCode,
                        playbookName,
                        "Controller integration test playbook",
                        status
                );

        MvcResult result =
                mockMvc.perform(
                                post("/api/v1/playbooks")
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
            String playbookCode,
            String playbookName,
            String description,
            String status) {

        Map<String, Object> request =
                new LinkedHashMap<>();

        request.put(
                "playbookCode",
                playbookCode
        );

        request.put(
                "playbookName",
                playbookName
        );

        request.put(
                "description",
                description
        );

        request.put(
                "status",
                status
        );

        return request;
    }
}