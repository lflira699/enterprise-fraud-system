package com.efs.modules.event.service;

import com.efs.modules.event.dto.FraudEventRequest;
import com.efs.modules.event.dto.FraudEventResponse;
import com.efs.shared.pagination.PageResponse;
import com.efs.shared.security.SecurityContext;

import java.time.LocalDateTime;
import java.util.UUID;

public interface FraudEventServiceInterface {

    FraudEventResponse registerEvent(
            FraudEventRequest request,
            SecurityContext securityContext);

    FraudEventResponse getEvent(
            UUID fraudEventId,
            SecurityContext securityContext);
    PageResponse<FraudEventResponse> searchEvents(
            UUID transactionId,
            String eventType,
            String sourceType,
            String sourceReference,
            UUID correlationId,
            LocalDateTime occurredFrom,
            LocalDateTime occurredTo,
            LocalDateTime receivedFrom,
            LocalDateTime receivedTo,
            int page,
            int size,
            String sort,
            String direction,
            SecurityContext securityContext);
}
