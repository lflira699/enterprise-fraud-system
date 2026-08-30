package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerEmailRequest;
import com.efs.modules.customer.dto.CustomerEmailResponse;
import com.efs.modules.customer.entity.CustomerEmail;
import com.efs.modules.customer.mapper.CustomerEmailMapper;
import com.efs.modules.customer.repository.CustomerEmailRepository;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerEmailService
        implements CustomerEmailServiceInterface {

    private final CustomerEmailRepository customerEmailRepository;
    private final CustomerRepository customerRepository;
    private final CustomerEmailMapper customerEmailMapper;

    public CustomerEmailService(
            CustomerEmailRepository customerEmailRepository,
            CustomerRepository customerRepository,
            CustomerEmailMapper customerEmailMapper) {

        this.customerEmailRepository = customerEmailRepository;
        this.customerRepository = customerRepository;
        this.customerEmailMapper = customerEmailMapper;
    }

    @Override
    @Transactional
    public CustomerEmailResponse createEmail(
            UUID customerId,
            CustomerEmailRequest request) {

        customerRepository
                .findByCustomerIdAndDeletedAtIsNull(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found: " + customerId
                        )
                );

        CustomerEmail email =
                customerEmailMapper.toEntity(request);

        email.setCustomerId(customerId);

        if (email.getPrimary() == null) {
            email.setPrimary(Boolean.FALSE);
        }

        if (email.getVerified() == null) {
            email.setVerified(Boolean.FALSE);
        }

        if (Boolean.TRUE.equals(email.getVerified())) {
            email.setVerifiedAt(LocalDateTime.now());
        }

        LocalDateTime now =
                LocalDateTime.now();

        email.setCreatedAt(now);
        email.setUpdatedAt(now);

        CustomerEmail savedEmail =
                customerEmailRepository.save(email);

        return customerEmailMapper.toResponse(savedEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerEmailResponse getEmailById(
            UUID customerEmailId) {

        CustomerEmail email =
                customerEmailRepository
                        .findByCustomerEmailIdAndDeletedAtIsNull(
                                customerEmailId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer email not found: "
                                                + customerEmailId
                                )
                        );

        return customerEmailMapper.toResponse(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerEmailResponse> getEmailsByCustomerId(
            UUID customerId) {

        customerRepository
                .findByCustomerIdAndDeletedAtIsNull(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found: " + customerId
                        )
                );

        return customerEmailRepository
                .findByCustomerIdAndDeletedAtIsNull(customerId)
                .stream()
                .map(customerEmailMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CustomerEmailResponse updateEmail(
            UUID customerEmailId,
            CustomerEmailRequest request) {

        CustomerEmail email =
                customerEmailRepository
                        .findByCustomerEmailIdAndDeletedAtIsNull(
                                customerEmailId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer email not found: "
                                                + customerEmailId
                                )
                        );

        Boolean previouslyVerified =
                email.getVerified();

        customerEmailMapper.updateEntity(
                request,
                email
        );

        if (email.getPrimary() == null) {
            email.setPrimary(Boolean.FALSE);
        }

        if (email.getVerified() == null) {
            email.setVerified(Boolean.FALSE);
        }

        if (Boolean.TRUE.equals(email.getVerified())
                && !Boolean.TRUE.equals(previouslyVerified)) {

            email.setVerifiedAt(LocalDateTime.now());
        }

        if (!Boolean.TRUE.equals(email.getVerified())) {
            email.setVerifiedAt(null);
        }

        email.setUpdatedAt(
                LocalDateTime.now()
        );

        CustomerEmail savedEmail =
                customerEmailRepository.save(email);

        return customerEmailMapper.toResponse(savedEmail);
    }

    @Override
    @Transactional
    public void deleteEmail(
            UUID customerEmailId,
            UUID deletedBy) {

        CustomerEmail email =
                customerEmailRepository
                        .findByCustomerEmailIdAndDeletedAtIsNull(
                                customerEmailId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer email not found: "
                                                + customerEmailId
                                )
                        );

        LocalDateTime now =
                LocalDateTime.now();

        email.setDeletedAt(now);
        email.setDeletedBy(deletedBy);
        email.setUpdatedAt(now);

        customerEmailRepository.save(email);
    }
}