package com.efs.modules.casemanagement.repository;

import com.efs.modules.casemanagement.entity.CaseTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaseTaskRepository
        extends JpaRepository<CaseTask, UUID> {

    Optional<CaseTask> findByTaskId(
            UUID taskId
    );

    List<CaseTask> findByCaseIdOrderByCreatedAtDesc(
            UUID caseId
    );

    List<CaseTask> findByCaseIdAndStatusOrderByCreatedAtDesc(
            UUID caseId,
            String status
    );

    List<CaseTask> findByAssignedToOrderByCreatedAtDesc(
            UUID assignedTo
    );

    List<CaseTask> findByStatusOrderByCreatedAtDesc(
            String status
    );
}