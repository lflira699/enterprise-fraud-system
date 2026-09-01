package com.efs.modules.casemanagement.repository;

import com.efs.modules.casemanagement.entity.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaseRepository
        extends JpaRepository<Case, UUID>,
        JpaSpecificationExecutor<Case> {

    Optional<Case> findByCaseId(
            UUID caseId
    );

    Optional<Case> findByCaseNumber(
            String caseNumber
    );

    List<Case> findByCustomerIdOrderByCreatedAtDesc(
            UUID customerId
    );

    List<Case> findByTransactionIdOrderByCreatedAtDesc(
            UUID transactionId
    );

    List<Case> findByCurrentStatusOrderByCreatedAtDesc(
            String currentStatus
    );

    List<Case> findByPriorityOrderByCreatedAtDesc(
            String priority
    );

    List<Case> findByAssignedUserOrderByCreatedAtDesc(
            UUID assignedUser
    );

    List<Case> findByAssignedTeamOrderByCreatedAtDesc(
            String assignedTeam
    );
}