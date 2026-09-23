package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.DeviceAnalysisRequest;
import com.efs.modules.detection.dto.DeviceAnalysisResponse;
import com.efs.shared.security.SecurityContext;

import java.util.List;
import java.util.UUID;

public interface DeviceAnalysisAccessServiceInterface {

    DeviceAnalysisResponse createDeviceAnalysis(
            DeviceAnalysisRequest request,
            SecurityContext securityContext
    );

    DeviceAnalysisResponse getDeviceAnalysisById(
            UUID deviceAnalysisId,
            SecurityContext securityContext
    );

    List<DeviceAnalysisResponse> getAnalysesByCustomer(
            UUID customerId,
            SecurityContext securityContext
    );

    List<DeviceAnalysisResponse> getAnalysesByTransaction(
            UUID transactionId,
            SecurityContext securityContext
    );

    List<DeviceAnalysisResponse> getAnalysesByCorrelation(
            UUID correlationId,
            SecurityContext securityContext
    );

    List<DeviceAnalysisResponse> getAnalysesByDeviceId(
            String deviceId,
            SecurityContext securityContext
    );

    List<DeviceAnalysisResponse> getAnalysesByFingerprint(
            String deviceFingerprint,
            SecurityContext securityContext
    );

    List<DeviceAnalysisResponse> getAnalysesByIpAddress(
            String ipAddress,
            SecurityContext securityContext
    );

    List<DeviceAnalysisResponse> getAnalysesByStatus(
            String analysisStatus,
            SecurityContext securityContext
    );
}