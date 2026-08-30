package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerDocumentRequest;
import com.efs.modules.customer.dto.CustomerDocumentResponse;
import com.efs.modules.customer.entity.CustomerDocument;
import com.efs.modules.customer.mapper.CustomerDocumentMapper;
import com.efs.modules.customer.repository.CustomerDocumentRepository;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.shared.exception.DuplicateRecordException;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerDocumentService
        implements CustomerDocumentServiceInterface {

    private final CustomerDocumentRepository customerDocumentRepository;
    private final CustomerRepository customerRepository;
    private final CustomerDocumentMapper customerDocumentMapper;

    public CustomerDocumentService(
            CustomerDocumentRepository customerDocumentRepository,
            CustomerRepository customerRepository,
            CustomerDocumentMapper customerDocumentMapper) {

        this.customerDocumentRepository = customerDocumentRepository;
        this.customerRepository = customerRepository;
        this.customerDocumentMapper = customerDocumentMapper;
    }

    @Override
    @Transactional
    public CustomerDocumentResponse createDocument(
            UUID customerId,
            CustomerDocumentRequest request) {

        customerRepository
                .findByCustomerIdAndDeletedAtIsNull(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found: " + customerId
                        )
                );

        if (customerDocumentRepository
                .existsByCustomerIdAndDocumentTypeAndDocumentNumber(
                        customerId,
                        request.getDocumentType(),
                        request.getDocumentNumber())) {

            throw new DuplicateRecordException(
                    "Customer document already exists"
            );
        }

        CustomerDocument document =
                customerDocumentMapper.toEntity(request);

        document.setCustomerId(customerId);

        LocalDateTime now =
                LocalDateTime.now();

        document.setCreatedAt(now);
        document.setUpdatedAt(now);

        if (request.getVerificationStatus() != null
                && request.getVerifiedBy() != null) {

            document.setVerifiedAt(now);
        }

        CustomerDocument savedDocument =
                customerDocumentRepository.save(document);

        return customerDocumentMapper.toResponse(savedDocument);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDocumentResponse getDocumentById(
            UUID documentId) {

        CustomerDocument document =
                customerDocumentRepository
                        .findById(documentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer document not found: "
                                                + documentId
                                )
                        );

        return customerDocumentMapper.toResponse(document);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDocumentResponse> getDocumentsByCustomerId(
            UUID customerId) {

        customerRepository
                .findByCustomerIdAndDeletedAtIsNull(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found: " + customerId
                        )
                );

        return customerDocumentRepository
                .findByCustomerId(customerId)
                .stream()
                .map(customerDocumentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CustomerDocumentResponse updateDocument(
            UUID documentId,
            CustomerDocumentRequest request) {

        CustomerDocument document =
                customerDocumentRepository
                        .findById(documentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer document not found: "
                                                + documentId
                                )
                        );

        boolean duplicateExists =
                customerDocumentRepository
                        .existsByCustomerIdAndDocumentTypeAndDocumentNumber(
                                document.getCustomerId(),
                                request.getDocumentType(),
                                request.getDocumentNumber()
                        );

        if (duplicateExists
                && !(document.getDocumentType()
                .equals(request.getDocumentType())
                && document.getDocumentNumber()
                .equals(request.getDocumentNumber()))) {

            throw new DuplicateRecordException(
                    "Customer document already exists"
            );
        }

        customerDocumentMapper.updateEntity(
                request,
                document
        );

        LocalDateTime now =
                LocalDateTime.now();

        document.setUpdatedAt(now);

        if (request.getVerificationStatus() != null
                && request.getVerifiedBy() != null
                && document.getVerifiedAt() == null) {

            document.setVerifiedAt(now);
        }

        CustomerDocument savedDocument =
                customerDocumentRepository.save(document);

        return customerDocumentMapper.toResponse(savedDocument);
    }

    @Override
    @Transactional
    public void deleteDocument(
            UUID documentId) {

        CustomerDocument document =
                customerDocumentRepository
                        .findById(documentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer document not found: "
                                                + documentId
                                )
                        );

        customerDocumentRepository.delete(document);
    }
}