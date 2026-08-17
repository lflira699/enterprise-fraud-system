package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerBiometricRequest;
import com.efs.modules.customer.dto.CustomerBiometricResponse;
import com.efs.modules.customer.entity.CustomerBiometric;
import com.efs.modules.customer.mapper.CustomerBiometricMapper;
import com.efs.modules.customer.repository.CustomerBiometricRepository;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerBiometricService
        implements CustomerBiometricServiceInterface {

    private final CustomerBiometricRepository customerBiometricRepository;
    private final CustomerRepository customerRepository;
    private final CustomerBiometricMapper customerBiometricMapper;

    public CustomerBiometricService(
            CustomerBiometricRepository customerBiometricRepository,
            CustomerRepository customerRepository,
            CustomerBiometricMapper customerBiometricMapper) {

        this.customerBiometricRepository = customerBiometricRepository;
        this.customerRepository = customerRepository;
        this.customerBiometricMapper = customerBiometricMapper;
    }

    @Override
    @Transactional
    public CustomerBiometricResponse createBiometric(
            UUID customerId,
            CustomerBiometricRequest request) {

        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer not found: " + customerId
            );
        }

        CustomerBiometric biometric =
                customerBiometricMapper.toEntity(request);

        biometric.setCustomerId(customerId);

        if (biometric.getActive() == null) {
            biometric.setActive(Boolean.TRUE);
        }

        LocalDateTime now = LocalDateTime.now();

        biometric.setCreatedAt(now);
        biometric.setUpdatedAt(now);

        CustomerBiometric savedBiometric =
                customerBiometricRepository.save(biometric);

        return customerBiometricMapper.toResponse(savedBiometric);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerBiometricResponse getBiometricById(
            UUID biometricId) {

        CustomerBiometric biometric =
                customerBiometricRepository
                        .findByBiometricIdAndDeletedAtIsNull(
                                biometricId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer biometric not found: "
                                                + biometricId
                                )
                        );

        return customerBiometricMapper.toResponse(biometric);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerBiometricResponse> getBiometricsByCustomerId(
            UUID customerId) {

        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer not found: " + customerId
            );
        }

        return customerBiometricRepository
                .findByCustomerIdAndDeletedAtIsNull(customerId)
                .stream()
                .map(customerBiometricMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CustomerBiometricResponse updateBiometric(
            UUID biometricId,
            CustomerBiometricRequest request) {

        CustomerBiometric biometric =
                customerBiometricRepository
                        .findByBiometricIdAndDeletedAtIsNull(
                                biometricId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer biometric not found: "
                                                + biometricId
                                )
                        );

        customerBiometricMapper.updateEntity(
                request,
                biometric
        );

        if (biometric.getActive() == null) {
            biometric.setActive(Boolean.TRUE);
        }

        biometric.setUpdatedAt(LocalDateTime.now());

        CustomerBiometric savedBiometric =
                customerBiometricRepository.save(biometric);

        return customerBiometricMapper.toResponse(savedBiometric);
    }

    @Override
    @Transactional
    public void deleteBiometric(UUID biometricId) {

        CustomerBiometric biometric =
                customerBiometricRepository
                        .findByBiometricIdAndDeletedAtIsNull(
                                biometricId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer biometric not found: "
                                                + biometricId
                                )
                        );

        LocalDateTime now = LocalDateTime.now();

        biometric.setDeletedAt(now);
        biometric.setUpdatedAt(now);
        biometric.setActive(Boolean.FALSE);

        customerBiometricRepository.save(biometric);
    }
}