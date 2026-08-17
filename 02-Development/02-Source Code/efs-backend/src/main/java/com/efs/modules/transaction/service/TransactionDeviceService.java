package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionDeviceRequest;
import com.efs.modules.transaction.dto.TransactionDeviceResponse;
import com.efs.modules.transaction.entity.TransactionDevice;
import com.efs.modules.transaction.mapper.TransactionDeviceMapper;
import com.efs.modules.transaction.repository.TransactionDeviceRepository;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionDeviceService
        implements TransactionDeviceServiceInterface {

    private final TransactionDeviceRepository transactionDeviceRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionDeviceMapper transactionDeviceMapper;

    public TransactionDeviceService(
            TransactionDeviceRepository transactionDeviceRepository,
            TransactionRepository transactionRepository,
            TransactionDeviceMapper transactionDeviceMapper) {

        this.transactionDeviceRepository =
                transactionDeviceRepository;

        this.transactionRepository =
                transactionRepository;

        this.transactionDeviceMapper =
                transactionDeviceMapper;
    }

    @Override
    @Transactional
    public TransactionDeviceResponse createDevice(
            UUID transactionId,
            TransactionDeviceRequest request) {

        transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found: " + transactionId
                        )
                );

        TransactionDevice device =
                transactionDeviceMapper.toEntity(request);

        device.setTransactionId(transactionId);
        device.setCreatedAt(LocalDateTime.now());

        TransactionDevice savedDevice =
                transactionDeviceRepository.save(device);

        return transactionDeviceMapper.toResponse(savedDevice);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionDeviceResponse getDeviceById(
            UUID deviceTransactionId) {

        TransactionDevice device =
                transactionDeviceRepository
                        .findByDeviceTransactionId(deviceTransactionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction device not found: "
                                                + deviceTransactionId
                                )
                        );

        return transactionDeviceMapper.toResponse(device);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDeviceResponse> getDevicesByTransactionId(
            UUID transactionId) {

        transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found: " + transactionId
                        )
                );

        return transactionDeviceRepository
                .findByTransactionId(transactionId)
                .stream()
                .map(transactionDeviceMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDeviceResponse> getDevicesByFingerprint(
            String deviceFingerprint) {

        return transactionDeviceRepository
                .findByDeviceFingerprint(deviceFingerprint)
                .stream()
                .map(transactionDeviceMapper::toResponse)
                .toList();
    }
}