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
public class TransactionRiskAssessmentAuditService {

    private static final String EVENT_TYPE =
            "TRANSACTION_RISK_ASSESSED";

    private static final String ENTITY_TYPE =
            "RISK_ASSESSMENT";

    private static final String ACTION =
            "CALCULATE";

    private static final String SOURCE_COMPONENT =
            "RISK_ENGINE";

    private final AuditEventServiceInterface auditEventService;

    public TransactionRiskAssessmentAuditService(
            AuditEventServiceInterface auditEventService) {

        this.auditEventService =
                auditEventService;
    }

    @Transactional(
            propagation = Propagation.REQUIRES_NEW
    )
    public void recordRejected(
            UUID transactionId,
            UUID correlationId,
            String reason,
            RuntimeException exception) {

        record(
                transactionId,
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
            UUID transactionId,
            UUID correlationId,
            String reason,
            RuntimeException exception) {

        record(
                transactionId,
                correlationId,
                "FAILURE",
                reason,
                exception
        );
    }

    private void record(
            UUID transactionId,
            UUID correlationId,
            String eventResult,
            String reason,
            RuntimeException exception) {

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "transactionId",
                transactionId == null
                        ? null
                        : transactionId.toString()
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