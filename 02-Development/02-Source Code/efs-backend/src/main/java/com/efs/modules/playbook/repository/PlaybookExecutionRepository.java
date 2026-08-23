package com.efs.modules.playbook.repository;

import com.efs.modules.playbook.entity.PlaybookExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlaybookExecutionRepository
        extends JpaRepository<PlaybookExecution, UUID> {

    List<PlaybookExecution> findByPlaybookVersionIdOrderByStartedAtDesc(
            UUID playbookVersionId
    );

    List<PlaybookExecution> findByAlertIdOrderByStartedAtDesc(
            UUID alertId
    );

    List<PlaybookExecution> findByStatusOrderByStartedAtDesc(
            String status
    );
}