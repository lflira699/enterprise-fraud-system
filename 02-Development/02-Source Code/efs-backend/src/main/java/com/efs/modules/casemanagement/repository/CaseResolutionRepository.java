package com.efs.modules.casemanagement.repository;

import com.efs.modules.casemanagement.entity.CaseResolution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaseResolutionRepository
        extends JpaRepository<CaseResolution, UUID> {

    Optional<CaseResolution> findByResolutionId(
            UUID resolutionId
    );

    List<CaseResolution> findByCaseIdOrderByResolvedAtDesc(
            UUID caseId
    );
}