package com.efs.modules.detection.service;

import com.efs.modules.detection.dto.CorrelationRequest;
import com.efs.modules.detection.dto.CorrelationResponse;
import com.efs.modules.detection.entity.Correlation;
import com.efs.modules.detection.mapper.CorrelationMapper;
import com.efs.modules.detection.repository.CorrelationRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CorrelationService
        implements CorrelationServiceInterface {

    private final CorrelationRepository correlationRepository;
    private final CorrelationMapper correlationMapper;

    public CorrelationService(
            CorrelationRepository correlationRepository,
            CorrelationMapper correlationMapper) {

        this.correlationRepository = correlationRepository;
        this.correlationMapper = correlationMapper;
    }

    @Override
    @Transactional
    public CorrelationResponse createCorrelation(
            CorrelationRequest request) {

        Correlation correlation =
                correlationMapper.toEntity(request);

        LocalDateTime now = LocalDateTime.now();

        correlation.setCreatedAt(now);
        correlation.setUpdatedAt(now);

        if (correlation.getConfidence() == null) {
            correlation.setConfidence(BigDecimal.ZERO);
        }

        Correlation savedCorrelation =
                correlationRepository.save(correlation);

        return correlationMapper.toResponse(
                savedCorrelation
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CorrelationResponse getCorrelationById(
            UUID correlationId) {

        Correlation correlation =
                correlationRepository
                        .findByCorrelationId(correlationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Correlation not found: "
                                                + correlationId
                                )
                        );

        return correlationMapper.toResponse(
                correlation
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorrelationResponse>
    getCorrelationsByCustomer(UUID customerId) {

        return correlationRepository
                .findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(correlationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorrelationResponse>
    getCorrelationsByTransaction(UUID transactionId) {

        return correlationRepository
                .findByTransactionIdOrderByCreatedAtDesc(transactionId)
                .stream()
                .map(correlationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorrelationResponse>
    getCorrelationsByKey(String correlationKey) {

        return correlationRepository
                .findByCorrelationKeyOrderByCreatedAtDesc(
                        correlationKey
                )
                .stream()
                .map(correlationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorrelationResponse>
    getCorrelationsByType(String correlationType) {

        return correlationRepository
                .findByCorrelationTypeOrderByCreatedAtDesc(
                        correlationType
                )
                .stream()
                .map(correlationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorrelationResponse>
    getCorrelationsByStatus(String correlationStatus) {

        return correlationRepository
                .findByCorrelationStatusOrderByCreatedAtDesc(
                        correlationStatus
                )
                .stream()
                .map(correlationMapper::toResponse)
                .toList();
    }
}