package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RuleGroupRequest;
import com.efs.modules.rules.dto.RuleGroupResponse;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class RuleGroupServiceIntegrationTest {

    @Autowired
    private RuleGroupServiceInterface service;

    @Test
    void shouldCreateAndRetrieveRuleGroupById() {

        RuleGroupResponse created =
                service.createRuleGroup(
                        buildRequest(
                                "RULE-GROUP-001",
                                "Transaction Risk Rules",
                                "Transaction fraud detection rules",
                                "TRANSACTION",
                                "ACTIVE",
                                (short) 1
                        )
                );

        assertNotNull(
                created.getRuleGroupId()
        );

        assertEquals(
                "RULE-GROUP-001",
                created.getGroupCode()
        );

        assertEquals(
                "Transaction Risk Rules",
                created.getGroupName()
        );

        assertEquals(
                "Transaction fraud detection rules",
                created.getDescription()
        );

        assertEquals(
                "TRANSACTION",
                created.getCategory()
        );

        assertEquals(
                "ACTIVE",
                created.getStatus()
        );

        assertEquals(
                Short.valueOf((short) 1),
                created.getExecutionOrder()
        );

        assertNotNull(
                created.getCreatedAt()
        );

        assertNotNull(
                created.getUpdatedAt()
        );

        RuleGroupResponse retrieved =
                service.getRuleGroupById(
                        created.getRuleGroupId()
                );

        assertEquals(
                created.getRuleGroupId(),
                retrieved.getRuleGroupId()
        );
    }

    @Test
    void shouldRetrieveRuleGroupByCode() {

        RuleGroupResponse created =
                service.createRuleGroup(
                        buildRequest(
                                "RULE-GROUP-002",
                                "ATO Rules",
                                "Account takeover rules",
                                "ATO",
                                "ACTIVE",
                                (short) 2
                        )
                );

        RuleGroupResponse retrieved =
                service.getRuleGroupByCode(
                        "RULE-GROUP-002"
                );

        assertEquals(
                created.getRuleGroupId(),
                retrieved.getRuleGroupId()
        );

        assertEquals(
                "RULE-GROUP-002",
                retrieved.getGroupCode()
        );
    }

    @Test
    void shouldReturnRuleGroupsByStatusOrderedByExecutionOrder() {

        service.createRuleGroup(
                buildRequest(
                        "RULE-GROUP-003",
                        "Third Active Group",
                        null,
                        "TRANSACTION",
                        "ACTIVE",
                        (short) 3
                )
        );

        service.createRuleGroup(
                buildRequest(
                        "RULE-GROUP-004",
                        "First Active Group",
                        null,
                        "ATO",
                        "ACTIVE",
                        (short) 1
                )
        );

        List<RuleGroupResponse> groups =
                service.getRuleGroupsByStatus(
                        "ACTIVE"
                );

        assertEquals(
                2,
                groups.size()
        );

        assertEquals(
                Short.valueOf((short) 1),
                groups.get(0).getExecutionOrder()
        );

        assertEquals(
                Short.valueOf((short) 3),
                groups.get(1).getExecutionOrder()
        );
    }

    @Test
    void shouldReturnRuleGroupsByCategoryOrderedByExecutionOrder() {

        service.createRuleGroup(
                buildRequest(
                        "RULE-GROUP-005",
                        "Second Transaction Group",
                        null,
                        "TRANSACTION",
                        "ACTIVE",
                        (short) 2
                )
        );

        service.createRuleGroup(
                buildRequest(
                        "RULE-GROUP-006",
                        "First Transaction Group",
                        null,
                        "TRANSACTION",
                        "ACTIVE",
                        (short) 1
                )
        );

        List<RuleGroupResponse> groups =
                service.getRuleGroupsByCategory(
                        "TRANSACTION"
                );

        assertEquals(
                2,
                groups.size()
        );

        assertEquals(
                Short.valueOf((short) 1),
                groups.get(0).getExecutionOrder()
        );

        assertEquals(
                Short.valueOf((short) 2),
                groups.get(1).getExecutionOrder()
        );
    }

    @Test
    void shouldAllowOptionalFieldsToBeNull() {

        RuleGroupResponse created =
                service.createRuleGroup(
                        buildRequest(
                                "RULE-GROUP-007",
                                "Generic Rules",
                                null,
                                null,
                                "ACTIVE",
                                null
                        )
                );

        assertNotNull(
                created.getRuleGroupId()
        );

        assertNull(
                created.getDescription()
        );

        assertNull(
                created.getCategory()
        );

        assertNull(
                created.getExecutionOrder()
        );
    }

    @Test
    void shouldRejectDuplicateGroupCode() {

        service.createRuleGroup(
                buildRequest(
                        "RULE-GROUP-DUPLICATE",
                        "Original Group",
                        null,
                        "TRANSACTION",
                        "ACTIVE",
                        (short) 1
                )
        );

        assertThrows(
                DataIntegrityViolationException.class,
                () -> service.createRuleGroup(
                        buildRequest(
                                "RULE-GROUP-DUPLICATE",
                                "Duplicate Group",
                                null,
                                "ATO",
                                "ACTIVE",
                                (short) 2
                        )
                )
        );
    }

    @Test
    void shouldRejectUnknownRuleGroupId() {

        UUID unknownRuleGroupId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getRuleGroupById(
                        unknownRuleGroupId
                )
        );
    }

    @Test
    void shouldRejectUnknownGroupCode() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getRuleGroupByCode(
                        "UNKNOWN-RULE-GROUP"
                )
        );
    }

    private RuleGroupRequest buildRequest(
            String groupCode,
            String groupName,
            String description,
            String category,
            String status,
            Short executionOrder) {

        RuleGroupRequest request =
                new RuleGroupRequest();

        request.setGroupCode(
                groupCode
        );

        request.setGroupName(
                groupName
        );

        request.setDescription(
                description
        );

        request.setCategory(
                category
        );

        request.setStatus(
                status
        );

        request.setExecutionOrder(
                executionOrder
        );

        return request;
    }
}