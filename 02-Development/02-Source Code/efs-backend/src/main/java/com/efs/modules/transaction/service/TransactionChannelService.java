package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionChannelRequest;
import com.efs.modules.transaction.dto.TransactionChannelResponse;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.entity.TransactionChannel;
import com.efs.modules.transaction.mapper.TransactionChannelMapper;
import com.efs.modules.transaction.repository.TransactionChannelRepository;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.modules.transaction.validator.TransactionChannelValueValidator;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionChannelService
        implements TransactionChannelServiceInterface {

    private final TransactionChannelRepository transactionChannelRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionChannelMapper transactionChannelMapper;
    private final TransactionChannelValueValidator
            transactionChannelValueValidator;

    public TransactionChannelService(
            TransactionChannelRepository transactionChannelRepository,
            TransactionRepository transactionRepository,
            TransactionChannelMapper transactionChannelMapper,
            TransactionChannelValueValidator
                    transactionChannelValueValidator) {

        this.transactionChannelRepository =
                transactionChannelRepository;

        this.transactionRepository =
                transactionRepository;

        this.transactionChannelMapper =
                transactionChannelMapper;
        this.transactionChannelValueValidator =
                transactionChannelValueValidator;
    }

    @Override
    @Transactional
    public TransactionChannelResponse createChannel(
            UUID transactionId,
            TransactionChannelRequest request) {

        transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found: " + transactionId
                        )
                );

        transactionChannelValueValidator
                .validate(request);

        TransactionChannel channel =
                transactionChannelMapper.toEntity(request);

        channel.setTransactionId(transactionId);
        channel.setCreatedAt(LocalDateTime.now());

        TransactionChannel savedChannel =
                transactionChannelRepository.save(channel);

        return transactionChannelMapper.toResponse(savedChannel);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionChannelResponse getChannelById(
            UUID channelTransactionId) {

        TransactionChannel channel =
                transactionChannelRepository
                        .findByChannelTransactionId(channelTransactionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction channel not found: "
                                                + channelTransactionId
                                )
                        );

        if (transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(
                        channel.getTransactionId()
                )
                .isEmpty()) {

            throw new ResourceNotFoundException(
                    "Transaction channel not found: "
                            + channelTransactionId
            );
        }

        return transactionChannelMapper.toResponse(channel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionChannelResponse> getChannelsByTransactionId(
            UUID transactionId) {

        transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found: " + transactionId
                        )
                );

        return transactionChannelRepository
                .findByTransactionId(transactionId)
                .stream()
                .map(transactionChannelMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionChannelResponse> getChannelsByType(
            String channelType) {

        return toActiveChannelResponses(
                transactionChannelRepository
                        .findByChannelType(channelType)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionChannelResponse> getChannelsByApplicationName(
            String applicationName) {

        return toActiveChannelResponses(
                transactionChannelRepository
                        .findByApplicationName(applicationName)
        );
    }

    private List<TransactionChannelResponse>
    toActiveChannelResponses(
            List<TransactionChannel> channels) {

        if (channels.isEmpty()) {
            return List.of();
        }

        Set<UUID> transactionIds =
                channels.stream()
                        .map(TransactionChannel::getTransactionId)
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

        return channels.stream()
                .filter(channel ->
                        activeTransactionIds.contains(
                                channel.getTransactionId()
                        )
                )
                .map(transactionChannelMapper::toResponse)
                .toList();
    }
}