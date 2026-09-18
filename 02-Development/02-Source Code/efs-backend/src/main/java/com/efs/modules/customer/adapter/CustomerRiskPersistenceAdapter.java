package com.efs.modules.customer.adapter;

import com.efs.modules.customer.dto.CustomerRiskProfileRequest;
import com.efs.modules.customer.dto.CustomerRiskProfileResponse;
import com.efs.modules.customer.entity.CustomerHistory;
import com.efs.modules.customer.entity.CustomerRiskProfile;
import com.efs.modules.customer.mapper.CustomerRiskProfileMapper;
import com.efs.modules.customer.repository.CustomerHistoryRepository;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.customer.repository.CustomerRiskProfileRepository;
import com.efs.modules.risk.port.out.CustomerRiskPersistencePort;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class CustomerRiskPersistenceAdapter
        implements CustomerRiskPersistencePort {

    private final CustomerRepository customerRepository;

    private final CustomerRiskProfileRepository
            customerRiskProfileRepository;

    private final CustomerHistoryRepository
            customerHistoryRepository;

    private final CustomerRiskProfileMapper
            customerRiskProfileMapper;

    public CustomerRiskPersistenceAdapter(
            CustomerRepository customerRepository,
            CustomerRiskProfileRepository
                    customerRiskProfileRepository,
            CustomerHistoryRepository
                    customerHistoryRepository,
            CustomerRiskProfileMapper
                    customerRiskProfileMapper) {

        this.customerRepository =
                customerRepository;

        this.customerRiskProfileRepository =
                customerRiskProfileRepository;

        this.customerHistoryRepository =
                customerHistoryRepository;

        this.customerRiskProfileMapper =
                customerRiskProfileMapper;
    }

    @Override
    public boolean activeCustomerExists(
            UUID customerId) {

        return customerRepository
                .findByCustomerIdAndDeletedAtIsNull(
                        customerId
                )
                .isPresent();
    }

    @Override
    public boolean activeRiskProfileExists(
            UUID customerId) {

        return customerRiskProfileRepository
                .existsByCustomerIdAndDeletedAtIsNull(
                        customerId
                );
    }

    @Override
    public Optional<CustomerRiskProfileResponse>
    findActiveRiskProfile(
            UUID customerId) {

        return customerRiskProfileRepository
                .findByCustomerIdAndDeletedAtIsNull(
                        customerId
                )
                .map(
                        customerRiskProfileMapper::toResponse
                );
    }

    @Override
    public CustomerRiskProfileResponse createRiskProfile(
            UUID customerId,
            CustomerRiskProfileRequest request,
            BigDecimal calculatedRiskScore,
            String calculatedRiskLevel,
            LocalDateTime timestamp) {

        CustomerRiskProfile profile =
                new CustomerRiskProfile();

        profile.setCustomerId(
                customerId
        );

        applyCalculatedState(
                profile,
                request,
                calculatedRiskScore,
                calculatedRiskLevel
        );

        profile.setLastCalculation(
                timestamp
        );

        profile.setCreatedAt(
                timestamp
        );

        profile.setUpdatedAt(
                timestamp
        );

        profile.setCreatedBy(
                request.getCreatedBy()
        );

        profile.setUpdatedBy(
                request.getUpdatedBy()
        );

        CustomerRiskProfile savedProfile =
                customerRiskProfileRepository.save(
                        profile
                );

        return customerRiskProfileMapper.toResponse(
                savedProfile
        );
    }

    @Override
    public CustomerRiskProfileResponse updateRiskProfile(
            UUID customerId,
            CustomerRiskProfileRequest request,
            BigDecimal calculatedRiskScore,
            String calculatedRiskLevel,
            LocalDateTime timestamp) {

        CustomerRiskProfile profile =
                customerRiskProfileRepository
                        .findByCustomerIdAndDeletedAtIsNull(
                                customerId
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Customer risk profile not found: "
                                                        + customerId
                                        )
                        );

        applyCalculatedState(
                profile,
                request,
                calculatedRiskScore,
                calculatedRiskLevel
        );

        profile.setLastCalculation(
                timestamp
        );

        profile.setUpdatedAt(
                timestamp
        );

        profile.setUpdatedBy(
                request.getUpdatedBy()
        );

        CustomerRiskProfile savedProfile =
                customerRiskProfileRepository.save(
                        profile
                );

        return customerRiskProfileMapper.toResponse(
                savedProfile
        );
    }

    @Override
    public Optional<String>
    findLatestRiskAssessmentSourceReference(
            UUID customerId,
            String eventType) {

        return customerHistoryRepository
                .findFirstByCustomerIdAndEventTypeOrderByEventTimestampDesc(
                        customerId,
                        eventType
                )
                .map(
                        CustomerHistory::getSourceReference
                );
    }

    @Override
    public void createRiskAssessmentHistory(
            UUID customerId,
            BigDecimal previousRiskScore,
            String previousRiskLevel,
            BigDecimal newRiskScore,
            String newRiskLevel,
            String sourceReference,
            LocalDateTime timestamp) {

        CustomerHistory history =
                new CustomerHistory();

        history.setCustomerId(
                customerId
        );

        history.setEventType(
                "CUSTOMER_RISK_ASSESSED"
        );

        history.setPreviousRiskScore(
                previousRiskScore
        );

        history.setPreviousRiskLevel(
                previousRiskLevel
        );

        history.setNewRiskScore(
                newRiskScore
        );

        history.setNewRiskLevel(
                newRiskLevel
        );

        history.setEventTimestamp(
                timestamp
        );

        history.setSourceReference(
                sourceReference
        );

        history.setCreatedAt(
                timestamp
        );

        customerHistoryRepository.save(
                history
        );
    }

    private void applyCalculatedState(
            CustomerRiskProfile profile,
            CustomerRiskProfileRequest request,
            BigDecimal calculatedRiskScore,
            String calculatedRiskLevel) {

        profile.setCurrentRiskScore(
                calculatedRiskScore
        );

        profile.setRiskLevel(
                calculatedRiskLevel
        );

        profile.setBehaviorScore(
                request.getBehaviorScore()
        );

        profile.setFraudScore(
                request.getFraudScore()
        );

        profile.setAmlScore(
                request.getAmlScore()
        );

        profile.setKycScore(
                request.getKycScore()
        );

        profile.setDeviceScore(
                request.getDeviceScore()
        );

        profile.setSanctionsScore(
                request.getSanctionsScore()
        );

        profile.setPepScore(
                request.getPepScore()
        );

        profile.setWatchlistScore(
                request.getWatchlistScore()
        );
    }
}