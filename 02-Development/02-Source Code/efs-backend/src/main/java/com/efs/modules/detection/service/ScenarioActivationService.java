package com.efs.modules.detection.service;

import com.efs.modules.administration.service.TenantOrganizationLookupServiceInterface;
import com.efs.modules.customer.dto.CustomerResponse;
import com.efs.modules.customer.service.CustomerServiceInterface;
import com.efs.modules.detection.dto.ScenarioActivationRequest;
import com.efs.modules.detection.dto.ScenarioActivationResponse;
import com.efs.modules.detection.entity.ScenarioActivation;
import com.efs.modules.detection.mapper.ScenarioActivationMapper;
import com.efs.modules.detection.repository.ScenarioActivationRepository;
import com.efs.modules.transaction.dto.TransactionResponse;
import com.efs.modules.transaction.service.TransactionServiceInterface;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ScenarioActivationService
        implements ScenarioActivationServiceInterface {

    private final ScenarioActivationRepository scenarioActivationRepository;
    private final ScenarioActivationMapper scenarioActivationMapper;
    private final TransactionServiceInterface transactionService;
    private final CustomerServiceInterface customerService;
    private final TenantOrganizationLookupServiceInterface
            tenantOrganizationLookupService;

    public ScenarioActivationService(
            ScenarioActivationRepository scenarioActivationRepository,
            ScenarioActivationMapper scenarioActivationMapper,
            TransactionServiceInterface transactionService,
            CustomerServiceInterface customerService,
            TenantOrganizationLookupServiceInterface
                    tenantOrganizationLookupService) {

        this.scenarioActivationRepository =
                scenarioActivationRepository;
        this.scenarioActivationMapper =
                scenarioActivationMapper;
        this.transactionService =
                transactionService;
        this.customerService =
                customerService;
        this.tenantOrganizationLookupService =
                tenantOrganizationLookupService;
    }

    @Override
    @Transactional
    public ScenarioActivationResponse createScenarioActivation(
            ScenarioActivationRequest request) {

        ScenarioActivation activation =
                scenarioActivationMapper.toEntity(request);

        applyOrganizationalOwnership(
                activation,
                request
        );

        LocalDateTime now = LocalDateTime.now();

        activation.setTriggeredAt(now);
        activation.setCreatedAt(now);

        ScenarioActivation savedActivation =
                scenarioActivationRepository.save(activation);

        return scenarioActivationMapper.toResponse(
                savedActivation
        );
    }

    private void applyOrganizationalOwnership(
            ScenarioActivation activation,
            ScenarioActivationRequest request) {

        if (request.getTransactionId() != null) {

            TransactionResponse transaction =
                    transactionService.getTransactionById(
                            request.getTransactionId()
                    );

            if (transaction.getOrganizationId() == null) {
                throw new IllegalStateException(
                        "Transaction organizationId is required "
                                + "for ScenarioActivation organizational scope"
                );
            }

            if (request.getCustomerId() != null) {

                CustomerResponse customer =
                        customerService.getCustomerById(
                                request.getCustomerId()
                        );

                UUID customerTenantId =
                        customer.getTenantId();

                if (customerTenantId == null) {
                    throw new IllegalStateException(
                            "Customer tenantId is required "
                                    + "to validate ScenarioActivation "
                                    + "organizational scope"
                    );
                }

                UUID customerOrganizationId =
                        tenantOrganizationLookupService
                                .getOrganizationIdByTenantId(
                                        customerTenantId
                                );

                if (!transaction
                        .getOrganizationId()
                        .equals(customerOrganizationId)
                        || !Objects.equals(
                                transaction.getTenantId(),
                                customerTenantId
                        )) {

                    throw new IllegalStateException(
                            "Transaction and Customer organizational "
                                    + "scope mismatch for ScenarioActivation"
                    );
                }
            }

            activation.setOrganizationId(
                    transaction.getOrganizationId()
            );

            activation.setTenantId(
                    transaction.getTenantId()
            );

            return;
        }

        if (request.getCustomerId() != null) {

            CustomerResponse customer =
                    customerService.getCustomerById(
                            request.getCustomerId()
                    );

            UUID customerTenantId =
                    customer.getTenantId();

            if (customerTenantId == null) {
                throw new IllegalStateException(
                        "Customer tenantId is required "
                                + "for ScenarioActivation organizational scope"
                );
            }

            UUID customerOrganizationId =
                    tenantOrganizationLookupService
                            .getOrganizationIdByTenantId(
                                    customerTenantId
                            );

            if (customerOrganizationId == null) {
                throw new IllegalStateException(
                        "Customer organizationId could not be resolved "
                                + "for ScenarioActivation organizational scope"
                );
            }

            activation.setOrganizationId(
                    customerOrganizationId
            );

            activation.setTenantId(
                    customerTenantId
            );

            return;
        }

        throw new IllegalStateException(
                "ScenarioActivation organizational scope "
                        + "requires transactionId or customerId"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ScenarioActivationResponse getScenarioActivationById(
            UUID activationId) {

        ScenarioActivation activation =
                scenarioActivationRepository
                        .findByActivationId(activationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Scenario activation not found: "
                                                + activationId
                                )
                        );

        return scenarioActivationMapper.toResponse(
                activation
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioActivationResponse>
    getActivationsByScenario(UUID scenarioId) {

        return scenarioActivationRepository
                .findByScenarioIdOrderByTriggeredAtDesc(scenarioId)
                .stream()
                .map(scenarioActivationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioActivationResponse>
    getActivationsByScenarioVersion(UUID scenarioVersionId) {

        return scenarioActivationRepository
                .findByScenarioVersionIdOrderByTriggeredAtDesc(
                        scenarioVersionId
                )
                .stream()
                .map(scenarioActivationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioActivationResponse>
    getActivationsByTransaction(UUID transactionId) {

        return scenarioActivationRepository
                .findByTransactionIdOrderByTriggeredAtDesc(
                        transactionId
                )
                .stream()
                .map(scenarioActivationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioActivationResponse>
    getActivationsByCustomer(UUID customerId) {

        return scenarioActivationRepository
                .findByCustomerIdOrderByTriggeredAtDesc(
                        customerId
                )
                .stream()
                .map(scenarioActivationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioActivationResponse>
    getActivationsByStatus(String activationStatus) {

        return scenarioActivationRepository
                .findByActivationStatusOrderByTriggeredAtDesc(
                        activationStatus
                )
                .stream()
                .map(scenarioActivationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioActivationResponse>
    getActivationsBySeverity(String severity) {

        return scenarioActivationRepository
                .findBySeverityOrderByTriggeredAtDesc(severity)
                .stream()
                .map(scenarioActivationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countActivatedDetectionScenarios(
            UUID organizationId,
            UUID tenantId) {

        if (organizationId == null) {
            throw new IllegalArgumentException(
                    "organizationId is required"
            );
        }

        if (tenantId == null) {
            return scenarioActivationRepository
                    .countDistinctScenariosByOrganizationIdAndTenantIdIsNull(
                            organizationId
                    );
        }

        return scenarioActivationRepository
                .countDistinctScenariosByOrganizationIdAndTenantId(
                        organizationId,
                        tenantId
                );
    }
}
