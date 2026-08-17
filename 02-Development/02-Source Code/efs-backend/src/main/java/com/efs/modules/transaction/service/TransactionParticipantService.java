package com.efs.modules.transaction.service;

import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.transaction.dto.TransactionParticipantRequest;
import com.efs.modules.transaction.dto.TransactionParticipantResponse;
import com.efs.modules.transaction.entity.TransactionParticipant;
import com.efs.modules.transaction.mapper.TransactionParticipantMapper;
import com.efs.modules.transaction.repository.TransactionParticipantRepository;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionParticipantService
        implements TransactionParticipantServiceInterface {

    private final TransactionParticipantRepository transactionParticipantRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final TransactionParticipantMapper transactionParticipantMapper;

    public TransactionParticipantService(
            TransactionParticipantRepository transactionParticipantRepository,
            TransactionRepository transactionRepository,
            CustomerRepository customerRepository,
            TransactionParticipantMapper transactionParticipantMapper) {

        this.transactionParticipantRepository =
                transactionParticipantRepository;

        this.transactionRepository =
                transactionRepository;

        this.customerRepository =
                customerRepository;

        this.transactionParticipantMapper =
                transactionParticipantMapper;
    }

    @Override
    @Transactional
    public TransactionParticipantResponse createParticipant(
            UUID transactionId,
            TransactionParticipantRequest request) {

        transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found: " + transactionId
                        )
                );

        if (request.getCustomerId() != null
                && !customerRepository.existsById(
                        request.getCustomerId()
                )) {

            throw new ResourceNotFoundException(
                    "Customer not found: "
                            + request.getCustomerId()
            );
        }

        TransactionParticipant participant =
                transactionParticipantMapper.toEntity(request);

        participant.setTransactionId(transactionId);
        participant.setCreatedAt(LocalDateTime.now());

        TransactionParticipant savedParticipant =
                transactionParticipantRepository.save(participant);

        return transactionParticipantMapper.toResponse(
                savedParticipant
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionParticipantResponse getParticipantById(
            UUID participantId) {

        TransactionParticipant participant =
                transactionParticipantRepository
                        .findByParticipantId(participantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction participant not found: "
                                                + participantId
                                )
                        );

        return transactionParticipantMapper.toResponse(
                participant
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionParticipantResponse>
    getParticipantsByTransactionId(
            UUID transactionId) {

        transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found: " + transactionId
                        )
                );

        return transactionParticipantRepository
                .findByTransactionId(transactionId)
                .stream()
                .map(transactionParticipantMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionParticipantResponse>
    getParticipantsByCustomerId(
            UUID customerId) {

        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer not found: " + customerId
            );
        }

        return transactionParticipantRepository
                .findByCustomerId(customerId)
                .stream()
                .map(transactionParticipantMapper::toResponse)
                .toList();
    }
}