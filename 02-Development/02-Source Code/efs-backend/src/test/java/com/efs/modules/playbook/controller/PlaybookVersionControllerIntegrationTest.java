package com.efs.modules.playbook.controller;

import com.efs.modules.playbook.dto.PlaybookRequest;
import com.efs.modules.playbook.dto.PlaybookResponse;
import com.efs.modules.playbook.service.PlaybookServiceInterface;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PlaybookVersionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlaybookServiceInterface playbookService;

    @Autowired
    private PlaybookTestDataCleaner playbookTestDataCleaner;

    @AfterEach
    void cleanUp() {
        playbookTestDataCleaner.clean();
    }

    @Test
    void shouldCreatePlaybookVersion()
            throws Exception {

        PlaybookResponse playbook =
                createPlaybook(
                        "PB-VERSION-CTRL-" + UUID.randomUUID()
                );

        Map<String, Object> request =
                buildRequest(
                        playbook.getPlaybookId(),
                        1,
                        "TEST",
                        LocalDateTime.of(
                                2026,
                                8,
                                30,
                                10,
                                0
                        ),
                        LocalDateTime.of(
                                2026,
                                8,
                                31,
                                10,
                                0
                        )
                );

        mockMvc.perform(
                        post("/api/v1/playbook-versions")
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
                        jsonPath("$.playbookVersionId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.playbookId")
                                .value(
                                        playbook.getPlaybookId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.versionNumber")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("TEST")
                )
                .andExpect(
                        jsonPath("$.effectiveFrom")
                                .value(
                                        "2026-08-30T10:00:00"
                                )
                )
                .andExpect(
                        jsonPath("$.effectiveTo")
                                .value(
                                        "2026-08-31T10:00:00"
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
    void shouldGetPlaybookVersionById()
            throws Exception {

        PlaybookResponse playbook =
                createPlaybook(
                        "PB-VERSION-CTRL-" + UUID.randomUUID()
                );

        JsonNode created =
                createVersion(
                        playbook.getPlaybookId(),
                        1,
                        "LOOKUP_TEST"
                );

        UUID playbookVersionId =
                UUID.fromString(
                        created.get(
                                "playbookVersionId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/playbook-versions/{playbookVersionId}",
                                playbookVersionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.playbookVersionId")
                                .value(
                                        playbookVersionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.playbookId")
                                .value(
                                        playbook.getPlaybookId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.versionNumber")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("LOOKUP_TEST")
                );
    }

    @Test
    void shouldGetPlaybookVersionsOrderedDescending()
            throws Exception {

        PlaybookResponse playbook =
                createPlaybook(
                        "PB-VERSION-CTRL-" + UUID.randomUUID()
                );

        JsonNode first =
                createVersion(
                        playbook.getPlaybookId(),
                        1,
                        "TEST"
                );

        JsonNode second =
                createVersion(
                        playbook.getPlaybookId(),
                        2,
                        "TEST"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/playbook-versions/playbook/{playbookId}",
                                playbook.getPlaybookId()
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[0].playbookVersionId")
                                .value(
                                        second.get(
                                                "playbookVersionId"
                                        ).asText()
                                )
                )
                .andExpect(
                        jsonPath("$[0].versionNumber")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[1].playbookVersionId")
                                .value(
                                        first.get(
                                                "playbookVersionId"
                                        ).asText()
                                )
                )
                .andExpect(
                        jsonPath("$[1].versionNumber")
                                .value(1)
                );
    }

    @Test
    void shouldGetPlaybookVersionByNumber()
            throws Exception {

        PlaybookResponse playbook =
                createPlaybook(
                        "PB-VERSION-CTRL-" + UUID.randomUUID()
                );

        createVersion(
                playbook.getPlaybookId(),
                1,
                "TEST"
        );

        JsonNode expected =
                createVersion(
                        playbook.getPlaybookId(),
                        2,
                        "ACTIVE"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/playbook-versions/playbook/{playbookId}/version/{versionNumber}",
                                playbook.getPlaybookId(),
                                2
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.playbookVersionId")
                                .value(
                                        expected.get(
                                                "playbookVersionId"
                                        ).asText()
                                )
                )
                .andExpect(
                        jsonPath("$.playbookId")
                                .value(
                                        playbook.getPlaybookId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.versionNumber")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ACTIVE")
                );
    }

    @Test
    void shouldUpdatePlaybookVersion()
            throws Exception {

        PlaybookResponse playbook =
                createPlaybook(
                        "PB-VERSION-CTRL-" + UUID.randomUUID()
                );

        JsonNode created =
                createVersion(
                        playbook.getPlaybookId(),
                        1,
                        "DRAFT"
                );

        UUID playbookVersionId =
                UUID.fromString(
                        created.get(
                                "playbookVersionId"
                        ).asText()
                );

        Map<String, Object> updateRequest =
                buildRequest(
                        playbook.getPlaybookId(),
                        1,
                        "ACTIVE",
                        LocalDateTime.of(
                                2026,
                                8,
                                30,
                                12,
                                0
                        ),
                        LocalDateTime.of(
                                2026,
                                9,
                                30,
                                12,
                                0
                        )
                );

        mockMvc.perform(
                        put(
                                "/api/v1/playbook-versions/{playbookVersionId}",
                                playbookVersionId
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
                        jsonPath("$.playbookVersionId")
                                .value(
                                        playbookVersionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.playbookId")
                                .value(
                                        playbook.getPlaybookId()
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$.versionNumber")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ACTIVE")
                )
                .andExpect(
                        jsonPath("$.effectiveFrom")
                                .value(
                                        "2026-08-30T12:00:00"
                                )
                )
                .andExpect(
                        jsonPath("$.effectiveTo")
                                .value(
                                        "2026-09-30T12:00:00"
                                )
                )
                .andExpect(
                        jsonPath("$.updatedAt")
                                .exists()
                );
    }

    private JsonNode createVersion(
            UUID playbookId,
            Integer versionNumber,
            String status)
            throws Exception {

        Map<String, Object> request =
                buildRequest(
                        playbookId,
                        versionNumber,
                        status,
                        null,
                        null
                );

        MvcResult result =
                mockMvc.perform(
                                post("/api/v1/playbook-versions")
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
            UUID playbookId,
            Integer versionNumber,
            String status,
            LocalDateTime effectiveFrom,
            LocalDateTime effectiveTo) {

        Map<String, Object> request =
                new LinkedHashMap<>();

        request.put(
                "playbookId",
                playbookId
        );

        request.put(
                "versionNumber",
                versionNumber
        );

        request.put(
                "status",
                status
        );

        if (effectiveFrom != null) {
            request.put(
                    "effectiveFrom",
                    effectiveFrom
            );
        }

        if (effectiveTo != null) {
            request.put(
                    "effectiveTo",
                    effectiveTo
            );
        }

        return request;
    }

    private PlaybookResponse createPlaybook(
            String code) {

        PlaybookRequest request =
                new PlaybookRequest();

        request.setPlaybookCode(
                code
        );

        request.setPlaybookName(
                code
        );

        request.setStatus(
                "TEST"
        );

        return playbookService.create(
                request
        );
    }
}