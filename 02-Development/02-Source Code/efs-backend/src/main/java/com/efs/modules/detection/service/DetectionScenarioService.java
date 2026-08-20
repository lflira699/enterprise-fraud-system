package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.DetectionScenarioRequest;
import com.efs.modules.detection.dto.DetectionScenarioResponse;
import com.efs.modules.detection.entity.DetectionScenario;
import com.efs.modules.detection.mapper.DetectionScenarioMapper;
import com.efs.modules.detection.repository.DetectionScenarioRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DetectionScenarioService
        implements DetectionScenarioServiceInterface {

    private final DetectionScenarioRepository detectionScenarioRepository;
    private final DetectionScenarioMapper detectionScenarioMapper;

    public DetectionScenarioService(
            DetectionScenarioRepository detectionScenarioRepository,
            DetectionScenarioMapper detectionScenarioMapper) {

        this.detectionScenarioRepository =
                detectionScenarioRepository;
        this.detectionScenarioMapper =
                detectionScenarioMapper;
    }

    @Override
    @Transactional
    public DetectionScenarioResponse createScenario(
            DetectionScenarioRequest request) {

        DetectionScenario scenario =
                detectionScenarioMapper.toEntity(request);

        LocalDateTime now = LocalDateTime.now();

        if (scenario.getVersion() == null) {
            scenario.setVersion(1);
        }

        scenario.setCreatedAt(now);
        scenario.setUpdatedAt(now);

        DetectionScenario savedScenario =
                detectionScenarioRepository.save(scenario);

        return detectionScenarioMapper.toResponse(
                savedScenario
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DetectionScenarioResponse getScenarioById(
            UUID scenarioId) {

        DetectionScenario scenario =
                detectionScenarioRepository
                        .findByScenarioId(scenarioId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Detection scenario not found: "
                                                + scenarioId
                                )
                        );

        return detectionScenarioMapper.toResponse(
                scenario
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DetectionScenarioResponse
    getScenarioByCodeAndVersion(
            String scenarioCode,
            Integer version) {

        DetectionScenario scenario =
                detectionScenarioRepository
                        .findByScenarioCodeAndVersion(
                                scenarioCode,
                                version
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Detection scenario not found: "
                                                + scenarioCode
                                                + " version "
                                                + version
                                )
                        );

        return detectionScenarioMapper.toResponse(
                scenario
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetectionScenarioResponse>
    getScenariosByCode(String scenarioCode) {

        return detectionScenarioRepository
                .findByScenarioCodeOrderByVersionDesc(
                        scenarioCode
                )
                .stream()
                .map(detectionScenarioMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetectionScenarioResponse>
    getScenariosByCategory(String category) {

        return detectionScenarioRepository
                .findByCategoryOrderByScenarioNameAsc(
                        category
                )
                .stream()
                .map(detectionScenarioMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetectionScenarioResponse>
    getScenariosByStatus(String status) {

        return detectionScenarioRepository
                .findByStatusOrderByScenarioNameAsc(
                        status
                )
                .stream()
                .map(detectionScenarioMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetectionScenarioResponse>
    getScenariosByCriticality(String criticality) {

        return detectionScenarioRepository
                .findByCriticalityOrderByScenarioNameAsc(
                        criticality
                )
                .stream()
                .map(detectionScenarioMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetectionScenarioResponse>
    getScenariosByOwner(String owner) {

        return detectionScenarioRepository
                .findByOwnerOrderByScenarioNameAsc(owner)
                .stream()
                .map(detectionScenarioMapper::toResponse)
                .toList();
    }
}