package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleSimulationRequest;
import com.efs.modules.rules.dto.RuleSimulationResponse;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class RuleSimulationServiceIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "37373737-3737-3737-3737-373737373737"
            );

    private static final UUID USER_ID =
            UUID.fromString(
                    "38383838-3838-3838-3838-383838383838"
            );

    private static final UUID ENTITY_ID =
            UUID.fromString(
                    "39393939-3939-3939-3939-393939393939"
            );

    @Autowired
    private RuleSimulationServiceInterface service;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        insertOrganization();
        insertUser();
    }

    @Test
    void shouldCreateAndRetrieveRuleSimulationById() {

        RuleSimulationResponse created =
                service.createRuleSimulation(
                        buildRequest(
                                "ATO Rule Simulation",
                                "RULE",
                                ENTITY_ID,
                                "dataset://ato/simulation-001",
                                1000L,
                                "COMPLETED",
                                125L,
                                700L,
                                150L,
                                150L,
                                Map.of(
                                        "matchRate",
                                        0.125,
                                        "decision",
                                        "REVIEW"
                                )
                        )
                );

        assertNotNull(
                created.getSimulationId()
        );

        assertEquals(
                "ATO Rule Simulation",
                created.getSimulationName()
        );

        assertEquals(
                "RULE",
                created.getEntityType()
        );

        assertEquals(
                ENTITY_ID,
                created.getEntityId()
        );

        assertEquals(
                "dataset://ato/simulation-001",
                created.getDatasetReference()
        );

        assertEquals(
                Long.valueOf(1000L),
                created.getSampleSize()
        );

        assertEquals(
                "COMPLETED",
                created.getSimulationStatus()
        );

        assertEquals(
                Long.valueOf(125L),
                created.getMatchCount()
        );

        assertEquals(
                Long.valueOf(700L),
                created.getApproveCount()
        );

        assertEquals(
                Long.valueOf(150L),
                created.getRejectCount()
        );

        assertEquals(
                Long.valueOf(150L),
                created.getReviewCount()
        );

        assertEquals(
                USER_ID,
                created.getExecutedBy()
        );

        assertNotNull(
                created.getStartedAt()
        );

        assertNotNull(
                created.getCreatedAt()
        );

        assertNull(
                created.getCompletedAt()
        );

        assertNotNull(
                created.getResultSummary()
        );

        assertEquals(
                "REVIEW",
                created.getResultSummary().get("decision")
        );

        RuleSimulationResponse retrieved =
                service.getRuleSimulationById(
                        created.getSimulationId()
                );

        assertEquals(
                created.getSimulationId(),
                retrieved.getSimulationId()
        );
    }

    @Test
    void shouldReturnSimulationsByEntityOrderedByStartedAtDescending() {

        RuleSimulationResponse first =
                service.createRuleSimulation(
                        buildRequest(
                                "Simulation One",
                                "RULE",
                                ENTITY_ID,
                                "dataset://simulation/one",
                                100L,
                                "COMPLETED",
                                10L,
                                70L,
                                15L,
                                15L,
                                null
                        )
                );

        sleepBriefly();

        RuleSimulationResponse second =
                service.createRuleSimulation(
                        buildRequest(
                                "Simulation Two",
                                "RULE",
                                ENTITY_ID,
                                "dataset://simulation/two",
                                200L,
                                "COMPLETED",
                                20L,
                                140L,
                                30L,
                                30L,
                                null
                        )
                );

        List<RuleSimulationResponse> simulations =
                service.getRuleSimulationsByEntity(
                        "RULE",
                        ENTITY_ID
                );

        assertEquals(
                2,
                simulations.size()
        );

        assertEquals(
                second.getSimulationId(),
                simulations.get(0).getSimulationId()
        );

        assertEquals(
                first.getSimulationId(),
                simulations.get(1).getSimulationId()
        );
    }

    @Test
    void shouldReturnSimulationsByStatusOrderedByStartedAtDescending() {

        RuleSimulationResponse first =
                service.createRuleSimulation(
                        buildRequest(
                                "Completed Simulation One",
                                "POLICY",
                                UUID.randomUUID(),
                                "dataset://status/one",
                                50L,
                                "COMPLETED",
                                5L,
                                30L,
                                10L,
                                10L,
                                null
                        )
                );

        sleepBriefly();

        RuleSimulationResponse second =
                service.createRuleSimulation(
                        buildRequest(
                                "Completed Simulation Two",
                                "POLICY",
                                UUID.randomUUID(),
                                "dataset://status/two",
                                75L,
                                "COMPLETED",
                                8L,
                                50L,
                                10L,
                                15L,
                                null
                        )
                );

        List<RuleSimulationResponse> simulations =
                service.getRuleSimulationsByStatus(
                        "COMPLETED"
                );

        assertEquals(
                2,
                simulations.size()
        );

        assertEquals(
                second.getSimulationId(),
                simulations.get(0).getSimulationId()
        );

        assertEquals(
                first.getSimulationId(),
                simulations.get(1).getSimulationId()
        );
    }

    @Test
    void shouldReturnSimulationsByExecutedByOrderedByStartedAtDescending() {

        RuleSimulationResponse first =
                service.createRuleSimulation(
                        buildRequest(
                                "User Simulation One",
                                "RULE",
                                UUID.randomUUID(),
                                "dataset://user/one",
                                100L,
                                "COMPLETED",
                                10L,
                                60L,
                                20L,
                                20L,
                                null
                        )
                );

        sleepBriefly();

        RuleSimulationResponse second =
                service.createRuleSimulation(
                        buildRequest(
                                "User Simulation Two",
                                "RULE",
                                UUID.randomUUID(),
                                "dataset://user/two",
                                120L,
                                "COMPLETED",
                                12L,
                                75L,
                                20L,
                                25L,
                                null
                        )
                );

        List<RuleSimulationResponse> simulations =
                service.getRuleSimulationsByExecutedBy(
                        USER_ID
                );

        assertEquals(
                2,
                simulations.size()
        );

        assertEquals(
                second.getSimulationId(),
                simulations.get(0).getSimulationId()
        );

        assertEquals(
                first.getSimulationId(),
                simulations.get(1).getSimulationId()
        );
    }

    @Test
    void shouldPreserveJsonResultSummary() {

        Map<String, Object> resultSummary =
                Map.of(
                        "matchRate",
                        0.25,
                        "approvalRate",
                        0.60,
                        "rejectRate",
                        0.10,
                        "reviewRate",
                        0.30
                );

        RuleSimulationResponse created =
                service.createRuleSimulation(
                        buildRequest(
                                "JSON Simulation",
                                "RULE",
                                ENTITY_ID,
                                "dataset://json/simulation",
                                500L,
                                "COMPLETED",
                                125L,
                                300L,
                                50L,
                                150L,
                                resultSummary
                        )
                );

        assertNotNull(
                created.getResultSummary()
        );

        assertEquals(
                0.25,
                created.getResultSummary().get("matchRate")
        );

        assertEquals(
                0.60,
                created.getResultSummary().get("approvalRate")
        );

        assertEquals(
                0.10,
                created.getResultSummary().get("rejectRate")
        );

        assertEquals(
                0.30,
                created.getResultSummary().get("reviewRate")
        );
    }

    @Test
    void shouldAllowNullResultSummaryAndCompletedAt() {

        RuleSimulationResponse created =
                service.createRuleSimulation(
                        buildRequest(
                                "Running Simulation",
                                "RULE",
                                ENTITY_ID,
                                "dataset://running/simulation",
                                250L,
                                "RUNNING",
                                0L,
                                0L,
                                0L,
                                0L,
                                null
                        )
                );

        assertNotNull(
                created.getSimulationId()
        );

        assertNull(
                created.getResultSummary()
        );

        assertNull(
                created.getCompletedAt()
        );

        assertEquals(
                "RUNNING",
                created.getSimulationStatus()
        );
    }

    @Test
    void shouldRejectUnknownSimulationId() {

        UUID unknownSimulationId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getRuleSimulationById(
                        unknownSimulationId
                )
        );
    }

    private RuleSimulationRequest buildRequest(
            String simulationName,
            String entityType,
            UUID entityId,
            String datasetReference,
            Long sampleSize,
            String simulationStatus,
            Long matchCount,
            Long approveCount,
            Long rejectCount,
            Long reviewCount,
            Map<String, Object> resultSummary) {

        RuleSimulationRequest request =
                new RuleSimulationRequest();

        request.setSimulationName(
                simulationName
        );

        request.setEntityType(
                entityType
        );

        request.setEntityId(
                entityId
        );

        request.setDatasetReference(
                datasetReference
        );

        request.setSampleSize(
                sampleSize
        );

        request.setSimulationStatus(
                simulationStatus
        );

        request.setMatchCount(
                matchCount
        );

        request.setApproveCount(
                approveCount
        );

        request.setRejectCount(
                rejectCount
        );

        request.setReviewCount(
                reviewCount
        );

        request.setResultSummary(
                resultSummary
        );

        request.setExecutedBy(
                USER_ID
        );

        return request;
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
                "EFS-RULE-SIMULATION-TEST-ORG",
                "EFS Rule Simulation Test Organization",
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
                "efs.rule.simulation.test",
                "EFS Rule Simulation Test User",
                "efs.rule.simulation.test@example.com",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );
    }

    private void sleepBriefly() {

        try {
            Thread.sleep(5);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}