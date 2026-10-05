package com.efs.modules.risk.service;

import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import com.efs.modules.risk.entity.EventRiskAssessment;
import com.efs.shared.security.SecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class EventRiskAssessmentAuditService {

    private static final String EVENT_TYPE =
            "EVENT_RISK_ASSESSED";

    private static final String ENTITY_TYPE =
            "RISK_ASSESSMENT";

    private static final String ACTION =
            "CALCULATE";

    private static final String SOURCE_COMPONENT =
            "RISK_ENGINE";

    private static final String REVIEW_EVENT_TYPE =
            "RISK_SCORE_REVIEWED";

    private static final String REVIEW_ACTION =
            "VIEW";

    private final AuditEventServiceInterface auditEventService;

    public EventRiskAssessmentAuditService(
            AuditEventServiceInterface auditEventService) {

        this.auditEventService =
                auditEventService;
    }

    public void recordSuccess(
            EventRiskAssessment assessment,
            SecurityContext securityContext,
            boolean reused) {

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "fraudEventId",
                assessment.getFraudEventId().toString()
        );

        details.put(
                "modelId",
                assessment.getModelId()
        );

        details.put(
                "modelVersion",
                assessment.getModelVersion()
        );

        details.put(
                "overallRiskScore",
                assessment.getOverallRiskScore()
        );

        details.put(
                "riskLevel",
                assessment.getRiskLevel()
        );

        details.put(
                "reused",
                reused
        );

        AuditEventRequest request =
                buildRequest(
                        assessment.getOrganizationId(),
                        assessment.getTenantId(),
                        securityContext,
                        assessment.getEventRiskAssessmentId(),
                        assessment.getCorrelationId(),
                        "SUCCESS",
                        details
                );

        auditEventService.createAuditEvent(
                request
        );
    }

    @Transactional(
            propagation = Propagation.REQUIRES_NEW
    )
    public void recordRejected(
            UUID organizationId,
            SecurityContext securityContext,
            UUID fraudEventId,
            UUID correlationId,
            String modelId,
            String modelVersion,
            String reason,
            RuntimeException exception) {

        recordNonSuccess(
                organizationId,
                securityContext,
                fraudEventId,
                correlationId,
                modelId,
                modelVersion,
                "REJECTED",
                reason,
                exception
        );
    }

    @Transactional(
            propagation = Propagation.REQUIRES_NEW
    )
    public void recordFailure(
            UUID organizationId,
            SecurityContext securityContext,
            UUID fraudEventId,
            UUID correlationId,
            String modelId,
            String modelVersion,
            String reason,
            RuntimeException exception) {

        recordNonSuccess(
                organizationId,
                securityContext,
                fraudEventId,
                correlationId,
                modelId,
                modelVersion,
                "FAILURE",
                reason,
                exception
        );
    }

    public void recordReview(
            UUID organizationId,
            SecurityContext securityContext,
            UUID entityId,
            String eventResult,
            String reason,
            Integer resultCount,
            Map<String, Object> criteria,
            RuntimeException exception) {

        AuditEventRequest request =
                new AuditEventRequest();

        request.setOrganizationId(
                organizationId
        );

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
                REVIEW_EVENT_TYPE
        );

        request.setEntityType(
                ENTITY_TYPE
        );

        request.setEntityId(
                entityId
        );

        request.setAction(
                REVIEW_ACTION
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
                "permissionCode",
                RiskAssessmentServiceInterface
                        .RISK_ASSESSMENT_VIEW_PERMISSION
        );

        if (criteria != null) {

            details.putAll(
                    criteria
            );
        }

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
                            ? "Risk assessment retrieval failed"
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

    private void recordNonSuccess(
            UUID organizationId,
            SecurityContext securityContext,
            UUID fraudEventId,
            UUID correlationId,
            String modelId,
            String modelVersion,
            String eventResult,
            String reason,
            RuntimeException exception) {

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "fraudEventId",
                fraudEventId == null
                        ? null
                        : fraudEventId.toString()
        );

        details.put(
                "reason",
                reason
        );

        details.put(
                "reused",
                false
        );

        if (modelId != null) {

            details.put(
                    "modelId",
                    modelId
            );
        }

        if (modelVersion != null) {

            details.put(
                    "modelVersion",
                    modelVersion
            );
        }

        if (exception != null) {

            details.put(
                    "exceptionType",
                    exception.getClass().getSimpleName()
            );

            details.put(
                    "exceptionMessage",
                    exception.getMessage()
            );
        }

        AuditEventRequest request =
                buildRequest(
                        organizationId,
                        securityContext == null
                                ? null
                                : securityContext.getTenantId(),
                        securityContext,
                        null,
                        correlationId,
                        eventResult,
                        details
                );

        auditEventService.createAuditEvent(
                request
        );
    }

    private AuditEventRequest buildRequest(
            UUID organizationId,
            UUID tenantId,
            SecurityContext securityContext,
            UUID entityId,
            UUID correlationId,
            String eventResult,
            Map<String, Object> details) {

        AuditEventRequest request =
                new AuditEventRequest();

        request.setOrganizationId(
                organizationId
        );

        request.setTenantId(
                tenantId
        );

        if (securityContext != null) {

            request.setUserId(
                    securityContext.getUserId()
            );

            request.setSessionId(
                    securityContext.getSessionId()
            );
        }

        request.setEventType(
                EVENT_TYPE
        );

        request.setEntityType(
                ENTITY_TYPE
        );

        request.setEntityId(
                entityId
        );

        request.setAction(
                ACTION
        );

        request.setSourceComponent(
                SOURCE_COMPONENT
        );

        request.setCorrelationId(
                correlationId
        );

        request.setEventResult(
                eventResult
        );

        request.setEventDetails(
                details
        );

        return request;
    }
}