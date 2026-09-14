package com.efs.modules.risk.repository;

import com.efs.modules.risk.entity.RiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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

    @Query(
            value = """
                    SELECT AVG(latest.overall_risk_score)
                    FROM (
                        SELECT DISTINCT ON (ra.transaction_id)
                            ra.overall_risk_score
                        FROM transaction.risk_assessment AS ra
                        WHERE ra.organization_id = :organizationId
                          AND ra.tenant_id = :tenantId
                          AND ra.deleted_at IS NULL
                          AND ra.overall_risk_score IS NOT NULL
                        ORDER BY
                            ra.transaction_id,
                            ra.assessment_timestamp DESC
                    ) AS latest
                    """,
            nativeQuery = true
    )
    BigDecimal averageLatestRiskScoreByOrganizationIdAndTenantId(
            @Param("organizationId") UUID organizationId,
            @Param("tenantId") UUID tenantId
    );

    @Query(
            value = """
                    SELECT AVG(latest.overall_risk_score)
                    FROM (
                        SELECT DISTINCT ON (ra.transaction_id)
                            ra.overall_risk_score
                        FROM transaction.risk_assessment AS ra
                        WHERE ra.organization_id = :organizationId
                          AND ra.tenant_id IS NULL
                          AND ra.deleted_at IS NULL
                          AND ra.overall_risk_score IS NOT NULL
                        ORDER BY
                            ra.transaction_id,
                            ra.assessment_timestamp DESC
                    ) AS latest
                    """,
            nativeQuery = true
    )
    BigDecimal averageLatestRiskScoreByOrganizationIdAndTenantIdIsNull(
            @Param("organizationId") UUID organizationId
    );
}
