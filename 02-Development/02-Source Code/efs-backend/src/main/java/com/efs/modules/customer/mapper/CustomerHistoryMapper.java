package com.efs.modules.customer.mapper;

import com.efs.modules.customer.dto.CustomerHistoryRequest;
import com.efs.modules.customer.dto.CustomerHistoryResponse;
import com.efs.modules.customer.entity.CustomerHistory;
import org.springframework.stereotype.Component;

@Component
public class CustomerHistoryMapper {

    public CustomerHistory toEntity(
            CustomerHistoryRequest request) {

        CustomerHistory history = new CustomerHistory();

        history.setEventType(request.getEventType());
        history.setEventDescription(request.getEventDescription());
        history.setPreviousStatus(request.getPreviousStatus());
        history.setNewStatus(request.getNewStatus());
        history.setPreviousRiskLevel(request.getPreviousRiskLevel());
        history.setNewRiskLevel(request.getNewRiskLevel());
        history.setPreviousRiskScore(request.getPreviousRiskScore());
        history.setNewRiskScore(request.getNewRiskScore());
        history.setEventTimestamp(request.getEventTimestamp());
        history.setSourceReference(request.getSourceReference());
        history.setCreatedBy(request.getCreatedBy());

        return history;
    }

    public CustomerHistoryResponse toResponse(
            CustomerHistory history) {

        CustomerHistoryResponse response =
                new CustomerHistoryResponse();

        response.setCustomerHistoryId(
                history.getCustomerHistoryId()
        );
        response.setCustomerId(history.getCustomerId());
        response.setEventType(history.getEventType());
        response.setEventDescription(
                history.getEventDescription()
        );
        response.setPreviousStatus(
                history.getPreviousStatus()
        );
        response.setNewStatus(history.getNewStatus());
        response.setPreviousRiskLevel(
                history.getPreviousRiskLevel()
        );
        response.setNewRiskLevel(
                history.getNewRiskLevel()
        );
        response.setPreviousRiskScore(
                history.getPreviousRiskScore()
        );
        response.setNewRiskScore(
                history.getNewRiskScore()
        );
        response.setEventTimestamp(
                history.getEventTimestamp()
        );
        response.setSourceReference(
                history.getSourceReference()
        );
        response.setCreatedAt(history.getCreatedAt());
        response.setCreatedBy(history.getCreatedBy());

        return response;
    }
}