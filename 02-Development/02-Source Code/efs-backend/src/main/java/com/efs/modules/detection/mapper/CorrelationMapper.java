package com.efs.modules.detection.mapper;

import com.efs.modules.detection.dto.CorrelationRequest;
import com.efs.modules.detection.dto.CorrelationResponse;
import com.efs.modules.detection.entity.Correlation;
import org.springframework.stereotype.Component;

@Component
public class CorrelationMapper {

    public Correlation toEntity(
            CorrelationRequest request) {

        Correlation correlation =
                new Correlation();

        correlation.setCustomerId(
                request.getCustomerId()
        );

        correlation.setTransactionId(
                request.getTransactionId()
        );

        correlation.setCorrelationKey(
                request.getCorrelationKey()
        );

        correlation.setCorrelationType(
                request.getCorrelationType()
        );

        correlation.setCorrelationStatus(
                request.getCorrelationStatus()
        );

        correlation.setWindowStart(
                request.getWindowStart()
        );

        correlation.setWindowEnd(
                request.getWindowEnd()
        );

        correlation.setEventCount(
                request.getEventCount()
        );

        correlation.setMatchedRuleCount(
                request.getMatchedRuleCount()
        );

        correlation.setCorrelationContext(
                request.getCorrelationContext()
        );

        return correlation;
    }

    public CorrelationResponse toResponse(
            Correlation correlation) {

        CorrelationResponse response =
                new CorrelationResponse();

        response.setCorrelationId(
                correlation.getCorrelationId()
        );

        response.setCustomerId(
                correlation.getCustomerId()
        );

        response.setTransactionId(
                correlation.getTransactionId()
        );

        response.setCorrelationKey(
                correlation.getCorrelationKey()
        );

        response.setCorrelationType(
                correlation.getCorrelationType()
        );

        response.setCorrelationStatus(
                correlation.getCorrelationStatus()
        );

        response.setWindowStart(
                correlation.getWindowStart()
        );

        response.setWindowEnd(
                correlation.getWindowEnd()
        );

        response.setEventCount(
                correlation.getEventCount()
        );

        response.setMatchedRuleCount(
                correlation.getMatchedRuleCount()
        );

        response.setConfidence(
                correlation.getConfidence()
        );

        response.setCorrelationContext(
                correlation.getCorrelationContext()
        );

        response.setCreatedAt(
                correlation.getCreatedAt()
        );

        response.setUpdatedAt(
                correlation.getUpdatedAt()
        );

        return response;
    }
}