package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.NetworkAnalysisRequest;
import com.efs.modules.detection.dto.NetworkAnalysisResponse;
import com.efs.shared.security.SecurityContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class NetworkAnalysisAccessService
        implements NetworkAnalysisAccessServiceInterface {

    private static final String PERMISSION_VIEW =
            "network.analysis.view";

    private static final String PERMISSION_CREATE =
            "network.analysis.create";

    private final NetworkAnalysisServiceInterface
            networkAnalysisService;

    public NetworkAnalysisAccessService(
            NetworkAnalysisServiceInterface networkAnalysisService) {

        this.networkAnalysisService =
                networkAnalysisService;
    }

    @Override
    public NetworkAnalysisResponse createNetworkAnalysis(
            NetworkAnalysisRequest request,
            SecurityContext securityContext) {

        requirePermission(
                securityContext,
                PERMISSION_CREATE
        );

        return networkAnalysisService
                .createNetworkAnalysis(request);
    }

    @Override
    public NetworkAnalysisResponse getNetworkAnalysisById(
            UUID networkAnalysisId,
            SecurityContext securityContext) {

        requireViewPermission(securityContext);

        return networkAnalysisService
                .getNetworkAnalysisById(networkAnalysisId);
    }

    @Override
    public List<NetworkAnalysisResponse> getAnalysesByCustomer(
            UUID customerId,
            SecurityContext securityContext) {

        requireViewPermission(securityContext);

        return networkAnalysisService
                .getAnalysesByCustomer(customerId);
    }

    @Override
    public List<NetworkAnalysisResponse> getAnalysesByTransaction(
            UUID transactionId,
            SecurityContext securityContext) {

        requireViewPermission(securityContext);

        return networkAnalysisService
                .getAnalysesByTransaction(transactionId);
    }

    @Override
    public List<NetworkAnalysisResponse> getAnalysesByCorrelation(
            UUID correlationId,
            SecurityContext securityContext) {

        requireViewPermission(securityContext);

        return networkAnalysisService
                .getAnalysesByCorrelation(correlationId);
    }

    @Override
    public List<NetworkAnalysisResponse> getAnalysesByType(
            String networkType,
            SecurityContext securityContext) {

        requireViewPermission(securityContext);

        return networkAnalysisService
                .getAnalysesByType(networkType);
    }

    @Override
    public List<NetworkAnalysisResponse> getAnalysesByStatus(
            String analysisStatus,
            SecurityContext securityContext) {

        requireViewPermission(securityContext);

        return networkAnalysisService
                .getAnalysesByStatus(analysisStatus);
    }

    @Override
    public List<NetworkAnalysisResponse> getAnalysesByKey(
            String networkKey,
            SecurityContext securityContext) {

        requireViewPermission(securityContext);

        return networkAnalysisService
                .getAnalysesByKey(networkKey);
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