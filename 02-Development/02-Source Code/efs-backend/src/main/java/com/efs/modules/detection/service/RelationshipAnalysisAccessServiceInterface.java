package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.RelationshipAnalysisRequest;
import com.efs.modules.detection.dto.RelationshipAnalysisResponse;
import com.efs.shared.security.SecurityContext;

import java.util.List;
import java.util.UUID;

public interface RelationshipAnalysisAccessServiceInterface {

    RelationshipAnalysisResponse createRelationshipAnalysis(
            RelationshipAnalysisRequest request,
            SecurityContext securityContext
    );

    RelationshipAnalysisResponse getRelationshipAnalysisById(
            UUID relationshipAnalysisId,
            SecurityContext securityContext
    );

    List<RelationshipAnalysisResponse> getAnalysesByCustomer(
            UUID customerId,
            SecurityContext securityContext
    );

    List<RelationshipAnalysisResponse> getAnalysesByTransaction(
            UUID transactionId,
            SecurityContext securityContext
    );

    List<RelationshipAnalysisResponse> getAnalysesByCorrelation(
            UUID correlationId,
            SecurityContext securityContext
    );

    List<RelationshipAnalysisResponse> getAnalysesByType(
            String relationshipType,
            SecurityContext securityContext
    );

    List<RelationshipAnalysisResponse> getAnalysesBySource(
            String sourceEntityKey,
            SecurityContext securityContext
    );

    List<RelationshipAnalysisResponse> getAnalysesByTarget(
            String targetEntityKey,
            SecurityContext securityContext
    );

    List<RelationshipAnalysisResponse> getAnalysesByStatus(
            String analysisStatus,
            SecurityContext securityContext
    );
}