package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionLocationRequest;
import com.efs.modules.transaction.dto.TransactionLocationResponse;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.entity.TransactionLocation;
import com.efs.modules.transaction.mapper.TransactionLocationMapper;
import com.efs.modules.transaction.repository.TransactionLocationRepository;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.modules.transaction.validator.TransactionLocationValueValidator;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

        if (transactionRepository
                .findByTransactionIdAndDeletedAtIsNull(
                        location.getTransactionId()
                )
                .isEmpty()) {

            throw new ResourceNotFoundException(
                    "Transaction location not found: "
                            + locationId
            );
        }

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

        return toActiveLocationResponses(
                transactionLocationRepository
                        .findByIpAddress(
                                TransactionLocationValueValidator
                                        .parseLiteralIpAddress(
                                                ipAddress
                                        )
                        )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionLocationResponse>
    getLocationsByCountryCode(
            String countryCode) {

        String normalizedCountryCode =
                TransactionLocationValueValidator
                        .normalizeCountryCode(countryCode);

        return toActiveLocationResponses(
                transactionLocationRepository
                        .findByCountryCode(
                                normalizedCountryCode
                        )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionLocationResponse>
    getLocationsByAsn(
            Long asn) {

        return toActiveLocationResponses(
                transactionLocationRepository
                        .findByAsn(asn)
        );
    }

    private List<TransactionLocationResponse>
    toActiveLocationResponses(
            List<TransactionLocation> locations) {

        if (locations.isEmpty()) {
            return List.of();
        }

        Set<UUID> transactionIds =
                locations.stream()
                        .map(
                                TransactionLocation
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

        return locations.stream()
                .filter(location ->
                        activeTransactionIds.contains(
                                location.getTransactionId()
                        )
                )
                .map(transactionLocationMapper::toResponse)
                .toList();
    }
}