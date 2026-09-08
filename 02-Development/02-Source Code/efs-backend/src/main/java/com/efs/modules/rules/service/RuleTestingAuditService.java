package com.efs.modules.rules.service;

import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class RuleTestingAuditService {

    private final AuditEventServiceInterface auditEventService;

    public RuleTestingAuditService(
            AuditEventServiceInterface auditEventService) {

        this.auditEventService =
                auditEventService;
    }

    @Transactional(
            propagation = Propagation.REQUIRES_NEW
    )
    public void recordRejected(
            UUID ruleId,
            UUID ruleVersionId,
            UUID executedBy,
            UUID correlationId,
            String reason) {

        AuditEventRequest request =
                baseRequest(
                        ruleId,
                        executedBy,
                        correlationId
                );

        request.setEventType(
                "RULE_TEST_REJECTED"
        );

        request.setEventResult(
                "REJECTED"
        );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "ruleId",
                ruleId.toString()
        );

        details.put(
                "ruleVersionId",
                ruleVersionId.toString()
        );

        details.put(
                "reason",
                reason
        );

        request.setEventDetails(
                details
        );

        auditEventService.createAuditEvent(
                request
        );
    }

    @Transactional(
            propagation = Propagation.REQUIRES_NEW
    )
    public void recordFailure(
            UUID ruleId,
            UUID ruleVersionId,
            String datasetReference,
            UUID executedBy,
            UUID correlationId,
            RuntimeException exception) {

        AuditEventRequest request =
                baseRequest(
                        ruleId,
                        executedBy,
                        correlationId
                );

        request.setEventType(
                "RULE_TEST_FAILED"
        );

        request.setEventResult(
                "FAILURE"
        );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "ruleId",
                ruleId.toString()
        );

        details.put(
                "ruleVersionId",
                ruleVersionId.toString()
        );

        details.put(
                "datasetReference",
                datasetReference
        );

        details.put(
                "errorType",
                exception.getClass()
                        .getSimpleName()
        );

        details.put(
                "errorMessage",
                exception.getMessage() == null
                        ? "Rule test execution failed"
                        : exception.getMessage()
        );

        request.setEventDetails(
                details
        );

        auditEventService.createAuditEvent(
                request
        );
    }

    private AuditEventRequest baseRequest(
            UUID ruleId,
            UUID executedBy,
            UUID correlationId) {

        AuditEventRequest request =
                new AuditEventRequest();

        request.setUserId(
                executedBy
        );

        request.setEntityType(
                "RULE"
        );

        request.setEntityId(
                ruleId
        );

        request.setAction(
                "TEST"
        );

        request.setSourceComponent(
                "RULE_ENGINE"
        );

        request.setCorrelationId(
                correlationId
        );

        return request;
    }
}