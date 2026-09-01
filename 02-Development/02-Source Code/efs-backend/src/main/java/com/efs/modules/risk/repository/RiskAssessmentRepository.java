package com.efs.modules.risk.repository;

import com.efs.modules.risk.entity.RiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RiskAssessmentRepository
        extends JpaRepository<RiskAssessment, UUID>,
        JpaSpecificationExecutor<RiskAssessment> {

    List<RiskAssessment> findByTransactionIdOrderByAssessmentTimestampDesc(
            UUID transactionId
    );

    Optional<RiskAssessment> findFirstByTransactionIdOrderByAssessmentTimestampDesc(
            UUID transactionId
    );

    List<RiskAssessment> findByTransactionIdAndAssessmentTypeOrderByAssessmentTimestampDesc(
            UUID transactionId,
            String assessmentType
    );

    List<RiskAssessment> findByRiskLevelOrderByAssessmentTimestampDesc(
            String riskLevel
    );

    List<RiskAssessment> findByAssessmentResultOrderByAssessmentTimestampDesc(
            String assessmentResult
    );
}