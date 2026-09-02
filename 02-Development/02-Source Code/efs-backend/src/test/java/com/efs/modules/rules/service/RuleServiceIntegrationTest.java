package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleRequest;
import com.efs.modules.rules.dto.RuleResponse;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class RuleServiceIntegrationTest {

    @Autowired
    private RuleServiceInterface service;

    @Test
    void shouldCreateAndRetrieveRuleById() {

        RuleResponse created =
                service.createRule(
                        buildRequest(
                                "RULE-001",
                                "High Value Transaction Rule",
                                "TRANSACTION",
                                "HIGH",
                                (short) 1,
                                "ACTIVE"
                        )
                );

        assertNotNull(
                created.getRuleId()
        );

        assertEquals(
                "RULE-001",
                created.getRuleCode()
        );

        assertEquals(
                "High Value Transaction Rule",
                created.getRuleName()
        );

        assertEquals(
                "TRANSACTION",
                created.getCategory()
        );

        assertEquals(
                "HIGH",
                created.getSeverity()
        );

        assertEquals(
                Short.valueOf((short) 1),
                created.getPriority()
        );

        assertEquals(
                "FRAUD_RULES",
                created.getOwnerTeam()
        );

        assertEquals(
                Integer.valueOf(1),
                created.getCurrentVersion()
        );

        assertEquals(
                "ACTIVE",
                created.getStatus()
        );

        assertNotNull(
                created.getCreatedAt()
        );

        assertNotNull(
                created.getUpdatedAt()
        );

        RuleResponse retrieved =
                service.getRuleById(
                        created.getRuleId()
                );

        assertEquals(
                created.getRuleId(),
                retrieved.getRuleId()
        );
    }

    @Test
    void shouldReturnAllRulesOrderedByPriority() {

        service.createRule(
                buildRequest(
                        "RULE-LIST-001",
                        "List Rule One",
                        "TRANSACTION",
                        "HIGH",
                        (short) 5,
                        "ACTIVE"
                )
        );

        service.createRule(
                buildRequest(
                        "RULE-LIST-002",
                        "List Rule Two",
                        "ATO",
                        "CRITICAL",
                        (short) 1,
                        "ACTIVE"
                )
        );

        List<RuleResponse> rules =
                service.getRules();

        assertEquals(
                2,
                rules.size()
        );

        assertEquals(
                "RULE-LIST-002",
                rules.get(0).getRuleCode()
        );

        assertEquals(
                Short.valueOf((short) 1),
                rules.get(0).getPriority()
        );

        assertEquals(
                "RULE-LIST-001",
                rules.get(1).getRuleCode()
        );

        assertEquals(
                Short.valueOf((short) 5),
                rules.get(1).getPriority()
        );
    }

    @Test
    void shouldRetrieveRuleByCode() {

        service.createRule(
                buildRequest(
                        "RULE-002",
                        "Account Takeover Rule",
                        "ATO",
                        "CRITICAL",
                        (short) 2,
                        "ACTIVE"
                )
        );

        RuleResponse retrieved =
                service.getRuleByCode(
                        "RULE-002"
                );

        assertEquals(
                "RULE-002",
                retrieved.getRuleCode()
        );

        assertEquals(
                "Account Takeover Rule",
                retrieved.getRuleName()
        );
    }

    @Test
    void shouldReturnRulesByStatusOrderedByPriority() {

        service.createRule(
                buildRequest(
                        "RULE-STATUS-001",
                        "Status Rule One",
                        "TRANSACTION",
                        "HIGH",
                        (short) 5,
                        "ACTIVE"
                )
        );

        service.createRule(
                buildRequest(
                        "RULE-STATUS-002",
                        "Status Rule Two",
                        "TRANSACTION",
                        "MEDIUM",
                        (short) 2,
                        "ACTIVE"
                )
        );

        List<RuleResponse> rules =
                service.getRulesByStatus(
                        "ACTIVE"
                );

        assertEquals(
                2,
                rules.size()
        );

        assertEquals(
                Short.valueOf((short) 2),
                rules.get(0).getPriority()
        );

        assertEquals(
                Short.valueOf((short) 5),
                rules.get(1).getPriority()
        );
    }

    @Test
    void shouldReturnRulesByCategoryOrderedByPriority() {

        service.createRule(
                buildRequest(
                        "RULE-CATEGORY-001",
                        "Category Rule One",
                        "ATO",
                        "HIGH",
                        (short) 4,
                        "ACTIVE"
                )
        );

        service.createRule(
                buildRequest(
                        "RULE-CATEGORY-002",
                        "Category Rule Two",
                        "ATO",
                        "CRITICAL",
                        (short) 1,
                        "ACTIVE"
                )
        );

        List<RuleResponse> rules =
                service.getRulesByCategory(
                        "ATO"
                );

        assertEquals(
                2,
                rules.size()
        );

        assertEquals(
                Short.valueOf((short) 1),
                rules.get(0).getPriority()
        );

        assertEquals(
                Short.valueOf((short) 4),
                rules.get(1).getPriority()
        );
    }

    @Test
    void shouldReturnRulesBySeverityOrderedByPriority() {

        service.createRule(
                buildRequest(
                        "RULE-SEVERITY-001",
                        "Severity Rule One",
                        "TRANSACTION",
                        "HIGH",
                        (short) 3,
                        "ACTIVE"
                )
        );

        service.createRule(
                buildRequest(
                        "RULE-SEVERITY-002",
                        "Severity Rule Two",
                        "ATO",
                        "HIGH",
                        (short) 1,
                        "ACTIVE"
                )
        );

        List<RuleResponse> rules =
                service.getRulesBySeverity(
                        "HIGH"
                );

        assertEquals(
                2,
                rules.size()
        );

        assertEquals(
                Short.valueOf((short) 1),
                rules.get(0).getPriority()
        );

        assertEquals(
                Short.valueOf((short) 3),
                rules.get(1).getPriority()
        );
    }

    @Test
    void shouldRejectUnknownRuleId() {

        UUID unknownRuleId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getRuleById(
                        unknownRuleId
                )
        );
    }

    @Test
    void shouldRejectUnknownRuleCode() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getRuleByCode(
                        "RULE-NOT-FOUND"
                )
        );
    }

    private RuleRequest buildRequest(
            String ruleCode,
            String ruleName,
            String category,
            String severity,
            short priority,
            String status) {

        RuleRequest request =
                new RuleRequest();

        request.setRuleCode(
                ruleCode
        );

        request.setRuleName(
                ruleName
        );

        request.setDescription(
                "Rule integration test"
        );

        request.setCategory(
                category
        );

        request.setSeverity(
                severity
        );

        request.setPriority(
                priority
        );

        request.setOwnerTeam(
                "FRAUD_RULES"
        );

        request.setCurrentVersion(
                1
        );

        request.setStatus(
                status
        );

        return request;
    }
}