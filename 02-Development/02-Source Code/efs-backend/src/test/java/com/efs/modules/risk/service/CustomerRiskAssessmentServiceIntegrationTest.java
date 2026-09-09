package com.efs.modules.risk.service;

import com.efs.modules.customer.dto.CustomerRiskProfileRequest;
import com.efs.modules.customer.dto.CustomerRiskProfileResponse;
import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.entity.CustomerHistory;
import com.efs.modules.customer.entity.CustomerRiskProfile;
import com.efs.modules.customer.repository.CustomerHistoryRepository;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.customer.repository.CustomerRiskProfileRepository;
import com.efs.shared.exception.DuplicateRecordException;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class CustomerRiskAssessmentServiceIntegrationTest {

    private static final UUID ACTOR_ID =
            UUID.fromString(
                    "11111111-2222-3333-4444-555555555555"
            );

    @Autowired
    private CustomerRiskAssessmentServiceInterface service;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerRiskProfileRepository
            customerRiskProfileRepository;

    @Autowired
    private CustomerHistoryRepository
            customerHistoryRepository;

    @Test
    void shouldCreateCustomerRiskAssessmentFromEightFactors() {

        UUID customerId = createCustomer();

        UUID correlationId =
                UUID.randomUUID();

        CustomerRiskProfileRequest request =
                buildRequest(
                        correlationId,
                        "10",
                        "20",
                        "30",
                        "40",
                        "50",
                        "60",
                        "70",
                        "80"
                );

        request.setCurrentRiskScore(
                new BigDecimal("99.00")
        );

        request.setRiskLevel(
                "CRITICAL"
        );

        CustomerRiskProfileResponse response =
                service.createRiskAssessment(
                        customerId,
                        request
                );

        assertNotNull(response.getProfileId());

        assertEquals(
                customerId,
                response.getCustomerId()
        );

        assertEquals(
                new BigDecimal("45.00"),
                response.getCurrentRiskScore()
        );

        assertEquals(
                "MEDIUM",
                response.getRiskLevel()
        );

        assertNotNull(
                response.getLastCalculation()
        );

        CustomerRiskProfile persisted =
                customerRiskProfileRepository
                        .findByCustomerIdAndDeletedAtIsNull(
                                customerId
                        )
                        .orElseThrow();

        assertEquals(
                new BigDecimal("45.00"),
                persisted.getCurrentRiskScore()
        );

        assertEquals(
                "MEDIUM",
                persisted.getRiskLevel()
        );

        assertEquals(
                new BigDecimal("10"),
                persisted.getBehaviorScore()
        );

        assertEquals(
                new BigDecimal("20"),
                persisted.getFraudScore()
        );

        assertEquals(
                new BigDecimal("30"),
                persisted.getAmlScore()
        );

        assertEquals(
                new BigDecimal("40"),
                persisted.getKycScore()
        );

        assertEquals(
                new BigDecimal("50"),
                persisted.getDeviceScore()
        );

        assertEquals(
                new BigDecimal("60"),
                persisted.getSanctionsScore()
        );

        assertEquals(
                new BigDecimal("70"),
                persisted.getPepScore()
        );

        assertEquals(
                new BigDecimal("80"),
                persisted.getWatchlistScore()
        );

        List<CustomerHistory> history =
                customerHistoryRepository
                        .findByCustomerIdAndEventTypeOrderByEventTimestampDesc(
                                customerId,
                                "CUSTOMER_RISK_ASSESSED"
                        );

        assertEquals(
                1,
                history.size()
        );

        CustomerHistory event =
                history.getFirst();

        assertNull(
                event.getPreviousRiskScore()
        );

        assertNull(
                event.getPreviousRiskLevel()
        );

        assertEquals(
                new BigDecimal("45.00"),
                event.getNewRiskScore()
        );

        assertEquals(
                "MEDIUM",
                event.getNewRiskLevel()
        );

        assertEquals(
                correlationId.toString(),
                event.getSourceReference()
        );
    }

    @Test
    void shouldIgnoreCallerSuppliedCalculatedValues() {

        UUID customerId = createCustomer();

        CustomerRiskProfileRequest request =
                buildUniformRequest(
                        UUID.randomUUID(),
                        "10"
                );

        request.setCurrentRiskScore(
                new BigDecimal("99.00")
        );

        request.setRiskLevel(
                "CRITICAL"
        );

        CustomerRiskProfileResponse response =
                service.createRiskAssessment(
                        customerId,
                        request
                );

        assertEquals(
                new BigDecimal("10.00"),
                response.getCurrentRiskScore()
        );

        assertEquals(
                "VERY_LOW",
                response.getRiskLevel()
        );
    }

    @Test
    void shouldRejectMissingEnabledFactorScore() {

        UUID customerId = createCustomer();

        CustomerRiskProfileRequest request =
                buildUniformRequest(
                        UUID.randomUUID(),
                        "50"
                );

        request.setWatchlistScore(null);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                service.createRiskAssessment(
                                        customerId,
                                        request
                                )
                );

        assertEquals(
                "Risk score is required for enabled factor: WATCHLIST",
                exception.getMessage()
        );

        assertEquals(
                0,
                customerHistoryRepository
                        .findByCustomerIdAndEventTypeOrderByEventTimestampDesc(
                                customerId,
                                "CUSTOMER_RISK_ASSESSED"
                        )
                        .size()
        );
    }

    @Test
    void shouldReuseAssessmentForSameEvaluationProcess() {

        UUID customerId = createCustomer();

        UUID correlationId =
                UUID.randomUUID();

        CustomerRiskProfileResponse first =
                service.createRiskAssessment(
                        customerId,
                        buildUniformRequest(
                                correlationId,
                                "30"
                        )
                );

        CustomerRiskProfileRequest repeatedRequest =
                buildUniformRequest(
                        correlationId,
                        "90"
                );

        repeatedRequest.setCurrentRiskScore(
                new BigDecimal("100.00")
        );

        repeatedRequest.setRiskLevel(
                "CRITICAL"
        );

        CustomerRiskProfileResponse reused =
                service.createRiskAssessment(
                        customerId,
                        repeatedRequest
                );

        assertEquals(
                first.getProfileId(),
                reused.getProfileId()
        );

        assertEquals(
                new BigDecimal("30.00"),
                reused.getCurrentRiskScore()
        );

        assertEquals(
                "LOW",
                reused.getRiskLevel()
        );

        List<CustomerHistory> history =
                customerHistoryRepository
                        .findByCustomerIdAndEventTypeOrderByEventTimestampDesc(
                                customerId,
                                "CUSTOMER_RISK_ASSESSED"
                        );

        assertEquals(
                1,
                history.size()
        );
    }

    @Test
    void shouldUpdateAssessmentAndPreserveRiskEvolution() {

        UUID customerId = createCustomer();

        UUID firstCorrelationId =
                UUID.randomUUID();

        UUID secondCorrelationId =
                UUID.randomUUID();

        CustomerRiskProfileResponse first =
                service.createRiskAssessment(
                        customerId,
                        buildUniformRequest(
                                firstCorrelationId,
                                "20"
                        )
                );

        assertEquals(
                new BigDecimal("20.00"),
                first.getCurrentRiskScore()
        );

        assertEquals(
                "LOW",
                first.getRiskLevel()
        );

        CustomerRiskProfileResponse updated =
                service.updateRiskAssessment(
                        customerId,
                        buildUniformRequest(
                                secondCorrelationId,
                                "80"
                        )
                );

        assertEquals(
                first.getProfileId(),
                updated.getProfileId()
        );

        assertEquals(
                new BigDecimal("80.00"),
                updated.getCurrentRiskScore()
        );

        assertEquals(
                "CRITICAL",
                updated.getRiskLevel()
        );

        List<CustomerHistory> history =
                customerHistoryRepository
                        .findByCustomerIdAndEventTypeOrderByEventTimestampDesc(
                                customerId,
                                "CUSTOMER_RISK_ASSESSED"
                        );

        assertEquals(
                2,
                history.size()
        );

        CustomerHistory updateEvent =
                history.stream()
                        .filter(
                                item ->
                                        secondCorrelationId
                                                .toString()
                                                .equals(
                                                        item.getSourceReference()
                                                )
                        )
                        .findFirst()
                        .orElseThrow();

        assertEquals(
                new BigDecimal("20.00"),
                updateEvent.getPreviousRiskScore()
        );

        assertEquals(
                "LOW",
                updateEvent.getPreviousRiskLevel()
        );

        assertEquals(
                new BigDecimal("80.00"),
                updateEvent.getNewRiskScore()
        );

        assertEquals(
                "CRITICAL",
                updateEvent.getNewRiskLevel()
        );
    }

    @Test
    void shouldReuseUpdateForSameEvaluationProcess() {

        UUID customerId = createCustomer();

        service.createRiskAssessment(
                customerId,
                buildUniformRequest(
                        UUID.randomUUID(),
                        "20"
                )
        );

        UUID updateCorrelationId =
                UUID.randomUUID();

        CustomerRiskProfileResponse firstUpdate =
                service.updateRiskAssessment(
                        customerId,
                        buildUniformRequest(
                                updateCorrelationId,
                                "60"
                        )
                );

        CustomerRiskProfileResponse reused =
                service.updateRiskAssessment(
                        customerId,
                        buildUniformRequest(
                                updateCorrelationId,
                                "95"
                        )
                );

        assertEquals(
                firstUpdate.getProfileId(),
                reused.getProfileId()
        );

        assertEquals(
                new BigDecimal("60.00"),
                reused.getCurrentRiskScore()
        );

        assertEquals(
                "HIGH",
                reused.getRiskLevel()
        );

        List<CustomerHistory> history =
                customerHistoryRepository
                        .findByCustomerIdAndEventTypeOrderByEventTimestampDesc(
                                customerId,
                                "CUSTOMER_RISK_ASSESSED"
                        );

        assertEquals(
                2,
                history.size()
        );
    }

    @Test
    void shouldRejectDifferentCreateProcessWhenProfileAlreadyExists() {

        UUID customerId = createCustomer();

        service.createRiskAssessment(
                customerId,
                buildUniformRequest(
                        UUID.randomUUID(),
                        "30"
                )
        );

        assertThrows(
                DuplicateRecordException.class,
                () ->
                        service.createRiskAssessment(
                                customerId,
                                buildUniformRequest(
                                        UUID.randomUUID(),
                                        "40"
                                )
                        )
        );

        assertEquals(
                1,
                customerHistoryRepository
                        .findByCustomerIdAndEventTypeOrderByEventTimestampDesc(
                                customerId,
                                "CUSTOMER_RISK_ASSESSED"
                        )
                        .size()
        );
    }

    @Test
    void shouldRejectUpdateWhenRiskProfileDoesNotExist() {

        UUID customerId = createCustomer();

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        service.updateRiskAssessment(
                                customerId,
                                buildUniformRequest(
                                        UUID.randomUUID(),
                                        "50"
                                )
                        )
        );
    }

    @Test
    void shouldRejectAssessmentForUnknownCustomer() {

        UUID unknownCustomerId =
                UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        service.createRiskAssessment(
                                unknownCustomerId,
                                buildUniformRequest(
                                        UUID.randomUUID(),
                                        "50"
                                )
                        )
        );
    }

    private UUID createCustomer() {

        LocalDateTime now =
                LocalDateTime.now();

        Customer customer =
                new Customer();

        customer.setCustomerNumber(
                "UC030-"
                        + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setRiskLevel(
                "LOW"
        );

        customer.setRiskScore(
                BigDecimal.ZERO
        );

        customer.setCustomerStatus(
                "ACTIVE"
        );

        customer.setRecordStatus(
                "ACTIVE"
        );

        customer.setRecordVersion(1);

        customer.setCreatedAt(now);
        customer.setUpdatedAt(now);

        return customerRepository
                .saveAndFlush(customer)
                .getCustomerId();
    }

    private CustomerRiskProfileRequest buildUniformRequest(
            UUID correlationId,
            String score) {

        return buildRequest(
                correlationId,
                score,
                score,
                score,
                score,
                score,
                score,
                score,
                score
        );
    }

    private CustomerRiskProfileRequest buildRequest(
            UUID correlationId,
            String behaviorScore,
            String fraudScore,
            String amlScore,
            String kycScore,
            String deviceScore,
            String sanctionsScore,
            String pepScore,
            String watchlistScore) {

        CustomerRiskProfileRequest request =
                new CustomerRiskProfileRequest();

        request.setCorrelationId(
                correlationId
        );

        request.setBehaviorScore(
                decimal(behaviorScore)
        );

        request.setFraudScore(
                decimal(fraudScore)
        );

        request.setAmlScore(
                decimal(amlScore)
        );

        request.setKycScore(
                decimal(kycScore)
        );

        request.setDeviceScore(
                decimal(deviceScore)
        );

        request.setSanctionsScore(
                decimal(sanctionsScore)
        );

        request.setPepScore(
                decimal(pepScore)
        );

        request.setWatchlistScore(
                decimal(watchlistScore)
        );

        request.setCreatedBy(
                ACTOR_ID
        );

        request.setUpdatedBy(
                ACTOR_ID
        );

        return request;
    }

    private BigDecimal decimal(
            String value) {

        return value == null
                ? null
                : new BigDecimal(value);
    }
}