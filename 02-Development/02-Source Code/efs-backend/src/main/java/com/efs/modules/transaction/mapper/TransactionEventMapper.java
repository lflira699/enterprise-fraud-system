package com.efs.modules.transaction.mapper;

import com.efs.modules.transaction.dto.TransactionEventRequest;
import com.efs.modules.transaction.dto.TransactionEventResponse;
import com.efs.modules.transaction.entity.TransactionEvent;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventMapper {

    public TransactionEvent toEntity(
            TransactionEventRequest request) {

        TransactionEvent event =
                new TransactionEvent();

        event.setEventType(
                request.getEventType()
        );

        event.setEventTimestamp(
                request.getEventTimestamp()
        );

        event.setComponentName(
                request.getComponentName()
        );

        event.setEventResult(
                request.getEventResult()
        );

        event.setSeverity(
                request.getSeverity()
        );

        event.setCorrelationId(
                request.getCorrelationId()
        );

        event.setRequestId(
                request.getRequestId()
        );

        event.setEventMessage(
                request.getEventMessage()
        );

        event.setExecutionTimeMs(
                request.getExecutionTimeMs()
        );

        return event;
    }

    public TransactionEventResponse toResponse(
            TransactionEvent event) {

        TransactionEventResponse response =
                new TransactionEventResponse();

        response.setEventId(
                event.getEventId()
        );

        response.setTransactionId(
                event.getTransactionId()
        );

        response.setEventType(
                event.getEventType()
        );

        response.setEventTimestamp(
                event.getEventTimestamp()
        );

        response.setComponentName(
                event.getComponentName()
        );

        response.setEventResult(
                event.getEventResult()
        );

        response.setSeverity(
                event.getSeverity()
        );

        response.setCorrelationId(
                event.getCorrelationId()
        );

        response.setRequestId(
                event.getRequestId()
        );

        response.setEventMessage(
                event.getEventMessage()
        );

        response.setExecutionTimeMs(
                event.getExecutionTimeMs()
        );

        return response;
    }
}