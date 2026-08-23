package com.efs.modules.rules.service;

import com.efs.modules.rules.dto.RulePolicyRequest;
import com.efs.modules.rules.dto.RulePolicyResponse;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class RulePolicyServiceIntegrationTest {

    private static final UUID ORGANIZATION_ID =
            UUID.fromString(
                    "13131313-1313-1313-1313-131313131313"
            );

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "14141414-1414-1414-1414-141414141414"
            );

    @Autowired
    private RulePolicyServiceInterface service;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

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
                "EFS-RULE-POLICY-TEST-ORG",
                "EFS Rule Policy Test Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );

        jdbcTemplate.update(
                """
                INSERT INTO administration.tenant (
                    tenant_id,
                    organization_id,
                    tenant_code,
                    tenant_name,
                    status,
                    environment
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                TENANT_ID,
                ORGANIZATION_ID,
                "EFS-RULE-POLICY-TEST-TENANT",
                "EFS Rule Policy Test Tenant",
                "ACTIVE",
                "DEVELOPMENT"
        );
    }

    @Test
    void shouldCreateAndRetrieveRulePolicyById() {

        RulePolicyResponse created =
                service.createRulePolicy(
                        buildRequest(
                                "POLICY-001",
                                "High Risk Transaction Policy",
                                "Transaction risk policy",
                                "TRANSACTION",
                                ORGANIZATION_ID,
                                TENANT_ID,
                                "ACTIVE",
                                LocalDateTime.now(),
                                null,
                                (short) 1
                        )
                );

        assertNotNull(
                created.getPolicyId()
        );

        assertEquals(
                "POLICY-001",
                created.getPolicyCode()
        );

        assertEquals(
                "High Risk Transaction Policy",
                created.getPolicyName()
        );

        assertEquals(
                "Transaction risk policy",
                created.getDescription()
        );

        assertEquals(
                "TRANSACTION",
                created.getPolicyType()
        );

        assertEquals(
                ORGANIZATION_ID,
                created.getOrganizationId()
        );

        assertEquals(
                TENANT_ID,
                created.getTenantId()
        );

        assertEquals(
                "ACTIVE",
                created.getStatus()
        );

        assertEquals(
                Short.valueOf((short) 1),
                created.getPriority()
        );

        assertNotNull(
                created.getCreatedAt()
        );

        assertNotNull(
                created.getUpdatedAt()
        );

        RulePolicyResponse retrieved =
                service.getRulePolicyById(
                        created.getPolicyId()
                );

        assertEquals(
                created.getPolicyId(),
                retrieved.getPolicyId()
        );
    }

    @Test
    void shouldRetrieveRulePolicyByCode() {

        RulePolicyResponse created =
                service.createRulePolicy(
                        buildRequest(
                                "POLICY-002",
                                "ATO Policy",
                                null,
                                "ATO",
                                ORGANIZATION_ID,
                                TENANT_ID,
                                "ACTIVE",
                                null,
                                null,
                                (short) 2
                        )
                );

        RulePolicyResponse retrieved =
                service.getRulePolicyByCode(
                        "POLICY-002"
                );

        assertEquals(
                created.getPolicyId(),
                retrieved.getPolicyId()
        );

        assertEquals(
                "POLICY-002",
                retrieved.getPolicyCode()
        );
    }

    @Test
    void shouldReturnPoliciesByStatusOrderedByPriority() {

        service.createRulePolicy(
                buildRequest(
                        "POLICY-003",
                        "Third Active Policy",
                        null,
                        "TRANSACTION",
                        ORGANIZATION_ID,
                        TENANT_ID,
                        "ACTIVE",
                        null,
                        null,
                        (short) 3
                )
        );

        service.createRulePolicy(
                buildRequest(
                        "POLICY-004",
                        "First Active Policy",
                        null,
                        "ATO",
                        ORGANIZATION_ID,
                        TENANT_ID,
                        "ACTIVE",
                        null,
                        null,
                        (short) 1
                )
        );

        List<RulePolicyResponse> policies =
                service.getRulePoliciesByStatus(
                        "ACTIVE"
                );

        assertEquals(
                2,
                policies.size()
        );

        assertEquals(
                Short.valueOf((short) 1),
                policies.get(0).getPriority()
        );

        assertEquals(
                Short.valueOf((short) 3),
                policies.get(1).getPriority()
        );
    }

    @Test
    void shouldReturnPoliciesByOrganizationOrderedByPriority() {

        service.createRulePolicy(
                buildRequest(
                        "POLICY-005",
                        "Organization Policy Two",
                        null,
                        "TRANSACTION",
                        ORGANIZATION_ID,
                        TENANT_ID,
                        "ACTIVE",
                        null,
                        null,
                        (short) 2
                )
        );

        service.createRulePolicy(
                buildRequest(
                        "POLICY-006",
                        "Organization Policy One",
                        null,
                        "TRANSACTION",
                        ORGANIZATION_ID,
                        TENANT_ID,
                        "ACTIVE",
                        null,
                        null,
                        (short) 1
                )
        );

        List<RulePolicyResponse> policies =
                service.getRulePoliciesByOrganizationId(
                        ORGANIZATION_ID
                );

        assertEquals(
                2,
                policies.size()
        );

        assertEquals(
                Short.valueOf((short) 1),
                policies.get(0).getPriority()
        );

        assertEquals(
                Short.valueOf((short) 2),
                policies.get(1).getPriority()
        );
    }

    @Test
    void shouldReturnPoliciesByTenantOrderedByPriority() {

        service.createRulePolicy(
                buildRequest(
                        "POLICY-007",
                        "Tenant Policy Two",
                        null,
                        "ATO",
                        ORGANIZATION_ID,
                        TENANT_ID,
                        "ACTIVE",
                        null,
                        null,
                        (short) 2
                )
        );

        service.createRulePolicy(
                buildRequest(
                        "POLICY-008",
                        "Tenant Policy One",
                        null,
                        "ATO",
                        ORGANIZATION_ID,
                        TENANT_ID,
                        "ACTIVE",
                        null,
                        null,
                        (short) 1
                )
        );

        List<RulePolicyResponse> policies =
                service.getRulePoliciesByTenantId(
                        TENANT_ID
                );

        assertEquals(
                2,
                policies.size()
        );

        assertEquals(
                Short.valueOf((short) 1),
                policies.get(0).getPriority()
        );

        assertEquals(
                Short.valueOf((short) 2),
                policies.get(1).getPriority()
        );
    }

    @Test
    void shouldReturnPoliciesByTypeOrderedByPriority() {

        service.createRulePolicy(
                buildRequest(
                        "POLICY-009",
                        "Transaction Policy Two",
                        null,
                        "TRANSACTION",
                        ORGANIZATION_ID,
                        TENANT_ID,
                        "ACTIVE",
                        null,
                        null,
                        (short) 2
                )
        );

        service.createRulePolicy(
                buildRequest(
                        "POLICY-010",
                        "Transaction Policy One",
                        null,
                        "TRANSACTION",
                        ORGANIZATION_ID,
                        TENANT_ID,
                        "ACTIVE",
                        null,
                        null,
                        (short) 1
                )
        );

        List<RulePolicyResponse> policies =
                service.getRulePoliciesByType(
                        "TRANSACTION"
                );

        assertEquals(
                2,
                policies.size()
        );

        assertEquals(
                Short.valueOf((short) 1),
                policies.get(0).getPriority()
        );

        assertEquals(
                Short.valueOf((short) 2),
                policies.get(1).getPriority()
        );
    }

    @Test
    void shouldAllowOptionalScopeAndEffectiveDatesToBeNull() {

        RulePolicyResponse created =
                service.createRulePolicy(
                        buildRequest(
                                "POLICY-011",
                                "Global Policy",
                                null,
                                "GLOBAL",
                                null,
                                null,
                                "ACTIVE",
                                null,
                                null,
                                (short) 1
                        )
                );

        assertNotNull(
                created.getPolicyId()
        );

        assertNull(
                created.getOrganizationId()
        );

        assertNull(
                created.getTenantId()
        );

        assertNull(
                created.getEffectiveFrom()
        );

        assertNull(
                created.getEffectiveTo()
        );
    }

    @Test
    void shouldRejectDuplicatePolicyCode() {

        service.createRulePolicy(
                buildRequest(
                        "POLICY-DUPLICATE",
                        "Original Policy",
                        null,
                        "TRANSACTION",
                        ORGANIZATION_ID,
                        TENANT_ID,
                        "ACTIVE",
                        null,
                        null,
                        (short) 1
                )
        );

        assertThrows(
                DataIntegrityViolationException.class,
                () -> service.createRulePolicy(
                        buildRequest(
                                "POLICY-DUPLICATE",
                                "Duplicate Policy",
                                null,
                                "ATO",
                                ORGANIZATION_ID,
                                TENANT_ID,
                                "ACTIVE",
                                null,
                                null,
                                (short) 2
                        )
                )
        );
    }

    @Test
    void shouldRejectUnknownPolicyId() {

        UUID unknownPolicyId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getRulePolicyById(
                        unknownPolicyId
                )
        );
    }

    @Test
    void shouldRejectUnknownPolicyCode() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getRulePolicyByCode(
                        "UNKNOWN-POLICY"
                )
        );
    }

    private RulePolicyRequest buildRequest(
            String policyCode,
            String policyName,
            String description,
            String policyType,
            UUID organizationId,
            UUID tenantId,
            String status,
            LocalDateTime effectiveFrom,
            LocalDateTime effectiveTo,
            short priority) {

        RulePolicyRequest request =
                new RulePolicyRequest();

        request.setPolicyCode(
                policyCode
        );

        request.setPolicyName(
                policyName
        );

        request.setDescription(
                description
        );

        request.setPolicyType(
                policyType
        );

        request.setOrganizationId(
                organizationId
        );

        request.setTenantId(
                tenantId
        );

        request.setStatus(
                status
        );

        request.setEffectiveFrom(
                effectiveFrom
        );

        request.setEffectiveTo(
                effectiveTo
        );

        request.setPriority(
                priority
        );

        return request;
    }
}