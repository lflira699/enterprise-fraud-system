package com.efs.modules.detection.mapper;

import com.efs.modules.detection.dto.BehavioralAnalysisRequest;
import com.efs.modules.detection.dto.BehavioralAnalysisResponse;
import com.efs.modules.detection.entity.BehavioralAnalysis;
import org.springframework.stereotype.Component;

@Component
public class BehavioralAnalysisMapper {

    public BehavioralAnalysis toEntity(
            BehavioralAnalysisRequest request) {

        BehavioralAnalysis analysis =
                new BehavioralAnalysis();

        analysis.setCustomerId(
                request.getCustomerId()
        );

        analysis.setTransactionId(
                request.getTransactionId()
        );

        analysis.setCorrelationId(
                request.getCorrelationId()
        );

        analysis.setAnalysisStatus(
                request.getAnalysisStatus()
        );

        analysis.setBaselineWindowDays(
                request.getBaselineWindowDays()
        );

        analysis.setObservedWindowStart(
                request.getObservedWindowStart()
        );

        analysis.setObservedWindowEnd(
                request.getObservedWindowEnd()
        );

        analysis.setBehavioralIndicators(
                request.getBehavioralIndicators()
        );

        analysis.setAnalysisContext(
                request.getAnalysisContext()
        );

        return analysis;
    }

    public BehavioralAnalysisResponse toResponse(
            BehavioralAnalysis analysis) {

        BehavioralAnalysisResponse response =
                new BehavioralAnalysisResponse();

        response.setBehavioralAnalysisId(
                analysis.getBehavioralAnalysisId()
        );

        response.setCustomerId(
                analysis.getCustomerId()
        );

        response.setTransactionId(
                analysis.getTransactionId()
        );

        response.setCorrelationId(
                analysis.getCorrelationId()
        );

        response.setAnalysisStatus(
                analysis.getAnalysisStatus()
        );

        response.setBaselineWindowDays(
                analysis.getBaselineWindowDays()
        );

        response.setObservedWindowStart(
                analysis.getObservedWindowStart()
        );

        response.setObservedWindowEnd(
                analysis.getObservedWindowEnd()
        );

        response.setAmountDeviation(
                analysis.getAmountDeviation()
        );

        response.setFrequencyDeviation(
                analysis.getFrequencyDeviation()
        );

        response.setVelocityDeviation(
                analysis.getVelocityDeviation()
        );

        response.setChannelDeviation(
                analysis.getChannelDeviation()
        );

        response.setGeographicDeviation(
                analysis.getGeographicDeviation()
        );

        response.setTemporalDeviation(
                analysis.getTemporalDeviation()
        );

        response.setBehavioralConfidence(
                analysis.getBehavioralConfidence()
        );

        response.setBehavioralIndicators(
                analysis.getBehavioralIndicators()
        );

        response.setAnalysisContext(
                analysis.getAnalysisContext()
        );

        response.setAnalyzedAt(
                analysis.getAnalyzedAt()
        );

        response.setCreatedAt(
                analysis.getCreatedAt()
        );

        return response;
    }
}