package com.efs.modules.catalog.repository;

import com.efs.modules.catalog.entity.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RiskLevelRepository
        extends JpaRepository<RiskLevel, UUID> {

    Optional<RiskLevel> findByRiskCode(
            String riskCode
    );

    List<RiskLevel> findByStatusOrderByDisplayOrderAsc(
            String status
    );

    List<RiskLevel> findAllByOrderByDisplayOrderAsc();
}