package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.NetworkAnalysisRequest;
import com.efs.modules.detection.dto.NetworkAnalysisResponse;
import com.efs.shared.security.SecurityContext;

import java.util.List;
import java.util.UUID;

public interface NetworkAnalysisAccessServiceInterface {

    NetworkAnalysisResponse createNetworkAnalysis(
            NetworkAnalysisRequest request,
            SecurityContext securityContext
    );

    NetworkAnalysisResponse getNetworkAnalysisById(
            UUID networkAnalysisId,
            SecurityContext securityContext
    );

    List<NetworkAnalysisResponse> getAnalysesByCustomer(
            UUID customerId,
            SecurityContext securityContext
    );

    List<NetworkAnalysisResponse> getAnalysesByTransaction(
            UUID transactionId,
            SecurityContext securityContext
    );

    List<NetworkAnalysisResponse> getAnalysesByCorrelation(
            UUID correlationId,
            SecurityContext securityContext
    );

    List<NetworkAnalysisResponse> getAnalysesByType(
            String networkType,
            SecurityContext securityContext
    );

    List<NetworkAnalysisResponse> getAnalysesByStatus(
            String analysisStatus,
            SecurityContext securityContext
    );

    List<NetworkAnalysisResponse> getAnalysesByKey(
            String networkKey,
            SecurityContext securityContext
    );
}