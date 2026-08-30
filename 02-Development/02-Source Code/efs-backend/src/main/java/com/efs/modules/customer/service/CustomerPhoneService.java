package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerPhoneRequest;
import com.efs.modules.customer.dto.CustomerPhoneResponse;
import com.efs.modules.customer.entity.CustomerPhone;
import com.efs.modules.customer.mapper.CustomerPhoneMapper;
import com.efs.modules.customer.repository.CustomerPhoneRepository;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerPhoneService
        implements CustomerPhoneServiceInterface {

    private final CustomerPhoneRepository customerPhoneRepository;
    private final CustomerRepository customerRepository;
    private final CustomerPhoneMapper customerPhoneMapper;

    public CustomerPhoneService(
            CustomerPhoneRepository customerPhoneRepository,
            CustomerRepository customerRepository,
            CustomerPhoneMapper customerPhoneMapper) {

        this.customerPhoneRepository = customerPhoneRepository;
        this.customerRepository = customerRepository;
        this.customerPhoneMapper = customerPhoneMapper;
    }

    @Override
    @Transactional
    public CustomerPhoneResponse createPhone(
            UUID customerId,
            CustomerPhoneRequest request) {

        customerRepository
                .findByCustomerIdAndDeletedAtIsNull(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found: " + customerId
                        )
                );

        CustomerPhone phone =
                customerPhoneMapper.toEntity(request);

        phone.setCustomerId(customerId);

        if (phone.getPrimary() == null) {
            phone.setPrimary(Boolean.FALSE);
        }

        if (phone.getVerified() == null) {
            phone.setVerified(Boolean.FALSE);
        }

        if (Boolean.TRUE.equals(phone.getVerified())) {
            phone.setVerifiedAt(LocalDateTime.now());
        }

        LocalDateTime now =
                LocalDateTime.now();

        phone.setCreatedAt(now);
        phone.setUpdatedAt(now);

        CustomerPhone savedPhone =
                customerPhoneRepository.save(phone);

        return customerPhoneMapper.toResponse(savedPhone);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerPhoneResponse getPhoneById(
            UUID customerPhoneId) {

        CustomerPhone phone =
                customerPhoneRepository
                        .findByCustomerPhoneIdAndDeletedAtIsNull(
                                customerPhoneId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer phone not found: "
                                                + customerPhoneId
                                )
                        );

        return customerPhoneMapper.toResponse(phone);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerPhoneResponse> getPhonesByCustomerId(
            UUID customerId) {

        customerRepository
                .findByCustomerIdAndDeletedAtIsNull(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found: " + customerId
                        )
                );

        return customerPhoneRepository
                .findByCustomerIdAndDeletedAtIsNull(customerId)
                .stream()
                .map(customerPhoneMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CustomerPhoneResponse updatePhone(
            UUID customerPhoneId,
            CustomerPhoneRequest request) {

        CustomerPhone phone =
                customerPhoneRepository
                        .findByCustomerPhoneIdAndDeletedAtIsNull(
                                customerPhoneId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer phone not found: "
                                                + customerPhoneId
                                )
                        );

        Boolean previouslyVerified =
                phone.getVerified();

        customerPhoneMapper.updateEntity(
                request,
                phone
        );

        if (phone.getPrimary() == null) {
            phone.setPrimary(Boolean.FALSE);
        }

        if (phone.getVerified() == null) {
            phone.setVerified(Boolean.FALSE);
        }

        if (Boolean.TRUE.equals(phone.getVerified())
                && !Boolean.TRUE.equals(previouslyVerified)) {

            phone.setVerifiedAt(LocalDateTime.now());
        }

        if (!Boolean.TRUE.equals(phone.getVerified())) {
            phone.setVerifiedAt(null);
        }

        phone.setUpdatedAt(
                LocalDateTime.now()
        );

        CustomerPhone savedPhone =
                customerPhoneRepository.save(phone);

        return customerPhoneMapper.toResponse(savedPhone);
    }

    @Override
    @Transactional
    public void deletePhone(
            UUID customerPhoneId,
            UUID deletedBy) {

        CustomerPhone phone =
                customerPhoneRepository
                        .findByCustomerPhoneIdAndDeletedAtIsNull(
                                customerPhoneId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer phone not found: "
                                                + customerPhoneId
                                )
                        );

        LocalDateTime now =
                LocalDateTime.now();

        phone.setDeletedAt(now);
        phone.setDeletedBy(deletedBy);
        phone.setUpdatedAt(now);

        customerPhoneRepository.save(phone);
    }
}