package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.RelationshipAnalysisRequest;
import com.efs.modules.detection.dto.RelationshipAnalysisResponse;

import java.util.List;
import java.util.UUID;

public interface RelationshipAnalysisServiceInterface {

    RelationshipAnalysisResponse createRelationshipAnalysis(
            RelationshipAnalysisRequest request
    );

    RelationshipAnalysisResponse getRelationshipAnalysisById(
            UUID relationshipAnalysisId
    );

    List<RelationshipAnalysisResponse> getAnalysesByCustomer(
            UUID customerId
    );

    List<RelationshipAnalysisResponse> getAnalysesByTransaction(
            UUID transactionId
    );

    List<RelationshipAnalysisResponse> getAnalysesByCorrelation(
            UUID correlationId
    );

    List<RelationshipAnalysisResponse> getAnalysesByType(
            String relationshipType
    );

    List<RelationshipAnalysisResponse> getAnalysesBySource(
            String sourceEntityKey
    );

    List<RelationshipAnalysisResponse> getAnalysesByTarget(
            String targetEntityKey
    );

    List<RelationshipAnalysisResponse> getAnalysesByStatus(
            String analysisStatus
    );
}