package com.efs.modules.detection.mapper;

import com.efs.modules.detection.dto.NetworkAnalysisRequest;
import com.efs.modules.detection.dto.NetworkAnalysisResponse;
import com.efs.modules.detection.entity.NetworkAnalysis;
import org.springframework.stereotype.Component;

@Component
public class NetworkAnalysisMapper {

    public NetworkAnalysis toEntity(
            NetworkAnalysisRequest request) {

        NetworkAnalysis analysis =
                new NetworkAnalysis();

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

        analysis.setNetworkType(
                request.getNetworkType()
        );

        analysis.setNetworkKey(
                request.getNetworkKey()
        );

        analysis.setNetworkIndicators(
                request.getNetworkIndicators()
        );

        analysis.setAnalysisContext(
                request.getAnalysisContext()
        );

        return analysis;
    }

    public NetworkAnalysisResponse toResponse(
            NetworkAnalysis analysis) {

        NetworkAnalysisResponse response =
                new NetworkAnalysisResponse();

        response.setNetworkAnalysisId(
                analysis.getNetworkAnalysisId()
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

        response.setNetworkType(
                analysis.getNetworkType()
        );

        response.setNetworkKey(
                analysis.getNetworkKey()
        );

        response.setEntityCount(
                analysis.getEntityCount()
        );

        response.setRelationshipCount(
                analysis.getRelationshipCount()
        );

        response.setNetworkConfidence(
                analysis.getNetworkConfidence()
        );

        response.setNetworkIndicators(
                analysis.getNetworkIndicators()
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