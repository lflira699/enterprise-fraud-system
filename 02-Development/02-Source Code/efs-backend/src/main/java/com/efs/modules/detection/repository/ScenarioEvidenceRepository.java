package com.efs.modules.detection.repository;

import com.efs.modules.detection.entity.ScenarioEvidence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScenarioEvidenceRepository
        extends JpaRepository<ScenarioEvidence, UUID> {

    Optional<ScenarioEvidence> findByEvidenceId(
            UUID evidenceId
    );

    List<ScenarioEvidence> findByScenarioVersionIdOrderByObservedAtDesc(
            UUID scenarioVersionId
    );

    List<ScenarioEvidence> findByEvidenceTypeOrderByObservedAtDesc(
            String evidenceType
    );

    List<ScenarioEvidence> findBySourceTypeOrderByObservedAtDesc(
            String sourceType
    );
}