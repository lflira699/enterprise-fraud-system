package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.RiskLevelRequest;
import com.efs.modules.catalog.dto.RiskLevelResponse;
import com.efs.modules.catalog.entity.RiskLevel;
import com.efs.modules.catalog.mapper.RiskLevelMapper;
import com.efs.modules.catalog.repository.RiskLevelRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RiskLevelService
        implements RiskLevelServiceInterface {

    private final RiskLevelRepository riskLevelRepository;
    private final RiskLevelMapper riskLevelMapper;

    public RiskLevelService(
            RiskLevelRepository riskLevelRepository,
            RiskLevelMapper riskLevelMapper) {

        this.riskLevelRepository =
                riskLevelRepository;

        this.riskLevelMapper =
                riskLevelMapper;
    }

    @Override
    public RiskLevelResponse createRiskLevel(
            RiskLevelRequest request) {

        RiskLevel riskLevel =
                riskLevelMapper.toEntity(
                        request
                );

        RiskLevel savedRiskLevel =
                riskLevelRepository.save(
                        riskLevel
                );

        return riskLevelMapper.toResponse(
                savedRiskLevel
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RiskLevelResponse getRiskLevelById(
            UUID riskLevelId) {

        RiskLevel riskLevel =
                riskLevelRepository
                        .findById(riskLevelId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Risk level not found: "
                                                        + riskLevelId
                                        )
                        );

        return riskLevelMapper.toResponse(
                riskLevel
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RiskLevelResponse getRiskLevelByRiskCode(
            String riskCode) {

        RiskLevel riskLevel =
                riskLevelRepository
                        .findByRiskCode(
                                riskCode
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Risk level not found for risk code: "
                                                        + riskCode
                                        )
                        );

        return riskLevelMapper.toResponse(
                riskLevel
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<RiskLevelResponse> getRiskLevelsByStatus(
            String status) {

        return riskLevelRepository
                .findByStatusOrderByDisplayOrderAsc(
                        status
                )
                .stream()
                .map(riskLevelMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RiskLevelResponse> getAllRiskLevels() {

        return riskLevelRepository
                .findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(riskLevelMapper::toResponse)
                .toList();
    }
}