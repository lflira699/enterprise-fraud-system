package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.RelationshipAnalysisRequest;
import com.efs.modules.detection.dto.RelationshipAnalysisResponse;
import com.efs.shared.security.SecurityContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class RelationshipAnalysisAccessService
        implements RelationshipAnalysisAccessServiceInterface {

    private static final String PERMISSION_VIEW =
            "relationship.analysis.view";

    private static final String PERMISSION_CREATE =
            "relationship.analysis.create";

    private final RelationshipAnalysisServiceInterface
            relationshipAnalysisService;

    public RelationshipAnalysisAccessService(
            RelationshipAnalysisServiceInterface relationshipAnalysisService) {

        this.relationshipAnalysisService =
                relationshipAnalysisService;
    }

    @Override
    public RelationshipAnalysisResponse createRelationshipAnalysis(
            RelationshipAnalysisRequest request,
            SecurityContext securityContext) {

        requirePermission(
                securityContext,
                PERMISSION_CREATE
        );

        return relationshipAnalysisService
                .createRelationshipAnalysis(request);
    }

    @Override
    public RelationshipAnalysisResponse getRelationshipAnalysisById(
            UUID relationshipAnalysisId,
            SecurityContext securityContext) {

        requireViewPermission(securityContext);

        return relationshipAnalysisService
                .getRelationshipAnalysisById(
                        relationshipAnalysisId
                );
    }

    @Override
    public List<RelationshipAnalysisResponse> getAnalysesByCustomer(
            UUID customerId,
            SecurityContext securityContext) {

        requireViewPermission(securityContext);

        return relationshipAnalysisService
                .getAnalysesByCustomer(customerId);
    }

    @Override
    public List<RelationshipAnalysisResponse> getAnalysesByTransaction(
            UUID transactionId,
            SecurityContext securityContext) {

        requireViewPermission(securityContext);

        return relationshipAnalysisService
                .getAnalysesByTransaction(transactionId);
    }

    @Override
    public List<RelationshipAnalysisResponse> getAnalysesByCorrelation(
            UUID correlationId,
            SecurityContext securityContext) {

        requireViewPermission(securityContext);

        return relationshipAnalysisService
                .getAnalysesByCorrelation(correlationId);
    }

    @Override
    public List<RelationshipAnalysisResponse> getAnalysesByType(
            String relationshipType,
            SecurityContext securityContext) {

        requireViewPermission(securityContext);

        return relationshipAnalysisService
                .getAnalysesByType(relationshipType);
    }

    @Override
    public List<RelationshipAnalysisResponse> getAnalysesBySource(
            String sourceEntityKey,
            SecurityContext securityContext) {

        requireViewPermission(securityContext);

        return relationshipAnalysisService
                .getAnalysesBySource(sourceEntityKey);
    }

    @Override
    public List<RelationshipAnalysisResponse> getAnalysesByTarget(
            String targetEntityKey,
            SecurityContext securityContext) {

        requireViewPermission(securityContext);

        return relationshipAnalysisService
                .getAnalysesByTarget(targetEntityKey);
    }

    @Override
    public List<RelationshipAnalysisResponse> getAnalysesByStatus(
            String analysisStatus,
            SecurityContext securityContext) {

        requireViewPermission(securityContext);

        return relationshipAnalysisService
                .getAnalysesByStatus(analysisStatus);
    }

    private void requireViewPermission(
            SecurityContext securityContext) {

        requirePermission(
                securityContext,
                PERMISSION_VIEW
        );
    }

    private void requirePermission(
            SecurityContext securityContext,
            String permission) {

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        if (!securityContext.hasPermission(permission)) {

            throw new AccessDeniedException(
                    "Missing permission: " + permission
            );
        }
    }
}