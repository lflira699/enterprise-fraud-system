package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.NetworkAnalysisRequest;
import com.efs.modules.detection.dto.NetworkAnalysisResponse;

import java.util.List;
import java.util.UUID;

public interface NetworkAnalysisServiceInterface {

    NetworkAnalysisResponse createNetworkAnalysis(
            NetworkAnalysisRequest request
    );

    NetworkAnalysisResponse getNetworkAnalysisById(
            UUID networkAnalysisId
    );

    List<NetworkAnalysisResponse> getAnalysesByCustomer(
            UUID customerId
    );

    List<NetworkAnalysisResponse> getAnalysesByTransaction(
            UUID transactionId
    );

    List<NetworkAnalysisResponse> getAnalysesByCorrelation(
            UUID correlationId
    );

    List<NetworkAnalysisResponse> getAnalysesByType(
            String networkType
    );

    List<NetworkAnalysisResponse> getAnalysesByStatus(
            String analysisStatus
    );

    List<NetworkAnalysisResponse> getAnalysesByKey(
            String networkKey
    );
}