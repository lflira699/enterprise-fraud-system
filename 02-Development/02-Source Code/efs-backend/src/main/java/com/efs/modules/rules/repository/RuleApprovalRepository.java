package com.efs.modules.rules.repository;

import com.efs.modules.rules.entity.RuleApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RuleApprovalRepository
        extends JpaRepository<RuleApproval, UUID> {

    Optional<RuleApproval> findByApprovalId(
            UUID approvalId
    );

    List<RuleApproval> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
            String entityType,
            UUID entityId
    );

    List<RuleApproval> findByApprovalStatusOrderByCreatedAtDesc(
            String approvalStatus
    );

    List<RuleApproval> findBySubmittedByOrderByCreatedAtDesc(
            UUID submittedBy
    );

    List<RuleApproval> findByReviewedByOrderByCreatedAtDesc(
            UUID reviewedBy
    );
}