package com.efs.modules.transaction.repository;

import com.efs.modules.transaction.entity.TransactionScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionScoreRepository
        extends JpaRepository<TransactionScore, UUID> {

    Optional<TransactionScore> findByScoreId(
            UUID scoreId
    );

    List<TransactionScore> findByTransactionIdOrderByCalculatedAtDesc(
            UUID transactionId
    );

    List<TransactionScore> findByScoreTypeOrderByCalculatedAtDesc(
            String scoreType
    );

    List<TransactionScore> findByScoringModelOrderByCalculatedAtDesc(
            String scoringModel
    );
}