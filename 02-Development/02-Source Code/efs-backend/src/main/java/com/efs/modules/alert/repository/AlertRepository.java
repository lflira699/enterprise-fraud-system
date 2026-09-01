package com.efs.modules.alert.repository;

import com.efs.modules.alert.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AlertRepository
        extends JpaRepository<Alert, UUID>,
        JpaSpecificationExecutor<Alert> {

    Optional<Alert> findByAlertId(
            UUID alertId
    );

    List<Alert> findByTransactionIdOrderByGeneratedAtDesc(
            UUID transactionId
    );

    List<Alert> findByDecisionIdOrderByGeneratedAtDesc(
            UUID decisionId
    );

    List<Alert> findByStatusOrderByGeneratedAtDesc(
            String status
    );

    List<Alert> findByPriorityOrderByGeneratedAtDesc(
            String priority
    );

    List<Alert> findByAlertTypeOrderByGeneratedAtDesc(
            String alertType
    );
}