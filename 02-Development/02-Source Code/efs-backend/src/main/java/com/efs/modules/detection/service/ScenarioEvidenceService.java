package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.ScenarioEvidenceRequest;
import com.efs.modules.detection.dto.ScenarioEvidenceResponse;
import com.efs.modules.detection.entity.ScenarioEvidence;
import com.efs.modules.detection.mapper.ScenarioEvidenceMapper;
import com.efs.modules.detection.repository.ScenarioEvidenceRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ScenarioEvidenceService
        implements ScenarioEvidenceServiceInterface {

    private final ScenarioEvidenceRepository scenarioEvidenceRepository;
    private final ScenarioEvidenceMapper scenarioEvidenceMapper;

    public ScenarioEvidenceService(
            ScenarioEvidenceRepository scenarioEvidenceRepository,
            ScenarioEvidenceMapper scenarioEvidenceMapper) {

        this.scenarioEvidenceRepository = scenarioEvidenceRepository;
        this.scenarioEvidenceMapper = scenarioEvidenceMapper;
    }

    @Override
    @Transactional
    public ScenarioEvidenceResponse createScenarioEvidence(
            ScenarioEvidenceRequest request) {

        ScenarioEvidence evidence =
                scenarioEvidenceMapper.toEntity(request);

        evidence.setCreatedAt(
                LocalDateTime.now()
        );

        ScenarioEvidence savedEvidence =
                scenarioEvidenceRepository.save(evidence);

        return scenarioEvidenceMapper.toResponse(
                savedEvidence
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ScenarioEvidenceResponse getScenarioEvidenceById(
            UUID evidenceId) {

        ScenarioEvidence evidence =
                scenarioEvidenceRepository
                        .findByEvidenceId(evidenceId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Scenario evidence not found: "
                                                + evidenceId
                                )
                        );

        return scenarioEvidenceMapper.toResponse(
                evidence
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioEvidenceResponse>
    getEvidenceByScenarioVersion(
            UUID scenarioVersionId) {

        return scenarioEvidenceRepository
                .findByScenarioVersionIdOrderByObservedAtDesc(
                        scenarioVersionId
                )
                .stream()
                .map(scenarioEvidenceMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioEvidenceResponse>
    getEvidenceByType(String evidenceType) {

        return scenarioEvidenceRepository
                .findByEvidenceTypeOrderByObservedAtDesc(
                        evidenceType
                )
                .stream()
                .map(scenarioEvidenceMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioEvidenceResponse>
    getEvidenceBySourceType(String sourceType) {

        return scenarioEvidenceRepository
                .findBySourceTypeOrderByObservedAtDesc(
                        sourceType
                )
                .stream()
                .map(scenarioEvidenceMapper::toResponse)
                .toList();
    }
}