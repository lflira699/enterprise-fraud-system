package com.efs.modules.catalog.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RiskLevelControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateRiskLevel()
            throws Exception {

        String requestBody =
                """
                {
                  "riskCode": "HIGH",
                  "riskName": "High Risk",
                  "description": "High analytical risk level",
                  "displayOrder": 3,
                  "status": "ACTIVE"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/risk-levels")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.riskLevelId").exists())
                .andExpect(
                        jsonPath("$.riskCode")
                                .value("HIGH")
                )
                .andExpect(
                        jsonPath("$.riskName")
                                .value("High Risk")
                )
                .andExpect(
                        jsonPath("$.description")
                                .value("High analytical risk level")
                )
                .andExpect(
                        jsonPath("$.displayOrder")
                                .value(3)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ACTIVE")
                )
                .andExpect(
                        jsonPath("$.createdAt").exists()
                );
    }

    @Test
    void shouldRejectInvalidRiskLevel()
            throws Exception {

        String requestBody =
                """
                {
                  "riskCode": "",
                  "riskName": "",
                  "description": "Invalid risk level",
                  "status": ""
                }
                """;

        mockMvc.perform(
                        post("/api/v1/risk-levels")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetRiskLevelById()
            throws Exception {

        UUID riskLevelId =
                UUID.randomUUID();

        insertRiskLevel(
                riskLevelId,
                "MEDIUM",
                "Medium Risk",
                "Medium analytical risk level",
                (short) 2,
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/risk-levels/{riskLevelId}",
                                riskLevelId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.riskLevelId")
                                .value(riskLevelId.toString())
                )
                .andExpect(
                        jsonPath("$.riskCode")
                                .value("MEDIUM")
                )
                .andExpect(
                        jsonPath("$.riskName")
                                .value("Medium Risk")
                )
                .andExpect(
                        jsonPath("$.displayOrder")
                                .value(2)
                );
    }

    @Test
    void shouldGetRiskLevelByRiskCode()
            throws Exception {

        UUID riskLevelId =
                UUID.randomUUID();

        insertRiskLevel(
                riskLevelId,
                "HIGH",
                "High Risk",
                "High analytical risk level",
                (short) 3,
                "ACTIVE"
        );

        mockMvc.perform(
                        get(
                                "/api/v1/risk-levels/code/{riskCode}",
                                "HIGH"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.riskLevelId")
                                .value(riskLevelId.toString())
                )
                .andExpect(
                        jsonPath("$.riskCode")
                                .value("HIGH")
                )
                .andExpect(
                        jsonPath("$.riskName")
                                .value("High Risk")
                );
    }

    @Test
    void shouldGetRiskLevelsByStatusOrderedByDisplayOrder()
            throws Exception {

        insertRiskLevel(
                UUID.randomUUID(),
                "HIGH",
                "High Risk",
                null,
                (short) 3,
                "ACTIVE"
        );

        insertRiskLevel(
                UUID.randomUUID(),
                "LOW",
                "Low Risk",
                null,
                (short) 1,
                "ACTIVE"
        );

        insertRiskLevel(
                UUID.randomUUID(),
                "MEDIUM",
                "Medium Risk",
                null,
                (short) 2,
                "ACTIVE"
        );

        mockMvc.perform(
                        get("/api/v1/risk-levels")
                                .param("status", "ACTIVE")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(3)
                )
                .andExpect(
                        jsonPath("$[0].riskCode")
                                .value("LOW")
                )
                .andExpect(
                        jsonPath("$[1].riskCode")
                                .value("MEDIUM")
                )
                .andExpect(
                        jsonPath("$[2].riskCode")
                                .value("HIGH")
                );
    }

    @Test
    void shouldGetAllRiskLevelsOrderedByDisplayOrder()
            throws Exception {

        insertRiskLevel(
                UUID.randomUUID(),
                "HIGH",
                "High Risk",
                null,
                (short) 3,
                "ACTIVE"
        );

        insertRiskLevel(
                UUID.randomUUID(),
                "LOW",
                "Low Risk",
                null,
                (short) 1,
                "ACTIVE"
        );

        insertRiskLevel(
                UUID.randomUUID(),
                "MEDIUM",
                "Medium Risk",
                null,
                (short) 2,
                "ACTIVE"
        );

        mockMvc.perform(
                        get("/api/v1/risk-levels")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(3)
                )
                .andExpect(
                        jsonPath("$[0].displayOrder")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[1].displayOrder")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[2].displayOrder")
                                .value(3)
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownRiskLevelId()
            throws Exception {

        UUID unknownRiskLevelId =
                UUID.randomUUID();

        mockMvc.perform(
                        get(
                                "/api/v1/risk-levels/{riskLevelId}",
                                unknownRiskLevelId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundForUnknownRiskCode()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/risk-levels/code/{riskCode}",
                                "UNKNOWN"
                        )
                )
                .andExpect(status().isNotFound());
    }

    private void insertRiskLevel(
            UUID riskLevelId,
            String riskCode,
            String riskName,
            String description,
            Short displayOrder,
            String status) {

        jdbcTemplate.update(
                """
                INSERT INTO catalog.risk_level (
                    risk_level_id,
                    risk_code,
                    risk_name,
                    description,
                    display_order,
                    status,
                    created_at
                )
                VALUES (
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    clock_timestamp()
                )
                """,
                riskLevelId,
                riskCode,
                riskName,
                description,
                displayOrder,
                status
        );
    }
}