package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.DeviceAnalysisRequest;
import com.efs.modules.detection.dto.DeviceAnalysisResponse;

import java.util.List;
import java.util.UUID;

public interface DeviceAnalysisServiceInterface {

    DeviceAnalysisResponse createDeviceAnalysis(
            DeviceAnalysisRequest request
    );

    DeviceAnalysisResponse getDeviceAnalysisById(
            UUID deviceAnalysisId
    );

    List<DeviceAnalysisResponse> getAnalysesByCustomer(
            UUID customerId
    );

    List<DeviceAnalysisResponse> getAnalysesByTransaction(
            UUID transactionId
    );

    List<DeviceAnalysisResponse> getAnalysesByCorrelation(
            UUID correlationId
    );

    List<DeviceAnalysisResponse> getAnalysesByDeviceId(
            String deviceId
    );

    List<DeviceAnalysisResponse> getAnalysesByFingerprint(
            String deviceFingerprint
    );

    List<DeviceAnalysisResponse> getAnalysesByIpAddress(
            String ipAddress
    );

    List<DeviceAnalysisResponse> getAnalysesByStatus(
            String analysisStatus
    );
}