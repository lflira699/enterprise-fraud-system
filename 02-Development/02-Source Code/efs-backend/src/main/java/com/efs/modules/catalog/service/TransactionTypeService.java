package com.efs.modules.catalog.service;

import com.efs.modules.catalog.dto.TransactionTypeRequest;
import com.efs.modules.catalog.dto.TransactionTypeResponse;
import com.efs.modules.catalog.entity.TransactionType;
import com.efs.modules.catalog.mapper.TransactionTypeMapper;
import com.efs.modules.catalog.repository.TransactionTypeRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TransactionTypeService
        implements TransactionTypeServiceInterface {

    private final TransactionTypeRepository transactionTypeRepository;
    private final TransactionTypeMapper transactionTypeMapper;

    public TransactionTypeService(
            TransactionTypeRepository transactionTypeRepository,
            TransactionTypeMapper transactionTypeMapper) {

        this.transactionTypeRepository =
                transactionTypeRepository;

        this.transactionTypeMapper =
                transactionTypeMapper;
    }

    @Override
    public TransactionTypeResponse createTransactionType(
            TransactionTypeRequest request) {

        TransactionType transactionType =
                transactionTypeMapper.toEntity(
                        request
                );

        TransactionType savedTransactionType =
                transactionTypeRepository.save(
                        transactionType
                );

        return transactionTypeMapper.toResponse(
                savedTransactionType
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionTypeResponse getTransactionTypeById(
            UUID transactionTypeId) {

        TransactionType transactionType =
                transactionTypeRepository
                        .findById(transactionTypeId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Transaction type not found: "
                                                        + transactionTypeId
                                        )
                        );

        return transactionTypeMapper.toResponse(
                transactionType
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionTypeResponse getTransactionTypeByCode(
            String transactionTypeCode) {

        TransactionType transactionType =
                transactionTypeRepository
                        .findByTransactionTypeCode(
                                transactionTypeCode
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Transaction type not found for code: "
                                                        + transactionTypeCode
                                        )
                        );

        return transactionTypeMapper.toResponse(
                transactionType
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionTypeResponse> getTransactionTypesByStatus(
            String status) {

        return transactionTypeRepository
                .findByStatusOrderByDisplayOrderAsc(
                        status
                )
                .stream()
                .map(transactionTypeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionTypeResponse> getAllTransactionTypes() {

        return transactionTypeRepository
                .findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(transactionTypeMapper::toResponse)
                .toList();
    }
}