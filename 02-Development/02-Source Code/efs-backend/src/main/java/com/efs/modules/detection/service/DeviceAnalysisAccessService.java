package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.DeviceAnalysisRequest;
import com.efs.modules.detection.dto.DeviceAnalysisResponse;
import com.efs.shared.security.SecurityContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class DeviceAnalysisAccessService
        implements DeviceAnalysisAccessServiceInterface {

    private static final String PERMISSION_VIEW =
            "device.analysis.view";

    private static final String PERMISSION_CREATE =
            "device.analysis.create";

    private final DeviceAnalysisServiceInterface
            deviceAnalysisService;

    public DeviceAnalysisAccessService(
            DeviceAnalysisServiceInterface deviceAnalysisService) {

        this.deviceAnalysisService =
                deviceAnalysisService;
    }

    @Override
    public DeviceAnalysisResponse createDeviceAnalysis(
            DeviceAnalysisRequest request,
            SecurityContext securityContext) {

        requirePermission(
                securityContext,
                PERMISSION_CREATE
        );

        return deviceAnalysisService
                .createDeviceAnalysis(request);
    }

    @Override
    public DeviceAnalysisResponse getDeviceAnalysisById(
            UUID deviceAnalysisId,
            SecurityContext securityContext) {

        requireViewPermission(securityContext);

        return deviceAnalysisService
                .getDeviceAnalysisById(deviceAnalysisId);
    }

    @Override
    public List<DeviceAnalysisResponse> getAnalysesByCustomer(
            UUID customerId,
            SecurityContext securityContext) {

        requireViewPermission(securityContext);

        return deviceAnalysisService
                .getAnalysesByCustomer(customerId);
    }

    @Override
    public List<DeviceAnalysisResponse> getAnalysesByTransaction(
            UUID transactionId,
            SecurityContext securityContext) {

        requireViewPermission(securityContext);

        return deviceAnalysisService
                .getAnalysesByTransaction(transactionId);
    }

    @Override
    public List<DeviceAnalysisResponse> getAnalysesByCorrelation(
            UUID correlationId,
            SecurityContext securityContext) {

        requireViewPermission(securityContext);

        return deviceAnalysisService
                .getAnalysesByCorrelation(correlationId);
    }

    @Override
    public List<DeviceAnalysisResponse> getAnalysesByDeviceId(
            String deviceId,
            SecurityContext securityContext) {

        requireViewPermission(securityContext);

        return deviceAnalysisService
                .getAnalysesByDeviceId(deviceId);
    }

    @Override
    public List<DeviceAnalysisResponse> getAnalysesByFingerprint(
            String deviceFingerprint,
            SecurityContext securityContext) {

        requireViewPermission(securityContext);

        return deviceAnalysisService
                .getAnalysesByFingerprint(deviceFingerprint);
    }

    @Override
    public List<DeviceAnalysisResponse> getAnalysesByIpAddress(
            String ipAddress,
            SecurityContext securityContext) {

        requireViewPermission(securityContext);

        return deviceAnalysisService
                .getAnalysesByIpAddress(ipAddress);
    }

    @Override
    public List<DeviceAnalysisResponse> getAnalysesByStatus(
            String analysisStatus,
            SecurityContext securityContext) {

        requireViewPermission(securityContext);

        return deviceAnalysisService
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