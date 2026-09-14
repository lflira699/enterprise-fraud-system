package com.efs.modules.detection.service;

import com.efs.modules.administration.service.TenantOrganizationLookupServiceInterface;
import com.efs.modules.customer.dto.CustomerResponse;
import com.efs.modules.customer.service.CustomerServiceInterface;
import com.efs.modules.detection.dto.ScenarioActivationRequest;
import com.efs.modules.detection.entity.ScenarioActivation;
import com.efs.modules.detection.mapper.ScenarioActivationMapper;
import com.efs.modules.detection.repository.ScenarioActivationRepository;
import com.efs.modules.transaction.dto.TransactionResponse;
import com.efs.modules.transaction.service.TransactionServiceInterface;
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
class ScenarioActivationOrganizationalScopeTest {

    @Mock
    private ScenarioActivationRepository scenarioActivationRepository;

    @Mock
    private TransactionServiceInterface transactionService;

    @Mock
    private CustomerServiceInterface customerService;

    @Mock
    private TenantOrganizationLookupServiceInterface
            tenantOrganizationLookupService;

    private ScenarioActivationService scenarioActivationService;

    @BeforeEach
    void setUp() {

        scenarioActivationService =
                new ScenarioActivationService(
                        scenarioActivationRepository,
                        new ScenarioActivationMapper(),
                        transactionService,
                        customerService,
                        tenantOrganizationLookupService
                );

    }

    @Test
    void transactionShouldProvideAuthoritativeOrganizationalScope() {

        stubScenarioActivationSave();

        UUID transactionId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        TransactionResponse transaction =
                transactionResponse(
                        transactionId,
                        customerId,
                        organizationId,
                        tenantId
                );

        CustomerResponse customer =
                customerResponse(
                        customerId,
                        tenantId
                );

        when(
                transactionService.getTransactionById(
                        transactionId
                )
        ).thenReturn(transaction);

        when(
                customerService.getCustomerById(
                        customerId
                )
        ).thenReturn(customer);

        when(
                tenantOrganizationLookupService
                        .getOrganizationIdByTenantId(
                                tenantId
                        )
        ).thenReturn(organizationId);

        scenarioActivationService.createScenarioActivation(
                request(
                        transactionId,
                        customerId
                )
        );

        ArgumentCaptor<ScenarioActivation> captor =
                ArgumentCaptor.forClass(
                        ScenarioActivation.class
                );

        verify(
                scenarioActivationRepository
        ).save(
                captor.capture()
        );

        assertEquals(
                organizationId,
                captor.getValue().getOrganizationId()
        );

        assertEquals(
                tenantId,
                captor.getValue().getTenantId()
        );
    }

    @Test
    void transactionOnlyShouldProvideAuthoritativeOrganizationalScope() {

        stubScenarioActivationSave();

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
                        UUID.randomUUID(),
                        organizationId,
                        tenantId
                )
        );

        scenarioActivationService.createScenarioActivation(
                request(
                        transactionId,
                        null
                )
        );

        ArgumentCaptor<ScenarioActivation> captor =
                ArgumentCaptor.forClass(
                        ScenarioActivation.class
                );

        verify(
                scenarioActivationRepository
        ).save(
                captor.capture()
        );

        assertEquals(
                organizationId,
                captor.getValue().getOrganizationId()
        );

        assertEquals(
                tenantId,
                captor.getValue().getTenantId()
        );
    }

    @Test
    void transactionOnlyShouldAllowNullTenant() {

        stubScenarioActivationSave();

        UUID transactionId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();

        when(
                transactionService.getTransactionById(
                        transactionId
                )
        ).thenReturn(
                transactionResponse(
                        transactionId,
                        UUID.randomUUID(),
                        organizationId,
                        null
                )
        );

        scenarioActivationService.createScenarioActivation(
                request(
                        transactionId,
                        null
                )
        );

        ArgumentCaptor<ScenarioActivation> captor =
                ArgumentCaptor.forClass(
                        ScenarioActivation.class
                );

        verify(
                scenarioActivationRepository
        ).save(
                captor.capture()
        );

        assertEquals(
                organizationId,
                captor.getValue().getOrganizationId()
        );

        assertNull(
                captor.getValue().getTenantId()
        );
    }
    @Test
    void customerOnlyShouldResolveOrganizationThroughTenant() {

        stubScenarioActivationSave();

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
        ).thenReturn(organizationId);

        scenarioActivationService.createScenarioActivation(
                request(
                        null,
                        customerId
                )
        );

        ArgumentCaptor<ScenarioActivation> captor =
                ArgumentCaptor.forClass(
                        ScenarioActivation.class
                );

        verify(
                scenarioActivationRepository
        ).save(
                captor.capture()
        );

        assertEquals(
                organizationId,
                captor.getValue().getOrganizationId()
        );

        assertEquals(
                tenantId,
                captor.getValue().getTenantId()
        );
    }

    @Test
    void transactionAndCustomerTenantMismatchShouldFailClosed() {

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
                        customerId,
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
        ).thenReturn(organizationId);

        assertThrows(
                IllegalStateException.class,
                () -> scenarioActivationService
                        .createScenarioActivation(
                                request(
                                        transactionId,
                                        customerId
                                )
                        )
        );

        verify(
                scenarioActivationRepository,
                never()
        ).save(any());
    }

    @Test
    void transactionAndCustomerOrganizationMismatchShouldFailClosed() {

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
                        customerId,
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
        ).thenReturn(customerOrganizationId);

        assertThrows(
                IllegalStateException.class,
                () -> scenarioActivationService
                        .createScenarioActivation(
                                request(
                                        transactionId,
                                        customerId
                                )
                        )
        );

        verify(
                scenarioActivationRepository,
                never()
        ).save(any());
    }

    @Test
    void missingTransactionOrganizationShouldFailClosed() {

        UUID transactionId = UUID.randomUUID();

        when(
                transactionService.getTransactionById(
                        transactionId
                )
        ).thenReturn(
                transactionResponse(
                        transactionId,
                        UUID.randomUUID(),
                        null,
                        UUID.randomUUID()
                )
        );

        assertThrows(
                IllegalStateException.class,
                () -> scenarioActivationService
                        .createScenarioActivation(
                                request(
                                        transactionId,
                                        null
                                )
                        )
        );

        verify(
                scenarioActivationRepository,
                never()
        ).save(any());
    }

    @Test
    void customerWithoutTenantShouldFailClosed() {

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
                () -> scenarioActivationService
                        .createScenarioActivation(
                                request(
                                        null,
                                        customerId
                                )
                        )
        );

        verify(
                scenarioActivationRepository,
                never()
        ).save(any());
    }

    @Test
    void activationWithoutTransactionOrCustomerShouldFailClosed() {

        assertThrows(
                IllegalStateException.class,
                () -> scenarioActivationService
                        .createScenarioActivation(
                                request(
                                        null,
                                        null
                                )
                        )
        );

        verify(
                scenarioActivationRepository,
                never()
        ).save(any());
    }

    private void stubScenarioActivationSave() {

        when(
                scenarioActivationRepository.save(
                        any(ScenarioActivation.class)
                )
        ).thenAnswer(invocation ->
                invocation.getArgument(0)
        );
    }

    private ScenarioActivationRequest request(
            UUID transactionId,
            UUID customerId) {

        ScenarioActivationRequest request =
                new ScenarioActivationRequest();

        request.setScenarioId(
                UUID.randomUUID()
        );

        request.setScenarioVersionId(
                UUID.randomUUID()
        );

        request.setTransactionId(
                transactionId
        );

        request.setCustomerId(
                customerId
        );

        request.setActivationStatus(
                "TRIGGERED"
        );

        request.setSeverity(
                "HIGH"
        );

        return request;
    }

    private TransactionResponse transactionResponse(
            UUID transactionId,
            UUID customerId,
            UUID organizationId,
            UUID tenantId) {

        TransactionResponse response =
                new TransactionResponse();

        response.setTransactionId(
                transactionId
        );

        response.setCustomerId(
                customerId
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
