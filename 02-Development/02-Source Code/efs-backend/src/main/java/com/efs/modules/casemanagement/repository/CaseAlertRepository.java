package com.efs.modules.casemanagement.repository;

import com.efs.modules.casemanagement.entity.CaseAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaseAlertRepository
        extends JpaRepository<CaseAlert, UUID> {

    List<CaseAlert> findByCaseIdOrderByGeneratedAtDesc(
            UUID caseId
    );

    Optional<CaseAlert> findBySourceAlertId(
            UUID sourceAlertId
    );

    boolean existsBySourceAlertId(
            UUID sourceAlertId
    );
}