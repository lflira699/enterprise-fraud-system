package com.efs.modules.rules.controller;

import org.junit.jupiter.api.BeforeEach;
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
class RuleSimulationControllerIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "40404040-4040-4040-4040-404040404040"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "41414141-4141-4141-4141-414141414141"
            );

    private static final UUID ENTITY_ID =
            UUID.fromString(
                    "42424242-4242-4242-4242-424242424242"
            );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertUser();
    }

    @Test
    void shouldCreateRuleSimulationThroughApi()
            throws Exception {

        String requestBody =
                """
                {
                    "simulationName": "ATO API Simulation",
                    "entityType": "RULE",
                    "entityId": "%s",
                    "datasetReference": "dataset://ato/api-001",
                    "sampleSize": 1000,
                    "simulationStatus": "COMPLETED",
                    "matchCount": 125,
                    "approveCount": 700,
                    "rejectCount": 150,
                    "reviewCount": 150,
                    "resultSummary": {
                        "matchRate": 0.125,
                        "decision": "REVIEW"
                    },
                    "executedBy": "%s"
                }
                """.formatted(
                        ENTITY_ID,
                        USER_ID
                );

        mockMvc.perform(
                        post("/api/v1/rules/simulations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.simulationId").exists())
                .andExpect(jsonPath("$.simulationName").value(
                        "ATO API Simulation"
                ))
                .andExpect(jsonPath("$.entityType").value(
                        "RULE"
                ))
                .andExpect(jsonPath("$.entityId").value(
                        ENTITY_ID.toString()
                ))
                .andExpect(jsonPath("$.datasetReference").value(
                        "dataset://ato/api-001"
                ))
                .andExpect(jsonPath("$.sampleSize").value(1000))
                .andExpect(jsonPath("$.simulationStatus").value(
                        "COMPLETED"
                ))
                .andExpect(jsonPath("$.matchCount").value(125))
                .andExpect(jsonPath("$.approveCount").value(700))
                .andExpect(jsonPath("$.rejectCount").value(150))
                .andExpect(jsonPath("$.reviewCount").value(150))
                .andExpect(jsonPath("$.executedBy").value(
                        USER_ID.toString()
                ))
                .andExpect(jsonPath("$.resultSummary.decision").value(
                        "REVIEW"
                ))
                .andExpect(jsonPath("$.startedAt").exists())
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void shouldRetrieveRuleSimulationByIdThroughApi()
            throws Exception {

        UUID simulationId =
                insertRuleSimulation(
                        "Simulation By Id",
                        "RULE",
                        ENTITY_ID,
                        "dataset://simulation/id",
                        500L,
                        "COMPLETED",
                        50L,
                        350L,
                        75L,
                        75L
                );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/simulations/{simulationId}",
                                simulationId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.simulationId").value(
                        simulationId.toString()
                ))
                .andExpect(jsonPath("$.simulationName").value(
                        "Simulation By Id"
                ))
                .andExpect(jsonPath("$.entityId").value(
                        ENTITY_ID.toString()
                ))
                .andExpect(jsonPath("$.executedBy").value(
                        USER_ID.toString()
                ));
    }

    @Test
    void shouldRetrieveRuleSimulationsByEntityThroughApi()
            throws Exception {

        insertRuleSimulation(
                "Entity Simulation One",
                "RULE",
                ENTITY_ID,
                "dataset://entity/one",
                100L,
                "COMPLETED",
                10L,
                70L,
                15L,
                15L
        );

        Thread.sleep(5);

        insertRuleSimulation(
                "Entity Simulation Two",
                "RULE",
                ENTITY_ID,
                "dataset://entity/two",
                200L,
                "COMPLETED",
                20L,
                140L,
                30L,
                30L
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/simulations/entity/{entityType}/{entityId}",
                                "RULE",
                                ENTITY_ID
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].simulationName").value(
                        "Entity Simulation Two"
                ))
                .andExpect(jsonPath("$[1].simulationName").value(
                        "Entity Simulation One"
                ));
    }

    @Test
    void shouldRetrieveRuleSimulationsByStatusThroughApi()
            throws Exception {

        insertRuleSimulation(
                "Status Simulation One",
                "RULE",
                UUID.randomUUID(),
                "dataset://status/one",
                100L,
                "COMPLETED",
                10L,
                70L,
                15L,
                15L
        );

        Thread.sleep(5);

        insertRuleSimulation(
                "Status Simulation Two",
                "POLICY",
                UUID.randomUUID(),
                "dataset://status/two",
                120L,
                "COMPLETED",
                12L,
                80L,
                15L,
                25L
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/simulations/status/{simulationStatus}",
                                "COMPLETED"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].simulationName").value(
                        "Status Simulation Two"
                ))
                .andExpect(jsonPath("$[1].simulationName").value(
                        "Status Simulation One"
                ));
    }

    @Test
    void shouldRetrieveRuleSimulationsByExecutedByThroughApi()
            throws Exception {

        insertRuleSimulation(
                "Executor Simulation One",
                "RULE",
                UUID.randomUUID(),
                "dataset://executed-by/one",
                90L,
                "COMPLETED",
                9L,
                60L,
                15L,
                15L
        );

        Thread.sleep(5);

        insertRuleSimulation(
                "Executor Simulation Two",
                "RULE",
                UUID.randomUUID(),
                "dataset://executed-by/two",
                110L,
                "RUNNING",
                0L,
                0L,
                0L,
                0L
        );

        mockMvc.perform(
                        get(
                                "/api/v1/rules/simulations/executed-by/{executedBy}",
                                USER_ID
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].simulationName").value(
                        "Executor Simulation Two"
                ))
                .andExpect(jsonPath("$[1].simulationName").value(
                        "Executor Simulation One"
                ));
    }

    private UUID insertRuleSimulation(
            String simulationName,
            String entityType,
            UUID entityId,
            String datasetReference,
            long sampleSize,
            String simulationStatus,
            long matchCount,
            long approveCount,
            long rejectCount,
            long reviewCount) {

        UUID simulationId =
                UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO rules.rule_simulation (
                    simulation_id,
                    simulation_name,
                    entity_type,
                    entity_id,
                    dataset_reference,
                    sample_size,
                    started_at,
                    completed_at,
                    simulation_status,
                    match_count,
                    approve_count,
                    reject_count,
                    review_count,
                    result_summary,
                    executed_by,
                    created_at
                )
                VALUES (
                    ?, ?, ?, ?, ?, ?,
                    clock_timestamp(),
                    NULL,
                    ?, ?, ?, ?, ?,
                    NULL,
                    ?,
                    clock_timestamp()
                )
                """,
                simulationId,
                simulationName,
                entityType,
                entityId,
                datasetReference,
                sampleSize,
                simulationStatus,
                matchCount,
                approveCount,
                rejectCount,
                reviewCount,
                USER_ID
        );

        return simulationId;
    }

    private void insertOrganization() {

        jdbcTemplate.update(
                """
                INSERT INTO administration.organization (
                    organization_id,
                    organization_code,
                    legal_name,
                    country_code,
                    timezone,
                    status
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                ORGANIZATION_ID,
                "EFS-RULE-SIMULATION-API-ORG",
                "EFS Rule Simulation API Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );
    }

    private void insertUser() {

        jdbcTemplate.update(
                """
                INSERT INTO administration.user_account (
                    user_id,
                    organization_id,
                    username,
                    full_name,
                    email,
                    authentication_provider,
                    mfa_enabled,
                    account_status,
                    failed_login_attempts
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                USER_ID,
                ORGANIZATION_ID,
                "efs.rule.simulation.api",
                "EFS Rule Simulation API User",
                "efs.rule.simulation.api@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }
}