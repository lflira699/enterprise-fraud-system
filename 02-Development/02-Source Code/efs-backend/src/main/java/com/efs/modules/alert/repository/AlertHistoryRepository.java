package com.efs.modules.alert.repository;

import com.efs.modules.alert.entity.AlertHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlertHistoryRepository
        extends JpaRepository<AlertHistory, UUID> {

    List<AlertHistory> findByAlertIdOrderByChangedAtDesc(
            UUID alertId
    );

    List<AlertHistory> findByAlertIdAndActionTypeOrderByChangedAtDesc(
            UUID alertId,
            String actionType
    );
}