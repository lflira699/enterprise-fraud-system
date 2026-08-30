package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerRelationshipRequest;
import com.efs.modules.customer.dto.CustomerRelationshipResponse;
import com.efs.modules.customer.entity.CustomerRelationship;
import com.efs.modules.customer.mapper.CustomerRelationshipMapper;
import com.efs.modules.customer.repository.CustomerRelationshipRepository;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import com.efs.shared.exception.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerRelationshipService
        implements CustomerRelationshipServiceInterface {

    private final CustomerRelationshipRepository customerRelationshipRepository;
    private final CustomerRepository customerRepository;
    private final CustomerRelationshipMapper customerRelationshipMapper;

    public CustomerRelationshipService(
            CustomerRelationshipRepository customerRelationshipRepository,
            CustomerRepository customerRepository,
            CustomerRelationshipMapper customerRelationshipMapper) {

        this.customerRelationshipRepository = customerRelationshipRepository;
        this.customerRepository = customerRepository;
        this.customerRelationshipMapper = customerRelationshipMapper;
    }

    @Override
    @Transactional
    public CustomerRelationshipResponse createRelationship(
            UUID customerId,
            CustomerRelationshipRequest request) {

        customerRepository
                .findByCustomerIdAndDeletedAtIsNull(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found: " + customerId
                        )
                );

        validateRelationship(
                customerId,
                request
        );

        CustomerRelationship relationship =
                customerRelationshipMapper.toEntity(request);

        relationship.setCustomerId(customerId);

        LocalDateTime now =
                LocalDateTime.now();

        relationship.setCreatedAt(now);
        relationship.setUpdatedAt(now);

        CustomerRelationship savedRelationship =
                customerRelationshipRepository.save(relationship);

        return customerRelationshipMapper.toResponse(
                savedRelationship
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerRelationshipResponse getRelationshipById(
            UUID customerRelationshipId) {

        CustomerRelationship relationship =
                customerRelationshipRepository
                        .findByCustomerRelationshipIdAndDeletedAtIsNull(
                                customerRelationshipId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer relationship not found: "
                                                + customerRelationshipId
                                )
                        );

        return customerRelationshipMapper.toResponse(
                relationship
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerRelationshipResponse> getRelationshipsByCustomerId(
            UUID customerId) {

        customerRepository
                .findByCustomerIdAndDeletedAtIsNull(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found: " + customerId
                        )
                );

        return customerRelationshipRepository
                .findByCustomerIdAndDeletedAtIsNull(customerId)
                .stream()
                .map(customerRelationshipMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CustomerRelationshipResponse updateRelationship(
            UUID customerRelationshipId,
            CustomerRelationshipRequest request) {

        CustomerRelationship relationship =
                customerRelationshipRepository
                        .findByCustomerRelationshipIdAndDeletedAtIsNull(
                                customerRelationshipId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer relationship not found: "
                                                + customerRelationshipId
                                )
                        );

        validateRelationship(
                relationship.getCustomerId(),
                request
        );

        customerRelationshipMapper.updateEntity(
                request,
                relationship
        );

        relationship.setUpdatedAt(
                LocalDateTime.now()
        );

        CustomerRelationship savedRelationship =
                customerRelationshipRepository.save(relationship);

        return customerRelationshipMapper.toResponse(
                savedRelationship
        );
    }

    @Override
    @Transactional
    public void deleteRelationship(
            UUID customerRelationshipId,
            UUID deletedBy) {

        CustomerRelationship relationship =
                customerRelationshipRepository
                        .findByCustomerRelationshipIdAndDeletedAtIsNull(
                                customerRelationshipId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer relationship not found: "
                                                + customerRelationshipId
                                )
                        );

        LocalDateTime now =
                LocalDateTime.now();

        relationship.setDeletedAt(now);
        relationship.setDeletedBy(deletedBy);
        relationship.setUpdatedAt(now);

        customerRelationshipRepository.save(
                relationship
        );
    }

    private void validateRelationship(
            UUID customerId,
            CustomerRelationshipRequest request) {

        if (request.getRelatedCustomerId() != null) {

            if (customerId.equals(
                    request.getRelatedCustomerId())) {

                throw new ValidationException(
                        "A customer cannot be related to itself"
                );
            }

            customerRepository
                    .findByCustomerIdAndDeletedAtIsNull(
                            request.getRelatedCustomerId()
                    )
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Related customer not found: "
                                            + request.getRelatedCustomerId()
                            )
                    );
        }

        if (request.getEffectiveDate() != null
                && request.getExpirationDate() != null
                && request.getExpirationDate()
                .isBefore(request.getEffectiveDate())) {

            throw new ValidationException(
                    "Expiration date cannot be before effective date"
            );
        }
    }
}