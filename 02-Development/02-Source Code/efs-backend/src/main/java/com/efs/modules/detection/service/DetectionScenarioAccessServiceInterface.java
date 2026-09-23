package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.DetectionScenarioRequest;
import com.efs.modules.detection.dto.DetectionScenarioResponse;
import com.efs.shared.pagination.PageResponse;
import com.efs.shared.security.SecurityContext;

import java.util.List;
import java.util.UUID;

public interface DetectionScenarioAccessServiceInterface {

    DetectionScenarioResponse createScenario(
            DetectionScenarioRequest request,
            SecurityContext securityContext
    );

    DetectionScenarioResponse getScenarioById(
            UUID scenarioId,
            SecurityContext securityContext
    );

    DetectionScenarioResponse getScenarioByCodeAndVersion(
            String scenarioCode,
            Integer version,
            SecurityContext securityContext
    );

    List<DetectionScenarioResponse> getScenariosByCode(
            String scenarioCode,
            SecurityContext securityContext
    );

    List<DetectionScenarioResponse> getScenariosByCategory(
            String category,
            SecurityContext securityContext
    );

    List<DetectionScenarioResponse> getScenariosByStatus(
            String status,
            SecurityContext securityContext
    );

    List<DetectionScenarioResponse> getScenariosByCriticality(
            String criticality,
            SecurityContext securityContext
    );

    List<DetectionScenarioResponse> getScenariosByOwner(
            String owner,
            SecurityContext securityContext
    );

    PageResponse<DetectionScenarioResponse> searchScenarios(
            String scenarioCode,
            String category,
            String status,
            String criticality,
            String owner,
            int page,
            int size,
            String sort,
            String direction,
            SecurityContext securityContext
    );
}