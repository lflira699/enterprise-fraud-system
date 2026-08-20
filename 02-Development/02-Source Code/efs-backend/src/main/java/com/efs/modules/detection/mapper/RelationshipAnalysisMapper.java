package com.efs.modules.detection.mapper;

import com.efs.modules.detection.dto.RelationshipAnalysisRequest;
import com.efs.modules.detection.dto.RelationshipAnalysisResponse;
import com.efs.modules.detection.entity.RelationshipAnalysis;
import org.springframework.stereotype.Component;

@Component
public class RelationshipAnalysisMapper {

    public RelationshipAnalysis toEntity(
            RelationshipAnalysisRequest request) {

        RelationshipAnalysis analysis =
                new RelationshipAnalysis();

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

        analysis.setRelationshipType(
                request.getRelationshipType()
        );

        analysis.setSourceEntityType(
                request.getSourceEntityType()
        );

        analysis.setSourceEntityKey(
                request.getSourceEntityKey()
        );

        analysis.setTargetEntityType(
                request.getTargetEntityType()
        );

        analysis.setTargetEntityKey(
                request.getTargetEntityKey()
        );

        analysis.setRelationshipIndicators(
                request.getRelationshipIndicators()
        );

        analysis.setAnalysisContext(
                request.getAnalysisContext()
        );

        return analysis;
    }

    public RelationshipAnalysisResponse toResponse(
            RelationshipAnalysis analysis) {

        RelationshipAnalysisResponse response =
                new RelationshipAnalysisResponse();

        response.setRelationshipAnalysisId(
                analysis.getRelationshipAnalysisId()
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

        response.setRelationshipType(
                analysis.getRelationshipType()
        );

        response.setSourceEntityType(
                analysis.getSourceEntityType()
        );

        response.setSourceEntityKey(
                analysis.getSourceEntityKey()
        );

        response.setTargetEntityType(
                analysis.getTargetEntityType()
        );

        response.setTargetEntityKey(
                analysis.getTargetEntityKey()
        );

        response.setRelationshipStrength(
                analysis.getRelationshipStrength()
        );

        response.setEntityCount(
                analysis.getEntityCount()
        );

        response.setRelationshipCount(
                analysis.getRelationshipCount()
        );

        response.setRelationshipIndicators(
                analysis.getRelationshipIndicators()
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