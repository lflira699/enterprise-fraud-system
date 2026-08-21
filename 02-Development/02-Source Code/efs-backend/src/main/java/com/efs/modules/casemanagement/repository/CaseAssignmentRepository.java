package com.efs.modules.casemanagement.repository;

import com.efs.modules.casemanagement.entity.CaseAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaseAssignmentRepository
        extends JpaRepository<CaseAssignment, UUID> {

    Optional<CaseAssignment> findByAssignmentId(
            UUID assignmentId
    );

    List<CaseAssignment> findByCaseIdOrderByAssignedAtDesc(
            UUID caseId
    );

    List<CaseAssignment> findByAssignedToOrderByAssignedAtDesc(
            UUID assignedTo
    );

    List<CaseAssignment> findByAssignedTeamOrderByAssignedAtDesc(
            String assignedTeam
    );

    Optional<CaseAssignment>
    findFirstByCaseIdAndReleasedAtIsNullOrderByAssignedAtDesc(
            UUID caseId
    );
}