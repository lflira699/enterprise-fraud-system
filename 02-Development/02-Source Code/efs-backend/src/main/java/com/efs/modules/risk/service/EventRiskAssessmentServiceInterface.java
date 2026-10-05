package com.efs.modules.risk.service;

import com.efs.modules.risk.dto.EventRiskAssessmentRequest;
import com.efs.modules.risk.dto.EventRiskAssessmentResponse;
import com.efs.shared.security.SecurityContext;

import java.util.List;
import java.util.UUID;

public interface EventRiskAssessmentServiceInterface {

    EventRiskAssessmentResponse assess(
            EventRiskAssessmentRequest request
    );

    EventRiskAssessmentResponse getEventRiskAssessmentById(
            UUID eventRiskAssessmentId,
            SecurityContext securityContext
    );

    List<EventRiskAssessmentResponse>
    getEventRiskAssessmentsByFraudEventId(
            UUID fraudEventId,
            SecurityContext securityContext
    );
}