package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerRiskProfileRequest;
import com.efs.modules.customer.dto.CustomerRiskProfileResponse;
import com.efs.modules.customer.entity.CustomerRiskProfile;
import com.efs.modules.customer.mapper.CustomerRiskProfileMapper;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.customer.repository.CustomerRiskProfileRepository;
import com.efs.shared.exception.DuplicateRecordException;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CustomerRiskProfileService
        implements CustomerRiskProfileServiceInterface {

    private final CustomerRiskProfileRepository customerRiskProfileRepository;
    private final CustomerRepository customerRepository;
    private final CustomerRiskProfileMapper customerRiskProfileMapper;

    public CustomerRiskProfileService(
            CustomerRiskProfileRepository customerRiskProfileRepository,
            CustomerRepository customerRepository,
            CustomerRiskProfileMapper customerRiskProfileMapper) {

        this.customerRiskProfileRepository = customerRiskProfileRepository;
        this.customerRepository = customerRepository;
        this.customerRiskProfileMapper = customerRiskProfileMapper;
    }

    @Override
    @Transactional
    public CustomerRiskProfileResponse createRiskProfile(
            UUID customerId,
            CustomerRiskProfileRequest request) {

        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer not found: " + customerId
            );
        }

        if (customerRiskProfileRepository
                .existsByCustomerIdAndDeletedAtIsNull(customerId)) {
            throw new DuplicateRecordException(
                    "Customer risk profile already exists"
            );
        }

        CustomerRiskProfile profile =
                customerRiskProfileMapper.toEntity(request);

        profile.setCustomerId(customerId);

        if (profile.getCurrentRiskScore() == null) {
            profile.setCurrentRiskScore(BigDecimal.ZERO);
        }

        if (profile.getBehaviorScore() == null) {
            profile.setBehaviorScore(BigDecimal.ZERO);
        }

        if (profile.getFraudScore() == null) {
            profile.setFraudScore(BigDecimal.ZERO);
        }

        if (profile.getAmlScore() == null) {
            profile.setAmlScore(BigDecimal.ZERO);
        }

        if (profile.getKycScore() == null) {
            profile.setKycScore(BigDecimal.ZERO);
        }

        if (profile.getDeviceScore() == null) {
            profile.setDeviceScore(BigDecimal.ZERO);
        }

        if (profile.getSanctionsScore() == null) {
            profile.setSanctionsScore(BigDecimal.ZERO);
        }

        if (profile.getPepScore() == null) {
            profile.setPepScore(BigDecimal.ZERO);
        }

        if (profile.getWatchlistScore() == null) {
            profile.setWatchlistScore(BigDecimal.ZERO);
        }

        LocalDateTime now = LocalDateTime.now();

        profile.setLastCalculation(now);
        profile.setCreatedAt(now);
        profile.setUpdatedAt(now);

        CustomerRiskProfile savedProfile =
                customerRiskProfileRepository.save(profile);

        return customerRiskProfileMapper.toResponse(savedProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerRiskProfileResponse getRiskProfileByCustomerId(
            UUID customerId) {

        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer not found: " + customerId
            );
        }

        CustomerRiskProfile profile =
                customerRiskProfileRepository
                        .findByCustomerIdAndDeletedAtIsNull(customerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer risk profile not found: "
                                                + customerId
                                )
                        );

        return customerRiskProfileMapper.toResponse(profile);
    }

    @Override
    @Transactional
    public CustomerRiskProfileResponse updateRiskProfile(
            UUID customerId,
            CustomerRiskProfileRequest request) {

        CustomerRiskProfile profile =
                customerRiskProfileRepository
                        .findByCustomerIdAndDeletedAtIsNull(customerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer risk profile not found: "
                                                + customerId
                                )
                        );

        customerRiskProfileMapper.updateEntity(request, profile);

        if (profile.getCurrentRiskScore() == null) {
            profile.setCurrentRiskScore(BigDecimal.ZERO);
        }

        if (profile.getBehaviorScore() == null) {
            profile.setBehaviorScore(BigDecimal.ZERO);
        }

        if (profile.getFraudScore() == null) {
            profile.setFraudScore(BigDecimal.ZERO);
        }

        if (profile.getAmlScore() == null) {
            profile.setAmlScore(BigDecimal.ZERO);
        }

        if (profile.getKycScore() == null) {
            profile.setKycScore(BigDecimal.ZERO);
        }

        if (profile.getDeviceScore() == null) {
            profile.setDeviceScore(BigDecimal.ZERO);
        }

        if (profile.getSanctionsScore() == null) {
            profile.setSanctionsScore(BigDecimal.ZERO);
        }

        if (profile.getPepScore() == null) {
            profile.setPepScore(BigDecimal.ZERO);
        }

        if (profile.getWatchlistScore() == null) {
            profile.setWatchlistScore(BigDecimal.ZERO);
        }

        LocalDateTime now = LocalDateTime.now();

        profile.setLastCalculation(now);
        profile.setUpdatedAt(now);

        CustomerRiskProfile savedProfile =
                customerRiskProfileRepository.save(profile);

        return customerRiskProfileMapper.toResponse(savedProfile);
    }

    @Override
    @Transactional
    public void deleteRiskProfile(UUID customerId) {

        CustomerRiskProfile profile =
                customerRiskProfileRepository
                        .findByCustomerIdAndDeletedAtIsNull(customerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer risk profile not found: "
                                                + customerId
                                )
                        );

        LocalDateTime now = LocalDateTime.now();

        profile.setDeletedAt(now);
        profile.setUpdatedAt(now);

        customerRiskProfileRepository.save(profile);
    }
}