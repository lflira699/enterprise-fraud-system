package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.DetectionScenarioRequest;
import com.efs.modules.detection.dto.DetectionScenarioResponse;
import com.efs.shared.pagination.PageResponse;
import com.efs.shared.security.SecurityContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class DetectionScenarioAccessService
        implements DetectionScenarioAccessServiceInterface {

    private static final String VIEW_PERMISSION =
            "scenario.view";

    private static final String CREATE_PERMISSION =
            "scenario.create";

    private final DetectionScenarioServiceInterface
            detectionScenarioService;

    public DetectionScenarioAccessService(
            DetectionScenarioServiceInterface
                    detectionScenarioService) {

        this.detectionScenarioService =
                detectionScenarioService;
    }

    @Override
    public DetectionScenarioResponse createScenario(
            DetectionScenarioRequest request,
            SecurityContext securityContext) {

        requirePermission(
                securityContext,
                CREATE_PERMISSION
        );

        return detectionScenarioService.createScenario(
                request
        );
    }

    @Override
    public DetectionScenarioResponse getScenarioById(
            UUID scenarioId,
            SecurityContext securityContext) {

        requirePermission(
                securityContext,
                VIEW_PERMISSION
        );

        return detectionScenarioService.getScenarioById(
                scenarioId
        );
    }

    @Override
    public DetectionScenarioResponse getScenarioByCodeAndVersion(
            String scenarioCode,
            Integer version,
            SecurityContext securityContext) {

        requirePermission(
                securityContext,
                VIEW_PERMISSION
        );

        return detectionScenarioService
                .getScenarioByCodeAndVersion(
                        scenarioCode,
                        version
                );
    }

    @Override
    public List<DetectionScenarioResponse> getScenariosByCode(
            String scenarioCode,
            SecurityContext securityContext) {

        requirePermission(
                securityContext,
                VIEW_PERMISSION
        );

        return detectionScenarioService
                .getScenariosByCode(
                        scenarioCode
                );
    }

    @Override
    public List<DetectionScenarioResponse> getScenariosByCategory(
            String category,
            SecurityContext securityContext) {

        requirePermission(
                securityContext,
                VIEW_PERMISSION
        );

        return detectionScenarioService
                .getScenariosByCategory(
                        category
                );
    }

    @Override
    public List<DetectionScenarioResponse> getScenariosByStatus(
            String status,
            SecurityContext securityContext) {

        requirePermission(
                securityContext,
                VIEW_PERMISSION
        );

        return detectionScenarioService
                .getScenariosByStatus(
                        status
                );
    }

    @Override
    public List<DetectionScenarioResponse> getScenariosByCriticality(
            String criticality,
            SecurityContext securityContext) {

        requirePermission(
                securityContext,
                VIEW_PERMISSION
        );

        return detectionScenarioService
                .getScenariosByCriticality(
                        criticality
                );
    }

    @Override
    public List<DetectionScenarioResponse> getScenariosByOwner(
            String owner,
            SecurityContext securityContext) {

        requirePermission(
                securityContext,
                VIEW_PERMISSION
        );

        return detectionScenarioService
                .getScenariosByOwner(
                        owner
                );
    }

    @Override
    public PageResponse<DetectionScenarioResponse> searchScenarios(
            String scenarioCode,
            String category,
            String status,
            String criticality,
            String owner,
            int page,
            int size,
            String sort,
            String direction,
            SecurityContext securityContext) {

        requirePermission(
                securityContext,
                VIEW_PERMISSION
        );

        return detectionScenarioService.searchScenarios(
                scenarioCode,
                category,
                status,
                criticality,
                owner,
                page,
                size,
                sort,
                direction
        );
    }

    private void requirePermission(
            SecurityContext securityContext,
            String permission) {

        Objects.requireNonNull(
                securityContext,
                "securityContext is required"
        );

        Objects.requireNonNull(
                permission,
                "permission is required"
        );

        if (!securityContext.hasPermission(
                permission
        )) {

            throw new AccessDeniedException(
                    "Missing required permission: "
                            + permission
            );
        }
    }
}