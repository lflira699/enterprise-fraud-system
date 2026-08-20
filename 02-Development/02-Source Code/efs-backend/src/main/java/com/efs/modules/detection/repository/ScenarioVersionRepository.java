package com.efs.modules.detection.repository;

import com.efs.modules.detection.entity.ScenarioVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScenarioVersionRepository
        extends JpaRepository<ScenarioVersion, UUID> {

    Optional<ScenarioVersion> findByScenarioVersionId(
            UUID scenarioVersionId
    );

    List<ScenarioVersion> findByScenarioIdOrderByVersionNumberDesc(
            UUID scenarioId
    );

    Optional<ScenarioVersion> findByScenarioIdAndVersionNumber(
            UUID scenarioId,
            Integer versionNumber
    );

    List<ScenarioVersion> findByVersionStatusOrderByCreatedAtDesc(
            String versionStatus
    );

    List<ScenarioVersion> findByActivationModeOrderByCreatedAtDesc(
            String activationMode
    );
}