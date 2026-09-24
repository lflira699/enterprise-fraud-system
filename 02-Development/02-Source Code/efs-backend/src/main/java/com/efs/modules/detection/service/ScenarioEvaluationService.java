package com.efs.modules.detection.service;

import com.efs.modules.administration.service.TenantOrganizationLookupServiceInterface;
import com.efs.modules.customer.dto.CustomerResponse;
import com.efs.modules.customer.service.CustomerServiceInterface;
import com.efs.modules.detection.dto.ScenarioEvaluationRequest;
import com.efs.modules.detection.dto.ScenarioEvaluationResponse;
import com.efs.modules.detection.entity.ScenarioEvaluation;
import com.efs.modules.detection.mapper.ScenarioEvaluationMapper;
import com.efs.modules.detection.repository.ScenarioEvaluationRepository;
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
public class ScenarioEvaluationService
        implements ScenarioEvaluationServiceInterface {

    private final ScenarioEvaluationRepository
            scenarioEvaluationRepository;

    private final ScenarioEvaluationMapper
            scenarioEvaluationMapper;

    private final TransactionServiceInterface
            transactionService;

    private final CustomerServiceInterface
            customerService;

    private final TenantOrganizationLookupServiceInterface
            tenantOrganizationLookupService;

    public ScenarioEvaluationService(
            ScenarioEvaluationRepository scenarioEvaluationRepository,
            ScenarioEvaluationMapper scenarioEvaluationMapper,
            TransactionServiceInterface transactionService,
            CustomerServiceInterface customerService,
            TenantOrganizationLookupServiceInterface
                    tenantOrganizationLookupService) {

        this.scenarioEvaluationRepository =
                scenarioEvaluationRepository;

        this.scenarioEvaluationMapper =
                scenarioEvaluationMapper;

        this.transactionService =
                transactionService;

        this.customerService =
                customerService;

        this.tenantOrganizationLookupService =
                tenantOrganizationLookupService;
    }

    @Override
    @Transactional
    public ScenarioEvaluationResponse createScenarioEvaluation(
            ScenarioEvaluationRequest request,
            UUID organizationId,
            UUID tenantId) {

        Objects.requireNonNull(
                request,
                "request is required"
        );

        Objects.requireNonNull(
                organizationId,
                "organizationId is required"
        );

        ScenarioEvaluation evaluation =
                scenarioEvaluationMapper.toEntity(request);

        applyOrganizationalOwnership(
                evaluation,
                request,
                organizationId,
                tenantId
        );

        LocalDateTime now =
                LocalDateTime.now();

        evaluation.setEvaluatedAt(now);
        evaluation.setCreatedAt(now);

        ScenarioEvaluation savedEvaluation =
                scenarioEvaluationRepository.save(
                        evaluation
                );

        return scenarioEvaluationMapper.toResponse(
                savedEvaluation
        );
    }

    private void applyOrganizationalOwnership(
            ScenarioEvaluation evaluation,
            ScenarioEvaluationRequest request,
            UUID authorizedOrganizationId,
            UUID authorizedTenantId) {

        UUID resourceOrganizationId =
                authorizedOrganizationId;

        UUID resourceTenantId =
                authorizedTenantId;

        boolean hasTransaction =
                request.getTransactionId() != null;

        boolean hasCustomer =
                request.getCustomerId() != null;

        if (hasTransaction) {

            TransactionResponse transaction =
                    transactionService.getTransactionById(
                            request.getTransactionId()
                    );

            if (transaction.getOrganizationId() == null) {

                throw new IllegalStateException(
                        "Transaction organizationId is required "
                                + "for ScenarioEvaluation organizational scope"
                );
            }

            resourceOrganizationId =
                    transaction.getOrganizationId();

            resourceTenantId =
                    transaction.getTenantId();
        }

        if (hasCustomer) {

            CustomerResponse customer =
                    customerService.getCustomerById(
                            request.getCustomerId()
                    );

            UUID customerTenantId =
                    customer.getTenantId();

            if (customerTenantId == null) {

                throw new IllegalStateException(
                        "Customer tenantId is required "
                                + "for ScenarioEvaluation organizational scope"
                );
            }

            UUID customerOrganizationId =
                    tenantOrganizationLookupService
                            .getOrganizationIdByTenantId(
                                    customerTenantId
                            );

            if (customerOrganizationId == null) {

                throw new IllegalStateException(
                        "Customer organizationId is required "
                                + "for ScenarioEvaluation organizational scope"
                );
            }

            if (hasTransaction) {

                if (
                    !Objects.equals(
                            resourceOrganizationId,
                            customerOrganizationId
                    )
                            ||
                    !Objects.equals(
                            resourceTenantId,
                            customerTenantId
                    )
                ) {

                    throw new ResourceNotFoundException(
                            "Scenario evaluation references "
                                    + "do not exist in the same "
                                    + "organizational scope"
                    );
                }
            }
            else {

                resourceOrganizationId =
                        customerOrganizationId;

                resourceTenantId =
                        customerTenantId;
            }
        }

        validateAuthorizedScope(
                resourceOrganizationId,
                resourceTenantId,
                authorizedOrganizationId,
                authorizedTenantId
        );

        evaluation.setOrganizationId(
                resourceOrganizationId
        );

        evaluation.setTenantId(
                resourceTenantId
        );
    }

    private void validateAuthorizedScope(
            UUID resourceOrganizationId,
            UUID resourceTenantId,
            UUID authorizedOrganizationId,
            UUID authorizedTenantId) {

        if (
            !Objects.equals(
                    resourceOrganizationId,
                    authorizedOrganizationId
            )
        ) {

            throw new ResourceNotFoundException(
                    "Scenario evaluation ownership "
                            + "not found in authorized scope"
            );
        }

        if (
            authorizedTenantId != null
                    &&
            !Objects.equals(
                    resourceTenantId,
                    authorizedTenantId
            )
        ) {

            throw new ResourceNotFoundException(
                    "Scenario evaluation ownership "
                            + "not found in authorized scope"
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ScenarioEvaluationResponse getScenarioEvaluationById(
            UUID evaluationId,
            UUID organizationId,
            UUID tenantId) {

        ScenarioEvaluation evaluation =
                scenarioEvaluationRepository
                        .findScopedByEvaluationId(
                                evaluationId,
                                organizationId,
                                tenantId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Scenario evaluation not found: "
                                                + evaluationId
                                )
                        );

        return scenarioEvaluationMapper.toResponse(
                evaluation
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioEvaluationResponse>
    getEvaluationsByScenario(
            UUID scenarioId,
            UUID organizationId,
            UUID tenantId) {

        return scenarioEvaluationRepository
                .findScopedByScenarioId(
                        scenarioId,
                        organizationId,
                        tenantId
                )
                .stream()
                .map(scenarioEvaluationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioEvaluationResponse>
    getEvaluationsByScenarioVersion(
            UUID scenarioVersionId,
            UUID organizationId,
            UUID tenantId) {

        return scenarioEvaluationRepository
                .findScopedByScenarioVersionId(
                        scenarioVersionId,
                        organizationId,
                        tenantId
                )
                .stream()
                .map(scenarioEvaluationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioEvaluationResponse>
    getEvaluationsByTransaction(
            UUID transactionId,
            UUID organizationId,
            UUID tenantId) {

        return scenarioEvaluationRepository
                .findScopedByTransactionId(
                        transactionId,
                        organizationId,
                        tenantId
                )
                .stream()
                .map(scenarioEvaluationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioEvaluationResponse>
    getEvaluationsByCustomer(
            UUID customerId,
            UUID organizationId,
            UUID tenantId) {

        return scenarioEvaluationRepository
                .findScopedByCustomerId(
                        customerId,
                        organizationId,
                        tenantId
                )
                .stream()
                .map(scenarioEvaluationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioEvaluationResponse>
    getEvaluationsByStatus(
            String evaluationStatus,
            UUID organizationId,
            UUID tenantId) {

        return scenarioEvaluationRepository
                .findScopedByEvaluationStatus(
                        evaluationStatus,
                        organizationId,
                        tenantId
                )
                .stream()
                .map(scenarioEvaluationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScenarioEvaluationResponse>
    getEvaluationsByMatched(
            Boolean matched,
            UUID organizationId,
            UUID tenantId) {

        return scenarioEvaluationRepository
                .findScopedByMatched(
                        matched,
                        organizationId,
                        tenantId
                )
                .stream()
                .map(scenarioEvaluationMapper::toResponse)
                .toList();
    }
}