package com.efs.modules.transaction.service;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.transaction.dto.TransactionRuleResultRequest;
import com.efs.modules.transaction.dto.TransactionRuleResultResponse;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.modules.transaction.repository.TransactionRuleResultRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TransactionRuleResultServiceIntegrationTest {

    @Autowired
    private TransactionRuleResultServiceInterface
            transactionRuleResultService;

    @Autowired
    private TransactionRuleResultRepository
            transactionRuleResultRepository;

    @Autowired
    private TransactionRepository
            transactionRepository;

    @Autowired
    private CustomerRepository
            customerRepository;

    private UUID transactionId;
    private UUID ruleId;

    @BeforeEach
    void setUp() {

        transactionRuleResultRepository.deleteAll();
        transactionRepository.deleteAll();
        customerRepository.deleteAll();

        LocalDateTime now =
                LocalDateTime.now();

        Customer customer =
                new Customer();

        customer.setCustomerNumber(
                "TRR-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Integration"
        );

        customer.setLastName(
                "Test"
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

        customer.setCreatedAt(now);
        customer.setUpdatedAt(now);

        customer.setRecordStatus(
                "ACTIVE"
        );

        customer.setRecordVersion(0);

        Customer savedCustomer =
                customerRepository.saveAndFlush(customer);

        Transaction transaction =
                new Transaction();

        transaction.setTransactionReference(
                "EFS-TRR-TXN-" + UUID.randomUUID()
        );

        transaction.setCustomerId(
                savedCustomer.getCustomerId()
        );

        transaction.setOrganizationId(
                UUID.randomUUID()
        );

        transaction.setTransactionType(
                "PAYMENT"
        );

        transaction.setAmount(
                new BigDecimal("250.00")
        );

        transaction.setCurrencyCode(
                "GTQ"
        );

        transaction.setTransactionDatetime(now);

        transaction.setTransactionStatus(
                "PENDING"
        );

        transaction.setFinalDecision(
                "PENDING"
        );

        transaction.setFraudScore(
                BigDecimal.ZERO
        );

        transaction.setCreatedAt(now);
        transaction.setUpdatedAt(now);

        transaction.setCreatedBy(
                UUID.randomUUID()
        );

        transaction.setRecordVersion(0);

        Transaction savedTransaction =
                transactionRepository.saveAndFlush(transaction);

        transactionId =
                savedTransaction.getTransactionId();

        ruleId =
                UUID.randomUUID();
    }

    @Test
    void shouldCreateAndRetrieveRuleResult() {

        TransactionRuleResultRequest request =
                createRequest(
                        ruleId,
                        "MATCH",
                        (short) 1,
                        new BigDecimal("25.00"),
                        LocalDateTime.now()
                );

        TransactionRuleResultResponse created =
                transactionRuleResultService
                        .createRuleResult(
                                transactionId,
                                request
                        );

        assertNotNull(created);
        assertNotNull(created.getRuleResultId());

        assertEquals(
                transactionId,
                created.getTransactionId()
        );

        assertEquals(
                ruleId,
                created.getRuleId()
        );

        assertEquals(
                "MATCH",
                created.getEvaluationResult()
        );

        TransactionRuleResultResponse retrieved =
                transactionRuleResultService
                        .getRuleResultById(
                                created.getRuleResultId()
                        );

        assertEquals(
                created.getRuleResultId(),
                retrieved.getRuleResultId()
        );

        assertEquals(
                transactionId,
                retrieved.getTransactionId()
        );
    }

    @Test
    void shouldReturnRuleResultsByTransactionInExecutionOrder() {

        transactionRuleResultService.createRuleResult(
                transactionId,
                createRequest(
                        UUID.randomUUID(),
                        "NO_MATCH",
                        (short) 2,
                        BigDecimal.ZERO,
                        LocalDateTime.now().plusSeconds(2)
                )
        );

        transactionRuleResultService.createRuleResult(
                transactionId,
                createRequest(
                        UUID.randomUUID(),
                        "MATCH",
                        (short) 1,
                        new BigDecimal("40.00"),
                        LocalDateTime.now()
                )
        );

        List<TransactionRuleResultResponse> results =
                transactionRuleResultService
                        .getRuleResultsByTransactionId(
                                transactionId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                Short.valueOf((short) 1),
                results.get(0).getExecutionOrder()
        );

        assertEquals(
                Short.valueOf((short) 2),
                results.get(1).getExecutionOrder()
        );
    }

    @Test
    void shouldReturnRuleResultsByRuleAndEvaluationResult() {

        transactionRuleResultService.createRuleResult(
                transactionId,
                createRequest(
                        ruleId,
                        "MATCH",
                        (short) 1,
                        new BigDecimal("50.00"),
                        LocalDateTime.now()
                )
        );

        List<TransactionRuleResultResponse> byRule =
                transactionRuleResultService
                        .getRuleResultsByRuleId(
                                ruleId
                        );

        List<TransactionRuleResultResponse> byResult =
                transactionRuleResultService
                        .getRuleResultsByEvaluationResult(
                                "MATCH"
                        );

        assertFalse(byRule.isEmpty());
        assertFalse(byResult.isEmpty());

        assertTrue(
                byRule.stream()
                        .allMatch(result ->
                                ruleId.equals(
                                        result.getRuleId()
                                )
                        )
        );

        assertTrue(
                byResult.stream()
                        .allMatch(result ->
                                "MATCH".equals(
                                        result.getEvaluationResult()
                                )
                        )
        );
    }

    @Test
    void createRuleResultShouldRejectNegativeExecutionTime() {

        TransactionRuleResultRequest request =
                createRequest(
                        ruleId,
                        "MATCH",
                        (short) 1,
                        new BigDecimal("25.00"),
                        LocalDateTime.now()
                );

        request.setExecutionTimeMs(-1);

        assertThrows(
                IllegalArgumentException.class,
                () -> transactionRuleResultService
                        .createRuleResult(
                                transactionId,
                                request
                        )
        );
    }

    @Test
    void getRuleResultByIdShouldHideSoftDeletedParent() {

        TransactionRuleResultResponse created =
                transactionRuleResultService
                        .createRuleResult(
                                transactionId,
                                createRequest(
                                        ruleId,
                                        "MATCH",
                                        (short) 1,
                                        new BigDecimal("25.00"),
                                        LocalDateTime.now()
                                )
                        );

        softDeleteTransaction();

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionRuleResultService
                        .getRuleResultById(
                                created.getRuleResultId()
                        )
        );
    }

    @Test
    void getRuleResultsByTransactionIdShouldRejectSoftDeletedParent() {

        softDeleteTransaction();

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionRuleResultService
                        .getRuleResultsByTransactionId(
                                transactionId
                        )
        );
    }

    @Test
    void getRuleResultsByRuleIdShouldExcludeSoftDeletedParent() {

        UUID scopedRuleId =
                UUID.randomUUID();

        transactionRuleResultService
                .createRuleResult(
                        transactionId,
                        createRequest(
                                scopedRuleId,
                                "MATCH",
                                (short) 1,
                                new BigDecimal("25.00"),
                                LocalDateTime.now()
                        )
                );

        softDeleteTransaction();

        assertTrue(
                transactionRuleResultService
                        .getRuleResultsByRuleId(
                                scopedRuleId
                        )
                        .isEmpty()
        );
    }

    @Test
    void getRuleResultsByEvaluationResultShouldExcludeSoftDeletedParent() {

        String evaluationResult =
                "SOFT_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        transactionRuleResultService
                .createRuleResult(
                        transactionId,
                        createRequest(
                                ruleId,
                                evaluationResult,
                                (short) 1,
                                new BigDecimal("25.00"),
                                LocalDateTime.now()
                        )
                );

        softDeleteTransaction();

        assertTrue(
                transactionRuleResultService
                        .getRuleResultsByEvaluationResult(
                                evaluationResult
                        )
                        .isEmpty()
        );
    }

    private void softDeleteTransaction() {

        Transaction transaction =
                transactionRepository
                        .findById(transactionId)
                        .orElseThrow();

        transaction.setDeletedAt(
                LocalDateTime.now()
        );

        transactionRepository.saveAndFlush(
                transaction
        );
    }

    private TransactionRuleResultRequest createRequest(
            UUID requestRuleId,
            String evaluationResult,
            Short executionOrder,
            BigDecimal riskPoints,
            LocalDateTime executedAt) {

        TransactionRuleResultRequest request =
                new TransactionRuleResultRequest();

        request.setRuleId(
                requestRuleId
        );

        request.setRuleVersion(
                1
        );

        request.setExecutionOrder(
                executionOrder
        );

        request.setExecutionTimeMs(
                10
        );

        request.setEvaluationResult(
                evaluationResult
        );

        request.setRiskPoints(
                riskPoints
        );

        request.setRecommendedAction(
                "REVIEW"
        );

        request.setExplanation(
                "Integration test rule result"
        );

        request.setExecutedAt(
                executedAt
        );

        return request;
    }
}