package com.efs.modules.casemanagement.repository;

import com.efs.modules.casemanagement.entity.CaseNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaseNotificationRepository
        extends JpaRepository<CaseNotification, UUID> {

    Optional<CaseNotification> findByCaseNotificationIdAndCaseId(
            UUID caseNotificationId,
            UUID caseId
    );

    List<CaseNotification> findByCaseIdOrderByCreatedAtDesc(
            UUID caseId
    );
}