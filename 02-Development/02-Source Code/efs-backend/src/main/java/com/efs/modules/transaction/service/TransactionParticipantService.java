package com.efs.modules.transaction.service;

import com.efs.modules.transaction.port.out.CustomerExistencePort;
import com.efs.modules.transaction.dto.TransactionParticipantRequest;
import com.efs.modules.transaction.dto.TransactionParticipantResponse;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.entity.TransactionParticipant;
import com.efs.modules.transaction.mapper.TransactionParticipantMapper;
import com.efs.modules.transaction.repository.TransactionParticipantRepository;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionParticipantService
        implements TransactionParticipantServiceInterface {

    private final TransactionParticipantRepository transactionParticipantRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerExistencePort customerExistencePort;
    private final TransactionParticipantMapper transactionParticipantMapper;

    public TransactionParticipantService(
            TransactionParticipantRepository transactionParticipantRepository,
            TransactionRepository transactionRepository,
            CustomerExistencePort customerExistencePort,
            TransactionParticipantMapper transactionParticipantMapper) {

        this.transactionParticipantRepository =
                transactionParticipantRepository;

        this.transactionRepository =
                transactionRepository;

        this.customerExistencePort =
                customerExistencePort;

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
                && !customerExistencePort.exists(
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

        if (transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(
                        participant.getTransactionId()
                )
                .isEmpty()) {

            throw new ResourceNotFoundException(
                    "Transaction participant not found: "
                            + participantId
            );
        }

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

        if (!customerExistencePort.exists(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer not found: " + customerId
            );
        }

        return toActiveParticipantResponses(
                transactionParticipantRepository
                        .findByCustomerId(customerId)
        );
    }

    private List<TransactionParticipantResponse>
    toActiveParticipantResponses(
            List<TransactionParticipant> participants) {

        if (participants.isEmpty()) {
            return List.of();
        }

        Set<UUID> transactionIds =
                participants.stream()
                        .map(
                                TransactionParticipant
                                        ::getTransactionId
                        )
                        .collect(Collectors.toSet());

        Set<UUID> activeTransactionIds =
                transactionRepository
                        .findAllById(transactionIds)
                        .stream()
                        .filter(transaction ->
                                transaction.getDeletedAt() == null
                        )
                        .map(Transaction::getTransactionId)
                        .collect(Collectors.toSet());

        return participants.stream()
                .filter(participant ->
                        activeTransactionIds.contains(
                                participant.getTransactionId()
                        )
                )
                .map(transactionParticipantMapper::toResponse)
                .toList();
    }
}