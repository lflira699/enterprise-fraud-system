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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScenarioEvaluationOrganizationalScopeTest {

    @Mock
    private ScenarioEvaluationRepository
            scenarioEvaluationRepository;

    @Mock
    private ScenarioEvaluationMapper
            scenarioEvaluationMapper;

    @Mock
    private TransactionServiceInterface
            transactionService;

    @Mock
    private CustomerServiceInterface
            customerService;

    @Mock
    private TenantOrganizationLookupServiceInterface
            tenantOrganizationLookupService;

    private ScenarioEvaluationService
            scenarioEvaluationService;

    @BeforeEach
    void setUp() {

        scenarioEvaluationService =
                new ScenarioEvaluationService(
                        scenarioEvaluationRepository,
                        scenarioEvaluationMapper,
                        transactionService,
                        customerService,
                        tenantOrganizationLookupService
                );

        when(
                scenarioEvaluationMapper.toEntity(
                        any(ScenarioEvaluationRequest.class)
                )
        ).thenReturn(
                new ScenarioEvaluation()
        );

    }

    @Test
    void noReferencesShouldUseAuthorizedActorScope() {

        stubSuccessfulCreate();

        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        scenarioEvaluationService
                .createScenarioEvaluation(
                        request(
                                null,
                                null
                        ),
                        organizationId,
                        tenantId
                );

        ScenarioEvaluation saved =
                captureSavedEvaluation();

        assertEquals(
                organizationId,
                saved.getOrganizationId()
        );

        assertEquals(
                tenantId,
                saved.getTenantId()
        );
    }

    @Test
    void organizationLevelNoReferenceCreateShouldPersistNullTenant() {

        stubSuccessfulCreate();

        UUID organizationId = UUID.randomUUID();

        scenarioEvaluationService
                .createScenarioEvaluation(
                        request(
                                null,
                                null
                        ),
                        organizationId,
                        null
                );

        ScenarioEvaluation saved =
                captureSavedEvaluation();

        assertEquals(
                organizationId,
                saved.getOrganizationId()
        );

        assertNull(
                saved.getTenantId()
        );
    }

    @Test
    void transactionOnlyShouldProvideAuthoritativeScope() {

        stubSuccessfulCreate();

        UUID transactionId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        when(
                transactionService.getTransactionById(
                        transactionId
                )
        ).thenReturn(
                transactionResponse(
                        transactionId,
                        organizationId,
                        tenantId
                )
        );

        scenarioEvaluationService
                .createScenarioEvaluation(
                        request(
                                transactionId,
                                null
                        ),
                        organizationId,
                        tenantId
                );

        ScenarioEvaluation saved =
                captureSavedEvaluation();

        assertEquals(
                organizationId,
                saved.getOrganizationId()
        );

        assertEquals(
                tenantId,
                saved.getTenantId()
        );
    }

    @Test
    void customerOnlyShouldResolveOrganizationThroughTenant() {

        stubSuccessfulCreate();

        UUID customerId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        when(
                customerService.getCustomerById(
                        customerId
                )
        ).thenReturn(
                customerResponse(
                        customerId,
                        tenantId
                )
        );

        when(
                tenantOrganizationLookupService
                        .getOrganizationIdByTenantId(
                                tenantId
                        )
        ).thenReturn(
                organizationId
        );

        scenarioEvaluationService
                .createScenarioEvaluation(
                        request(
                                null,
                                customerId
                        ),
                        organizationId,
                        tenantId
                );

        ScenarioEvaluation saved =
                captureSavedEvaluation();

        assertEquals(
                organizationId,
                saved.getOrganizationId()
        );

        assertEquals(
                tenantId,
                saved.getTenantId()
        );
    }

    @Test
    void dualReferencesWithExactScopeShouldPersist() {

        stubSuccessfulCreate();

        UUID transactionId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        when(
                transactionService.getTransactionById(
                        transactionId
                )
        ).thenReturn(
                transactionResponse(
                        transactionId,
                        organizationId,
                        tenantId
                )
        );

        when(
                customerService.getCustomerById(
                        customerId
                )
        ).thenReturn(
                customerResponse(
                        customerId,
                        tenantId
                )
        );

        when(
                tenantOrganizationLookupService
                        .getOrganizationIdByTenantId(
                                tenantId
                        )
        ).thenReturn(
                organizationId
        );

        scenarioEvaluationService
                .createScenarioEvaluation(
                        request(
                                transactionId,
                                customerId
                        ),
                        organizationId,
                        tenantId
                );

        ScenarioEvaluation saved =
                captureSavedEvaluation();

        assertEquals(
                organizationId,
                saved.getOrganizationId()
        );

        assertEquals(
                tenantId,
                saved.getTenantId()
        );
    }

    @Test
    void dualReferenceTenantMismatchShouldRejectBeforeSave() {

        UUID transactionId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID transactionTenantId = UUID.randomUUID();
        UUID customerTenantId = UUID.randomUUID();

        when(
                transactionService.getTransactionById(
                        transactionId
                )
        ).thenReturn(
                transactionResponse(
                        transactionId,
                        organizationId,
                        transactionTenantId
                )
        );

        when(
                customerService.getCustomerById(
                        customerId
                )
        ).thenReturn(
                customerResponse(
                        customerId,
                        customerTenantId
                )
        );

        when(
                tenantOrganizationLookupService
                        .getOrganizationIdByTenantId(
                                customerTenantId
                        )
        ).thenReturn(
                organizationId
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> scenarioEvaluationService
                        .createScenarioEvaluation(
                                request(
                                        transactionId,
                                        customerId
                                ),
                                organizationId,
                                transactionTenantId
                        )
        );

        verify(
                scenarioEvaluationRepository,
                never()
        ).save(any());
    }

    @Test
    void dualReferenceOrganizationMismatchShouldRejectBeforeSave() {

        UUID transactionId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID transactionOrganizationId = UUID.randomUUID();
        UUID customerOrganizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        when(
                transactionService.getTransactionById(
                        transactionId
                )
        ).thenReturn(
                transactionResponse(
                        transactionId,
                        transactionOrganizationId,
                        tenantId
                )
        );

        when(
                customerService.getCustomerById(
                        customerId
                )
        ).thenReturn(
                customerResponse(
                        customerId,
                        tenantId
                )
        );

        when(
                tenantOrganizationLookupService
                        .getOrganizationIdByTenantId(
                                tenantId
                        )
        ).thenReturn(
                customerOrganizationId
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> scenarioEvaluationService
                        .createScenarioEvaluation(
                                request(
                                        transactionId,
                                        customerId
                                ),
                                transactionOrganizationId,
                                tenantId
                        )
        );

        verify(
                scenarioEvaluationRepository,
                never()
        ).save(any());
    }

    @Test
    void authorizedOrganizationMismatchShouldRejectBeforeSave() {

        UUID transactionId = UUID.randomUUID();
        UUID resourceOrganizationId = UUID.randomUUID();
        UUID authorizedOrganizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        when(
                transactionService.getTransactionById(
                        transactionId
                )
        ).thenReturn(
                transactionResponse(
                        transactionId,
                        resourceOrganizationId,
                        tenantId
                )
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> scenarioEvaluationService
                        .createScenarioEvaluation(
                                request(
                                        transactionId,
                                        null
                                ),
                                authorizedOrganizationId,
                                tenantId
                        )
        );

        verify(
                scenarioEvaluationRepository,
                never()
        ).save(any());
    }

    @Test
    void authorizedTenantMismatchShouldRejectBeforeSave() {

        UUID transactionId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID resourceTenantId = UUID.randomUUID();
        UUID authorizedTenantId = UUID.randomUUID();

        when(
                transactionService.getTransactionById(
                        transactionId
                )
        ).thenReturn(
                transactionResponse(
                        transactionId,
                        organizationId,
                        resourceTenantId
                )
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> scenarioEvaluationService
                        .createScenarioEvaluation(
                                request(
                                        transactionId,
                                        null
                                ),
                                organizationId,
                                authorizedTenantId
                        )
        );

        verify(
                scenarioEvaluationRepository,
                never()
        ).save(any());
    }

    @Test
    void organizationLevelActorShouldAllowTenantOwnedTransaction() {

        stubSuccessfulCreate();

        UUID transactionId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        when(
                transactionService.getTransactionById(
                        transactionId
                )
        ).thenReturn(
                transactionResponse(
                        transactionId,
                        organizationId,
                        tenantId
                )
        );

        scenarioEvaluationService
                .createScenarioEvaluation(
                        request(
                                transactionId,
                                null
                        ),
                        organizationId,
                        null
                );

        ScenarioEvaluation saved =
                captureSavedEvaluation();

        assertEquals(
                organizationId,
                saved.getOrganizationId()
        );

        assertEquals(
                tenantId,
                saved.getTenantId()
        );
    }

    @Test
    void missingTransactionOrganizationShouldFailClosedBeforeSave() {

        UUID transactionId = UUID.randomUUID();

        when(
                transactionService.getTransactionById(
                        transactionId
                )
        ).thenReturn(
                transactionResponse(
                        transactionId,
                        null,
                        UUID.randomUUID()
                )
        );

        assertThrows(
                IllegalStateException.class,
                () -> scenarioEvaluationService
                        .createScenarioEvaluation(
                                request(
                                        transactionId,
                                        null
                                ),
                                UUID.randomUUID(),
                                null
                        )
        );

        verify(
                scenarioEvaluationRepository,
                never()
        ).save(any());
    }

    @Test
    void customerWithoutTenantShouldFailClosedBeforeSave() {

        UUID customerId = UUID.randomUUID();

        when(
                customerService.getCustomerById(
                        customerId
                )
        ).thenReturn(
                customerResponse(
                        customerId,
                        null
                )
        );

        assertThrows(
                IllegalStateException.class,
                () -> scenarioEvaluationService
                        .createScenarioEvaluation(
                                request(
                                        null,
                                        customerId
                                ),
                                UUID.randomUUID(),
                                null
                        )
        );

        verify(
                scenarioEvaluationRepository,
                never()
        ).save(any());
    }

    private void stubSuccessfulCreate() {

        when(
                scenarioEvaluationRepository.save(
                        any(ScenarioEvaluation.class)
                )
        ).thenAnswer(invocation ->
                invocation.getArgument(0)
        );

        when(
                scenarioEvaluationMapper.toResponse(
                        any(ScenarioEvaluation.class)
                )
        ).thenReturn(
                new ScenarioEvaluationResponse()
        );
    }

    private ScenarioEvaluation captureSavedEvaluation() {

        ArgumentCaptor<ScenarioEvaluation> captor =
                ArgumentCaptor.forClass(
                        ScenarioEvaluation.class
                );

        verify(
                scenarioEvaluationRepository
        ).save(
                captor.capture()
        );

        return captor.getValue();
    }

    private ScenarioEvaluationRequest request(
            UUID transactionId,
            UUID customerId) {

        ScenarioEvaluationRequest request =
                new ScenarioEvaluationRequest();

        request.setTransactionId(
                transactionId
        );

        request.setCustomerId(
                customerId
        );

        return request;
    }

    private TransactionResponse transactionResponse(
            UUID transactionId,
            UUID organizationId,
            UUID tenantId) {

        TransactionResponse response =
                new TransactionResponse();

        response.setTransactionId(
                transactionId
        );

        response.setOrganizationId(
                organizationId
        );

        response.setTenantId(
                tenantId
        );

        return response;
    }

    private CustomerResponse customerResponse(
            UUID customerId,
            UUID tenantId) {

        CustomerResponse response =
                new CustomerResponse();

        response.setCustomerId(
                customerId
        );

        response.setTenantId(
                tenantId
        );

        return response;
    }
}
