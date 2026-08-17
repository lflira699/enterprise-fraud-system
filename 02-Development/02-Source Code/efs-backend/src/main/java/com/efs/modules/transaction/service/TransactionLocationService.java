package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionLocationRequest;
import com.efs.modules.transaction.dto.TransactionLocationResponse;
import com.efs.modules.transaction.entity.TransactionLocation;
import com.efs.modules.transaction.mapper.TransactionLocationMapper;
import com.efs.modules.transaction.repository.TransactionLocationRepository;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionLocationService
        implements TransactionLocationServiceInterface {

    private final TransactionLocationRepository transactionLocationRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionLocationMapper transactionLocationMapper;

    public TransactionLocationService(
            TransactionLocationRepository transactionLocationRepository,
            TransactionRepository transactionRepository,
            TransactionLocationMapper transactionLocationMapper) {

        this.transactionLocationRepository =
                transactionLocationRepository;

        this.transactionRepository =
                transactionRepository;

        this.transactionLocationMapper =
                transactionLocationMapper;
    }

    @Override
    @Transactional
    public TransactionLocationResponse createLocation(
            UUID transactionId,
            TransactionLocationRequest request) {

        transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found: " + transactionId
                        )
                );

        TransactionLocation location =
                transactionLocationMapper.toEntity(request);

        location.setTransactionId(transactionId);

        if (location.getVpnDetected() == null) {
            location.setVpnDetected(Boolean.FALSE);
        }

        if (location.getProxyDetected() == null) {
            location.setProxyDetected(Boolean.FALSE);
        }

        if (location.getTorDetected() == null) {
            location.setTorDetected(Boolean.FALSE);
        }

        location.setCreatedAt(LocalDateTime.now());

        TransactionLocation savedLocation =
                transactionLocationRepository.save(location);

        return transactionLocationMapper.toResponse(savedLocation);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionLocationResponse getLocationById(
            UUID locationId) {

        TransactionLocation location =
                transactionLocationRepository
                        .findByLocationId(locationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction location not found: "
                                                + locationId
                                )
                        );

        return transactionLocationMapper.toResponse(location);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionLocationResponse>
    getLocationsByTransactionId(
            UUID transactionId) {

        transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(transactionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found: " + transactionId
                        )
                );

        return transactionLocationRepository
                .findByTransactionId(transactionId)
                .stream()
                .map(transactionLocationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionLocationResponse>
    getLocationsByIpAddress(
            String ipAddress) {

        try {
            InetAddress address =
                    InetAddress.getByName(ipAddress);

            return transactionLocationRepository
                    .findByIpAddress(address)
                    .stream()
                    .map(transactionLocationMapper::toResponse)
                    .toList();

        } catch (UnknownHostException exception) {
            throw new IllegalArgumentException(
                    "Invalid IP address: " + ipAddress,
                    exception
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionLocationResponse>
    getLocationsByCountryCode(
            String countryCode) {

        return transactionLocationRepository
                .findByCountryCode(countryCode)
                .stream()
                .map(transactionLocationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionLocationResponse>
    getLocationsByAsn(
            Long asn) {

        return transactionLocationRepository
                .findByAsn(asn)
                .stream()
                .map(transactionLocationMapper::toResponse)
                .toList();
    }
}