package com.efs.modules.risk.service;

import com.efs.modules.audit.dto.AuditEventRequest;
import com.efs.modules.audit.service.AuditEventServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CustomerRiskAssessmentAuditService {

    private static final String EVENT_TYPE =
            "CUSTOMER_RISK_ASSESSED";

    private static final String ENTITY_TYPE =
            "CUSTOMER_RISK_PROFILE";

    private static final String ACTION =
            "CALCULATE";

    private static final String SOURCE_COMPONENT =
            "RISK_ENGINE";

    private final AuditEventServiceInterface auditEventService;

    public CustomerRiskAssessmentAuditService(
            AuditEventServiceInterface auditEventService) {

        this.auditEventService =
                auditEventService;
    }

    @Transactional(
            propagation = Propagation.REQUIRES_NEW
    )
    public void recordRejected(
            UUID customerId,
            UUID correlationId,
            String reason,
            RuntimeException exception) {

        record(
                customerId,
                correlationId,
                "REJECTED",
                reason,
                exception
        );
    }

    @Transactional(
            propagation = Propagation.REQUIRES_NEW
    )
    public void recordFailure(
            UUID customerId,
            UUID correlationId,
            String reason,
            RuntimeException exception) {

        record(
                customerId,
                correlationId,
                "FAILURE",
                reason,
                exception
        );
    }

    private void record(
            UUID customerId,
            UUID correlationId,
            String eventResult,
            String reason,
            RuntimeException exception) {

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "customerId",
                customerId.toString()
        );

        details.put(
                "reason",
                reason
        );

        details.put(
                "reused",
                false
        );

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
                new AuditEventRequest();

        request.setEventType(
                EVENT_TYPE
        );

        request.setEntityType(
                ENTITY_TYPE
        );

        request.setEntityId(null);

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

        auditEventService.createAuditEvent(
                request
        );
    }
}