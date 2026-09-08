package com.efs.modules.rules.service;

import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.rules.dto.RuleExecutionRequest;
import com.efs.modules.rules.dto.RuleExecutionResponse;
import com.efs.modules.rules.entity.RuleExecution;
import com.efs.modules.rules.mapper.RuleExecutionMapper;
import com.efs.modules.rules.repository.RuleExecutionRepository;
import com.efs.modules.rules.repository.RuleRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.security.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class RuleExecutionService
        implements RuleExecutionServiceInterface {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    RuleExecutionService.class
            );

    private static final String HISTORY_EVENT_TYPE =
            "RULE_EXECUTION_HISTORY_VIEW";

    private static final String ENTITY_TYPE_RULE =
            "RULE";

    private static final String ACTION_VIEW =
            "VIEW";

    private static final String SOURCE_COMPONENT =
            "RULE_ENGINE";

    private final RuleExecutionRepository ruleExecutionRepository;
    private final RuleExecutionMapper ruleExecutionMapper;
    private final RuleRepository ruleRepository;
    private final AuditEventServiceInterface auditEventService;

    public RuleExecutionService(
            RuleExecutionRepository ruleExecutionRepository,
            RuleExecutionMapper ruleExecutionMapper,
            RuleRepository ruleRepository,
            AuditEventServiceInterface auditEventService) {

        this.ruleExecutionRepository =
                ruleExecutionRepository;

        this.ruleExecutionMapper =
                ruleExecutionMapper;

        this.ruleRepository =
                ruleRepository;

        this.auditEventService =
                auditEventService;
    }

    @Override
    @Transactional
    public RuleExecutionResponse createRuleExecution(
            RuleExecutionRequest request) {

        RuleExecution execution =
                ruleExecutionMapper.toEntity(request);

        execution.setExecutedAt(
                LocalDateTime.now()
        );

        RuleExecution savedExecution =
                ruleExecutionRepository.save(execution);

        return ruleExecutionMapper.toResponse(savedExecution);
    }

    @Override
    @Transactional(readOnly = true)
    public RuleExecutionResponse getRuleExecutionById(
            UUID executionId) {

        RuleExecution execution =
                ruleExecutionRepository
                        .findByExecutionId(executionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Rule execution not found: "
                                                + executionId
                                )
                        );

        return ruleExecutionMapper.toResponse(execution);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleExecutionResponse> getRuleExecutionsByRuleId(
            UUID ruleId) {

        return ruleExecutionRepository
                .findByRuleIdOrderByExecutedAtDesc(ruleId)
                .stream()
                .map(ruleExecutionMapper::toResponse)
                .toList();
    }

    @Override
    public List<RuleExecutionResponse> getRuleExecutionsByRuleId(
            UUID ruleId,
            SecurityContext securityContext) {

        Objects.requireNonNull(
                ruleId,
                "ruleId is required"
        );

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        if (!securityContext.hasPermission(
                RULE_EXECUTION_VIEW_PERMISSION
        )) {

            recordHistoryAudit(
                    ruleId,
                    securityContext,
                    "REJECTED",
                    "MISSING_PERMISSION",
                    null,
                    null
            );

            throw new AccessDeniedException(
                    "Missing required permission: "
                            + RULE_EXECUTION_VIEW_PERMISSION
            );
        }

        boolean ruleExists;

        try {
            ruleExists =
                    ruleRepository.existsById(ruleId);
        }
        catch (RuntimeException exception) {

            LOGGER.error(
                    "UC-027 rule lookup failed for ruleId={} userId={}",
                    ruleId,
                    securityContext.getUserId(),
                    exception
            );

            recordHistoryAudit(
                    ruleId,
                    securityContext,
                    "FAILURE",
                    "RULE_LOOKUP_FAILED",
                    null,
                    exception
            );

            throw exception;
        }

        if (!ruleExists) {

            recordHistoryAudit(
                    ruleId,
                    securityContext,
                    "REJECTED",
                    "RULE_NOT_FOUND",
                    null,
                    null
            );

            throw new ResourceNotFoundException(
                    "Rule not found: " + ruleId
            );
        }

        List<RuleExecutionResponse> executions;

        try {
            executions =
                    getRuleExecutionsByRuleId(ruleId);
        }
        catch (RuntimeException exception) {

            LOGGER.error(
                    "UC-027 rule execution history retrieval failed for ruleId={} userId={}",
                    ruleId,
                    securityContext.getUserId(),
                    exception
            );

            recordHistoryAudit(
                    ruleId,
                    securityContext,
                    "FAILURE",
                    "HISTORY_RETRIEVAL_FAILED",
                    null,
                    exception
            );

            throw exception;
        }

        recordHistoryAudit(
                ruleId,
                securityContext,
                "SUCCESS",
                null,
                executions.size(),
                null
        );

        return executions;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleExecutionResponse> getRuleExecutionsByRuleVersionId(
            UUID ruleVersionId) {

        return ruleExecutionRepository
                .findByRuleVersionIdOrderByExecutedAtDesc(ruleVersionId)
                .stream()
                .map(ruleExecutionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleExecutionResponse> getRuleExecutionsByPolicyId(
            UUID policyId) {

        return ruleExecutionRepository
                .findByPolicyIdOrderByExecutedAtDesc(policyId)
                .stream()
                .map(ruleExecutionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleExecutionResponse> getRuleExecutionsByTransactionId(
            UUID transactionId) {

        return ruleExecutionRepository
                .findByTransactionIdOrderByExecutedAtDesc(transactionId)
                .stream()
                .map(ruleExecutionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleExecutionResponse> getRuleExecutionsByStatus(
            String executionStatus) {

        return ruleExecutionRepository
                .findByExecutionStatusOrderByExecutedAtDesc(
                        executionStatus
                )
                .stream()
                .map(ruleExecutionMapper::toResponse)
                .toList();
    }

    private void recordHistoryAudit(
            UUID ruleId,
            SecurityContext securityContext,
            String eventResult,
            String reason,
            Integer resultCount,
            RuntimeException exception) {

        AuditEventRequest request =
                new AuditEventRequest();

        request.setTenantId(
                securityContext.getTenantId()
        );

        request.setUserId(
                securityContext.getUserId()
        );

        request.setSessionId(
                securityContext.getSessionId()
        );

        request.setEventType(
                HISTORY_EVENT_TYPE
        );

        request.setEntityType(
                ENTITY_TYPE_RULE
        );

        request.setEntityId(
                ruleId
        );

        request.setAction(
                ACTION_VIEW
        );

        request.setSourceComponent(
                SOURCE_COMPONENT
        );

        request.setEventResult(
                eventResult
        );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "ruleId",
                ruleId.toString()
        );

        details.put(
                "permissionCode",
                RULE_EXECUTION_VIEW_PERMISSION
        );

        if (reason != null) {
            details.put(
                    "reason",
                    reason
            );
        }

        if (resultCount != null) {
            details.put(
                    "resultCount",
                    resultCount
            );
        }

        if (exception != null) {

            details.put(
                    "errorType",
                    exception.getClass()
                            .getSimpleName()
            );

            details.put(
                    "errorMessage",
                    exception.getMessage() == null
                            ? "Rule execution history retrieval failed"
                            : exception.getMessage()
            );
        }

        request.setEventDetails(
                details
        );

        auditEventService.createAuditEvent(
                request
        );
    }
}