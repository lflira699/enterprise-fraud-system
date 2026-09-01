package com.efs.modules.detection.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DetectionScenarioSearchControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSearchScenariosWithDefaultPaginationAndSorting()
            throws Exception {

        String category =
                uniqueValue("SEARCH-CATEGORY");

        createScenario(
                uniqueValue("SEARCH-Z"),
                "Zulu Search Scenario",
                category,
                "ACTIVE",
                "HIGH",
                "DetectionTeam"
        );

        createScenario(
                uniqueValue("SEARCH-A"),
                "Alpha Search Scenario",
                category,
                "ACTIVE",
                "HIGH",
                "DetectionTeam"
        );

        createScenario(
                uniqueValue("SEARCH-M"),
                "Mike Search Scenario",
                category,
                "ACTIVE",
                "HIGH",
                "DetectionTeam"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenarios"
                        )
                                .param(
                                        "category",
                                        category
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.page")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.size")
                                .value(25)
                )
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(3)
                )
                .andExpect(
                        jsonPath("$.totalPages")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.hasNext")
                                .value(false)
                )
                .andExpect(
                        jsonPath("$.hasPrevious")
                                .value(false)
                )
                .andExpect(
                        jsonPath("$.content.length()")
                                .value(3)
                )
                .andExpect(
                        jsonPath("$.content[0].scenarioName")
                                .value(
                                        "Alpha Search Scenario"
                                )
                )
                .andExpect(
                        jsonPath("$.content[1].scenarioName")
                                .value(
                                        "Mike Search Scenario"
                                )
                )
                .andExpect(
                        jsonPath("$.content[2].scenarioName")
                                .value(
                                        "Zulu Search Scenario"
                                )
                );
    }

    @Test
    void shouldSearchScenariosWithPaginationAndDescendingSort()
            throws Exception {

        String category =
                uniqueValue("PAGE-CATEGORY");

        createScenario(
                uniqueValue("PAGE-A"),
                "Alpha Page Scenario",
                category,
                "ACTIVE",
                "HIGH",
                "DetectionTeam"
        );

        createScenario(
                uniqueValue("PAGE-B"),
                "Bravo Page Scenario",
                category,
                "ACTIVE",
                "HIGH",
                "DetectionTeam"
        );

        createScenario(
                uniqueValue("PAGE-C"),
                "Charlie Page Scenario",
                category,
                "ACTIVE",
                "HIGH",
                "DetectionTeam"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenarios"
                        )
                                .param(
                                        "category",
                                        category
                                )
                                .param(
                                        "page",
                                        "1"
                                )
                                .param(
                                        "size",
                                        "1"
                                )
                                .param(
                                        "sort",
                                        "scenarioName"
                                )
                                .param(
                                        "direction",
                                        "DESC"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.page")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.size")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(3)
                )
                .andExpect(
                        jsonPath("$.totalPages")
                                .value(3)
                )
                .andExpect(
                        jsonPath("$.hasNext")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.hasPrevious")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.content.length()")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.content[0].scenarioName")
                                .value(
                                        "Bravo Page Scenario"
                                )
                );
    }

    @Test
    void shouldSearchScenariosUsingCombinedFilters()
            throws Exception {

        String scenarioCode =
                uniqueValue("COMBINED-CODE");

        String category =
                uniqueValue("COMBINED-CATEGORY");

        String statusValue =
                uniqueValue("COMBINED-STATUS");

        String criticality =
                uniqueValue("COMBINED-CRITICALITY");

        String owner =
                uniqueValue("COMBINED-OWNER");

        createScenario(
                scenarioCode,
                "Combined Matching Scenario",
                category,
                statusValue,
                criticality,
                owner
        );

        createScenario(
                uniqueValue("NON-MATCHING"),
                "Combined Non Matching Scenario",
                category,
                statusValue,
                criticality,
                owner
        );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenarios"
                        )
                                .param(
                                        "scenarioCode",
                                        scenarioCode
                                )
                                .param(
                                        "category",
                                        category
                                )
                                .param(
                                        "status",
                                        statusValue
                                )
                                .param(
                                        "criticality",
                                        criticality
                                )
                                .param(
                                        "owner",
                                        owner
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.content.length()")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.content[0].scenarioCode")
                                .value(scenarioCode)
                )
                .andExpect(
                        jsonPath("$.content[0].category")
                                .value(category)
                )
                .andExpect(
                        jsonPath("$.content[0].status")
                                .value(statusValue)
                )
                .andExpect(
                        jsonPath("$.content[0].criticality")
                                .value(criticality)
                )
                .andExpect(
                        jsonPath("$.content[0].owner")
                                .value(owner)
                );
    }

    @Test
    void shouldRejectNegativePage()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenarios"
                        )
                                .param(
                                        "page",
                                        "-1"
                                )
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectPageSizeAboveMaximum()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenarios"
                        )
                                .param(
                                        "size",
                                        "101"
                                )
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectUnsupportedSortField()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenarios"
                        )
                                .param(
                                        "sort",
                                        "createdAt"
                                )
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectUnsupportedSortDirection()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenarios"
                        )
                                .param(
                                        "direction",
                                        "INVALID"
                                )
                )
                .andExpect(status().isBadRequest());
    }

    private void createScenario(
            String scenarioCode,
            String scenarioName,
            String category,
            String statusValue,
            String criticality,
            String owner)
            throws Exception {

        Map<String, Object> request =
                new LinkedHashMap<>();

        request.put(
                "scenarioCode",
                scenarioCode
        );

        request.put(
                "scenarioName",
                scenarioName
        );

        request.put(
                "objective",
                "Detection scenario search integration test"
        );

        request.put(
                "category",
                category
        );

        request.put(
                "criticality",
                criticality
        );

        request.put(
                "status",
                statusValue
        );

        request.put(
                "owner",
                owner
        );

        request.put(
                "version",
                1
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
                .andExpect(status().isCreated());
    }

    private String uniqueValue(
            String prefix) {

        return prefix
                + "-"
                + UUID.randomUUID()
                .toString()
                .substring(
                        0,
                        8
                );
    }
}