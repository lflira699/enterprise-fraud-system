package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.ScenarioVersionRequest;
import com.efs.modules.detection.dto.ScenarioVersionResponse;
import com.efs.modules.detection.entity.ScenarioVersion;
import com.efs.modules.detection.mapper.ScenarioVersionMapper;
import com.efs.modules.detection.repository.ScenarioVersionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ScenarioVersionService
        implements ScenarioVersionServiceInterface {

    private final ScenarioVersionRepository scenarioVersionRepository;
    private final ScenarioVersionMapper scenarioVersionMapper;

    public ScenarioVersionService(
            ScenarioVersionRepository scenarioVersionRepository,
            ScenarioVersionMapper scenarioVersionMapper) {

        this.scenarioVersionRepository = scenarioVersionRepository;
        this.scenarioVersionMapper = scenarioVersionMapper;
    }

    @Override
    @Transactional
    public ScenarioVersionResponse createScenarioVersion(
            ScenarioVersionRequest request) {

        ScenarioVersion version =
                scenarioVersionMapper.toEntity(request);

        LocalDateTime now =
                LocalDateTime.now();

        version.setCreatedAt(now);
        version.setUpdatedAt(now);

        ScenarioVersion savedVersion =
                scenarioVersionRepository.save(version);

        return scenarioVersionMapper.toResponse(savedVersion);
    }

    @Override
    @Transactional(readOnly = true)
    public ScenarioVersionResponse getScenarioVersionById(
            UUID scenarioVersionId) {

        ScenarioVersion version =
                scenarioVersionRepository
                        .findByScenarioVersionId(scenarioVersionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Scenario version not found: "
                                                + scenarioVersionId
                                )
                        );

        return scenarioVersionMapper.toResponse(version);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioVersionResponse> getScenarioVersionsByScenario(
            UUID scenarioId) {

        return scenarioVersionRepository
                .findByScenarioIdOrderByVersionNumberDesc(scenarioId)
                .stream()
                .map(scenarioVersionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ScenarioVersionResponse getScenarioVersionByNumber(
            UUID scenarioId,
            Integer versionNumber) {

        ScenarioVersion version =
                scenarioVersionRepository
                        .findByScenarioIdAndVersionNumber(
                                scenarioId,
                                versionNumber
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Scenario version not found for scenario "
                                                + scenarioId
                                                + " and version "
                                                + versionNumber
                                )
                        );

        return scenarioVersionMapper.toResponse(version);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioVersionResponse> getScenarioVersionsByStatus(
            String versionStatus) {

        return scenarioVersionRepository
                .findByVersionStatusOrderByCreatedAtDesc(versionStatus)
                .stream()
                .map(scenarioVersionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioVersionResponse> getScenarioVersionsByActivationMode(
            String activationMode) {

        return scenarioVersionRepository
                .findByActivationModeOrderByCreatedAtDesc(activationMode)
                .stream()
                .map(scenarioVersionMapper::toResponse)
                .toList();
    }
}