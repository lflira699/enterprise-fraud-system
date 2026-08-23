package com.efs.modules.playbook.repository;

import com.efs.modules.playbook.entity.PlaybookExecutionStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlaybookExecutionStepRepository
        extends JpaRepository<PlaybookExecutionStep, UUID> {

    List<PlaybookExecutionStep> findByPlaybookExecutionIdOrderByCreatedAtAsc(
            UUID playbookExecutionId
    );

    Optional<PlaybookExecutionStep>
            findByPlaybookExecutionIdAndPlaybookStepId(
                    UUID playbookExecutionId,
                    UUID playbookStepId
            );
}