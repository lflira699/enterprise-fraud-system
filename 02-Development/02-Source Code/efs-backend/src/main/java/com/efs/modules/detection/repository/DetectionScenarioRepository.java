package com.efs.modules.detection.repository;

import com.efs.modules.detection.entity.DetectionScenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DetectionScenarioRepository
        extends JpaRepository<DetectionScenario, UUID>,
        JpaSpecificationExecutor<DetectionScenario> {

    Optional<DetectionScenario> findByScenarioId(
            UUID scenarioId
    );

    Optional<DetectionScenario>
    findByScenarioCodeAndVersion(
            String scenarioCode,
            Integer version
    );

    List<DetectionScenario>
    findByScenarioCodeOrderByVersionDesc(
            String scenarioCode
    );

    List<DetectionScenario>
    findByCategoryOrderByScenarioNameAsc(
            String category
    );

    List<DetectionScenario>
    findByStatusOrderByScenarioNameAsc(
            String status
    );

    List<DetectionScenario>
    findByCriticalityOrderByScenarioNameAsc(
            String criticality
    );

    List<DetectionScenario>
    findByOwnerOrderByScenarioNameAsc(
            String owner
    );
}