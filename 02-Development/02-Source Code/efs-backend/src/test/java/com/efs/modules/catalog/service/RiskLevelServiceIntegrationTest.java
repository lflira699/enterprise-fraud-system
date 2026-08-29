package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.RiskLevelRequest;
import com.efs.modules.catalog.dto.RiskLevelResponse;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class RiskLevelServiceIntegrationTest {

    @Autowired
    private RiskLevelServiceInterface riskLevelService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateAndRetrieveRiskLevelById() {

        RiskLevelRequest request =
                new RiskLevelRequest();

        request.setRiskCode("HIGH");
        request.setRiskName("High Risk");
        request.setDescription("High analytical risk level");
        request.setDisplayOrder((short) 3);
        request.setStatus("ACTIVE");

        RiskLevelResponse created =
                riskLevelService.createRiskLevel(request);

        assertNotNull(created);
        assertNotNull(created.getRiskLevelId());

        assertEquals(
                "HIGH",
                created.getRiskCode()
        );

        assertEquals(
                "High Risk",
                created.getRiskName()
        );

        assertEquals(
                "High analytical risk level",
                created.getDescription()
        );

        assertEquals(
                Short.valueOf((short) 3),
                created.getDisplayOrder()
        );

        assertEquals(
                "ACTIVE",
                created.getStatus()
        );

        assertNotNull(
                created.getCreatedAt()
        );

        RiskLevelResponse retrieved =
                riskLevelService.getRiskLevelById(
                        created.getRiskLevelId()
                );

        assertEquals(
                created.getRiskLevelId(),
                retrieved.getRiskLevelId()
        );
    }

    @Test
    void shouldRetrieveRiskLevelByRiskCode() {

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

        RiskLevelResponse result =
                riskLevelService.getRiskLevelByRiskCode(
                        "MEDIUM"
                );

        assertEquals(
                riskLevelId,
                result.getRiskLevelId()
        );

        assertEquals(
                "MEDIUM",
                result.getRiskCode()
        );

        assertEquals(
                "Medium Risk",
                result.getRiskName()
        );
    }

    @Test
    void shouldReturnRiskLevelsByStatusOrderedByDisplayOrder() {

        insertRiskLevel(
                UUID.randomUUID(),
                "HIGH",
                "High Risk",
                "High analytical risk level",
                (short) 3,
                "ACTIVE"
        );

        insertRiskLevel(
                UUID.randomUUID(),
                "LOW",
                "Low Risk",
                "Low analytical risk level",
                (short) 1,
                "ACTIVE"
        );

        insertRiskLevel(
                UUID.randomUUID(),
                "MEDIUM",
                "Medium Risk",
                "Medium analytical risk level",
                (short) 2,
                "ACTIVE"
        );

        List<RiskLevelResponse> results =
                riskLevelService.getRiskLevelsByStatus(
                        "ACTIVE"
                );

        assertEquals(
                3,
                results.size()
        );

        assertEquals(
                "LOW",
                results.get(0).getRiskCode()
        );

        assertEquals(
                "MEDIUM",
                results.get(1).getRiskCode()
        );

        assertEquals(
                "HIGH",
                results.get(2).getRiskCode()
        );

        assertTrue(
                results.stream()
                        .allMatch(
                                result ->
                                        "ACTIVE".equals(
                                                result.getStatus()
                                        )
                        )
        );
    }

    @Test
    void shouldReturnAllRiskLevelsOrderedByDisplayOrder() {

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

        List<RiskLevelResponse> results =
                riskLevelService.getAllRiskLevels();

        assertEquals(
                3,
                results.size()
        );

        assertEquals(
                Short.valueOf((short) 1),
                results.get(0).getDisplayOrder()
        );

        assertEquals(
                Short.valueOf((short) 2),
                results.get(1).getDisplayOrder()
        );

        assertEquals(
                Short.valueOf((short) 3),
                results.get(2).getDisplayOrder()
        );
    }

    @Test
    void shouldRejectUnknownRiskLevelId() {

        UUID unknownRiskLevelId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        riskLevelService.getRiskLevelById(
                                unknownRiskLevelId
                        )
        );
    }

    @Test
    void shouldRejectUnknownRiskCode() {

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        riskLevelService.getRiskLevelByRiskCode(
                                "UNKNOWN"
                        )
        );
    }

    @Test
    void shouldPersistRiskLevelFields() {

        RiskLevelRequest request =
                new RiskLevelRequest();

        request.setRiskCode("CRITICAL");
        request.setRiskName("Critical Risk");
        request.setDescription(
                "Critical analytical risk level"
        );
        request.setDisplayOrder((short) 4);
        request.setStatus("ACTIVE");

        RiskLevelResponse created =
                riskLevelService.createRiskLevel(request);

        String riskCode =
                jdbcTemplate.queryForObject(
                        """
                        SELECT risk_code
                        FROM catalog.risk_level
                        WHERE risk_level_id = ?
                        """,
                        String.class,
                        created.getRiskLevelId()
                );

        String riskName =
                jdbcTemplate.queryForObject(
                        """
                        SELECT risk_name
                        FROM catalog.risk_level
                        WHERE risk_level_id = ?
                        """,
                        String.class,
                        created.getRiskLevelId()
                );

        Short displayOrder =
                jdbcTemplate.queryForObject(
                        """
                        SELECT display_order
                        FROM catalog.risk_level
                        WHERE risk_level_id = ?
                        """,
                        Short.class,
                        created.getRiskLevelId()
                );

        assertEquals(
                "CRITICAL",
                riskCode
        );

        assertEquals(
                "Critical Risk",
                riskName
        );

        assertEquals(
                Short.valueOf((short) 4),
                displayOrder
        );
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