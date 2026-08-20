package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.BehavioralAnalysisRequest;
import com.efs.modules.detection.dto.BehavioralAnalysisResponse;

import java.util.List;
import java.util.UUID;

public interface BehavioralAnalysisServiceInterface {

    BehavioralAnalysisResponse createBehavioralAnalysis(
            BehavioralAnalysisRequest request
    );

    BehavioralAnalysisResponse getBehavioralAnalysisById(
            UUID behavioralAnalysisId
    );

    List<BehavioralAnalysisResponse> getAnalysesByCustomer(
            UUID customerId
    );

    List<BehavioralAnalysisResponse> getAnalysesByTransaction(
            UUID transactionId
    );

    List<BehavioralAnalysisResponse> getAnalysesByCorrelation(
            UUID correlationId
    );

    List<BehavioralAnalysisResponse> getAnalysesByStatus(
            String analysisStatus
    );
}