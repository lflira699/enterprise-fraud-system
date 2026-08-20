package com.efs.modules.rules.repository;

import com.efs.modules.rules.entity.RuleSimulation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RuleSimulationRepository
        extends JpaRepository<RuleSimulation, UUID> {

    Optional<RuleSimulation> findBySimulationId(
            UUID simulationId
    );

    List<RuleSimulation> findByEntityTypeAndEntityIdOrderByStartedAtDesc(
            String entityType,
            UUID entityId
    );

    List<RuleSimulation> findBySimulationStatusOrderByStartedAtDesc(
            String simulationStatus
    );

    List<RuleSimulation> findByExecutedByOrderByStartedAtDesc(
            UUID executedBy
    );
}