package com.efs.modules.detection.mapper;

import com.efs.modules.detection.dto.DeviceAnalysisRequest;
import com.efs.modules.detection.dto.DeviceAnalysisResponse;
import com.efs.modules.detection.entity.DeviceAnalysis;
import org.springframework.stereotype.Component;

@Component
public class DeviceAnalysisMapper {

    public DeviceAnalysis toEntity(
            DeviceAnalysisRequest request) {

        DeviceAnalysis analysis =
                new DeviceAnalysis();

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

        analysis.setDeviceId(
                request.getDeviceId()
        );

        analysis.setDeviceFingerprint(
                request.getDeviceFingerprint()
        );

        analysis.setDeviceType(
                request.getDeviceType()
        );

        analysis.setOperatingSystem(
                request.getOperatingSystem()
        );

        analysis.setBrowser(
                request.getBrowser()
        );

        analysis.setIpAddress(
                request.getIpAddress()
        );

        analysis.setGeolocationContext(
                request.getGeolocationContext()
        );

        analysis.setDeviceIndicators(
                request.getDeviceIndicators()
        );

        analysis.setAnalysisContext(
                request.getAnalysisContext()
        );

        return analysis;
    }

    public DeviceAnalysisResponse toResponse(
            DeviceAnalysis analysis) {

        DeviceAnalysisResponse response =
                new DeviceAnalysisResponse();

        response.setDeviceAnalysisId(
                analysis.getDeviceAnalysisId()
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

        response.setDeviceId(
                analysis.getDeviceId()
        );

        response.setDeviceFingerprint(
                analysis.getDeviceFingerprint()
        );

        response.setDeviceType(
                analysis.getDeviceType()
        );

        response.setOperatingSystem(
                analysis.getOperatingSystem()
        );

        response.setBrowser(
                analysis.getBrowser()
        );

        response.setIpAddress(
                analysis.getIpAddress()
        );

        response.setGeolocationContext(
                analysis.getGeolocationContext()
        );

        response.setDeviceIndicators(
                analysis.getDeviceIndicators()
        );

        response.setAnalysisContext(
                analysis.getAnalysisContext()
        );

        response.setDeviceConfidence(
                analysis.getDeviceConfidence()
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