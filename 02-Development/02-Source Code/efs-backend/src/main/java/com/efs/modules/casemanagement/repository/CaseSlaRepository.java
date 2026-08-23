package com.efs.modules.casemanagement.repository;

import com.efs.modules.casemanagement.entity.CaseSla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaseSlaRepository
        extends JpaRepository<CaseSla, UUID> {

    List<CaseSla> findByCaseIdOrderByDeadlineAsc(
            UUID caseId
    );

    Optional<CaseSla> findBySlaIdAndCaseId(
            UUID slaId,
            UUID caseId
    );
}