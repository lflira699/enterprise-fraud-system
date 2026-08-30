package com.efs.modules.detection.service;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.detection.dto.RelationshipAnalysisRequest;
import com.efs.modules.detection.dto.RelationshipAnalysisResponse;
import com.efs.modules.detection.entity.Correlation;
import com.efs.modules.detection.repository.CorrelationRepository;
import com.efs.modules.detection.repository.RelationshipAnalysisRepository;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RelationshipAnalysisServiceIntegrationTest {

    @Autowired
    private RelationshipAnalysisServiceInterface relationshipAnalysisService;

    @Autowired
    private RelationshipAnalysisRepository relationshipAnalysisRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CorrelationRepository correlationRepository;

    private UUID customerId;
    private UUID transactionId;
    private UUID correlationId;

    @BeforeEach
    void setUp() {

        LocalDateTime now =
                LocalDateTime.now();

        Customer customer =
                new Customer();

        customer.setCustomerNumber(
                "RA-SVC-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Relationship"
        );

        customer.setLastName(
                "Analysis"
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

        customer.setCreatedAt(
                now
        );

        customer.setUpdatedAt(
                now
        );

        customer.setRecordStatus(
                "ACTIVE"
        );

        customer.setRecordVersion(
                0
        );

        Customer savedCustomer =
                customerRepository.saveAndFlush(
                        customer
                );

        customerId =
                savedCustomer.getCustomerId();

        Transaction transaction =
                new Transaction();

        transaction.setTransactionReference(
                "EFS-RA-SVC-" + UUID.randomUUID()
        );

        transaction.setCustomerId(
                customerId
        );

        transaction.setOrganizationId(
                UUID.randomUUID()
        );

        transaction.setTransactionType(
                "PAYMENT"
        );

        transaction.setAmount(
                new BigDecimal("1100.00")
        );

        transaction.setCurrencyCode(
                "GTQ"
        );

        transaction.setTransactionDatetime(
                now
        );

        transaction.setTransactionStatus(
                "PENDING"
        );

        transaction.setFinalDecision(
                "PENDING"
        );

        transaction.setFraudScore(
                BigDecimal.ZERO
        );

        transaction.setCreatedAt(
                now
        );

        transaction.setUpdatedAt(
                now
        );

        transaction.setCreatedBy(
                UUID.randomUUID()
        );

        transaction.setRecordVersion(
                0
        );

        Transaction savedTransaction =
                transactionRepository.saveAndFlush(
                        transaction
                );

        transactionId =
                savedTransaction.getTransactionId();

        Correlation correlation =
                new Correlation();

        correlation.setCustomerId(
                customerId
        );

        correlation.setTransactionId(
                transactionId
        );

        correlation.setCorrelationKey(
                "RA-CORR-" + UUID.randomUUID()
        );

        correlation.setCorrelationType(
                "TRANSACTION"
        );

        correlation.setCorrelationStatus(
                "OPEN"
        );

        correlation.setWindowStart(
                now.minusMinutes(30)
        );

        correlation.setWindowEnd(
                now
        );

        correlation.setEventCount(
                1
        );

        correlation.setMatchedRuleCount(
                (short) 0
        );

        correlation.setConfidence(
                new BigDecimal("0.7500")
        );

        correlation.setCreatedAt(
                now
        );

        correlation.setUpdatedAt(
                now
        );

        Correlation savedCorrelation =
                correlationRepository.saveAndFlush(
                        correlation
                );

        correlationId =
                savedCorrelation.getCorrelationId();
    }

    @Test
    void createRelationshipAnalysisShouldPersistValuesAndApplyInitialCounts() {

        RelationshipAnalysisRequest request =
                buildRequest(
                        customerId,
                        transactionId,
                        correlationId,
                        "COMPLETED",
                        "CUSTOMER_TO_DEVICE"
                );

        RelationshipAnalysisResponse response =
                relationshipAnalysisService
                        .createRelationshipAnalysis(
                                request
                        );

        assertNotNull(
                response
        );

        assertNotNull(
                response.getRelationshipAnalysisId()
        );

        assertEquals(
                customerId,
                response.getCustomerId()
        );

        assertEquals(
                transactionId,
                response.getTransactionId()
        );

        assertEquals(
                correlationId,
                response.getCorrelationId()
        );

        assertEquals(
                "COMPLETED",
                response.getAnalysisStatus()
        );

        assertEquals(
                "CUSTOMER_TO_DEVICE",
                response.getRelationshipType()
        );

        assertEquals(
                "CUSTOMER",
                response.getSourceEntityType()
        );

        assertEquals(
                request.getSourceEntityKey(),
                response.getSourceEntityKey()
        );

        assertEquals(
                "DEVICE",
                response.getTargetEntityType()
        );

        assertEquals(
                request.getTargetEntityKey(),
                response.getTargetEntityKey()
        );

        assertNull(
                response.getRelationshipStrength()
        );

        assertEquals(
                0,
                response.getEntityCount()
        );

        assertEquals(
                0,
                response.getRelationshipCount()
        );

        assertNotNull(
                response.getRelationshipIndicators()
        );

        assertEquals(
                true,
                response.getRelationshipIndicators()
                        .get("sharedDevice")
        );

        assertEquals(
                4,
                ((Number) response.getRelationshipIndicators()
                        .get("linkedEntities"))
                        .intValue()
        );

        assertNotNull(
                response.getAnalysisContext()
        );

        assertEquals(
                "SERVICE_TEST",
                response.getAnalysisContext()
                        .get("source")
        );

        assertEquals(
                "WEB",
                response.getAnalysisContext()
                        .get("channel")
        );

        assertNotNull(
                response.getAnalyzedAt()
        );

        assertNotNull(
                response.getCreatedAt()
        );

        assertTrue(
                relationshipAnalysisRepository.existsById(
                        response.getRelationshipAnalysisId()
                )
        );
    }

    @Test
    void createRelationshipAnalysisShouldAllowOptionalReferences() {

        RelationshipAnalysisRequest request =
                buildRequest(
                        null,
                        null,
                        null,
                        "COMPLETED",
                        "STANDALONE_LINK"
                );

        RelationshipAnalysisResponse response =
                relationshipAnalysisService
                        .createRelationshipAnalysis(
                                request
                        );

        assertNotNull(
                response.getRelationshipAnalysisId()
        );

        assertNull(
                response.getCustomerId()
        );

        assertNull(
                response.getTransactionId()
        );

        assertNull(
                response.getCorrelationId()
        );

        assertEquals(
                "COMPLETED",
                response.getAnalysisStatus()
        );

        assertEquals(
                "STANDALONE_LINK",
                response.getRelationshipType()
        );

        assertEquals(
                0,
                response.getEntityCount()
        );

        assertEquals(
                0,
                response.getRelationshipCount()
        );

        assertNotNull(
                response.getAnalyzedAt()
        );

        assertNotNull(
                response.getCreatedAt()
        );
    }

    @Test
    void getRelationshipAnalysisByIdShouldReturnExistingAnalysis() {

        RelationshipAnalysisResponse created =
                relationshipAnalysisService
                        .createRelationshipAnalysis(
                                buildRequest(
                                        customerId,
                                        transactionId,
                                        correlationId,
                                        "COMPLETED",
                                        "CUSTOMER_TO_DEVICE"
                                )
                        );

        RelationshipAnalysisResponse found =
                relationshipAnalysisService
                        .getRelationshipAnalysisById(
                                created.getRelationshipAnalysisId()
                        );

        assertEquals(
                created.getRelationshipAnalysisId(),
                found.getRelationshipAnalysisId()
        );

        assertEquals(
                customerId,
                found.getCustomerId()
        );

        assertEquals(
                transactionId,
                found.getTransactionId()
        );

        assertEquals(
                correlationId,
                found.getCorrelationId()
        );

        assertEquals(
                "CUSTOMER_TO_DEVICE",
                found.getRelationshipType()
        );

        assertEquals(
                0,
                found.getEntityCount()
        );

        assertEquals(
                0,
                found.getRelationshipCount()
        );
    }

    @Test
    void getRelationshipAnalysisByIdShouldThrowWhenAnalysisDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> relationshipAnalysisService
                        .getRelationshipAnalysisById(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void getAnalysesByCustomerShouldReturnMatchingAnalyses() {

        RelationshipAnalysisResponse first =
                relationshipAnalysisService
                        .createRelationshipAnalysis(
                                buildRequest(
                                        customerId,
                                        transactionId,
                                        correlationId,
                                        "COMPLETED",
                                        "CUSTOMER_LINK"
                                )
                        );

        RelationshipAnalysisResponse second =
                relationshipAnalysisService
                        .createRelationshipAnalysis(
                                buildRequest(
                                        customerId,
                                        null,
                                        null,
                                        "PENDING",
                                        "CUSTOMER_LINK"
                                )
                        );

        List<RelationshipAnalysisResponse> results =
                relationshipAnalysisService
                        .getAnalysesByCustomer(
                                customerId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsAnalysis(
                        results,
                        first.getRelationshipAnalysisId()
                )
        );

        assertTrue(
                containsAnalysis(
                        results,
                        second.getRelationshipAnalysisId()
                )
        );
    }

    @Test
    void getAnalysesByTransactionShouldReturnMatchingAnalysis() {

        RelationshipAnalysisResponse created =
                relationshipAnalysisService
                        .createRelationshipAnalysis(
                                buildRequest(
                                        customerId,
                                        transactionId,
                                        correlationId,
                                        "COMPLETED",
                                        "TRANSACTION_LINK"
                                )
                        );

        List<RelationshipAnalysisResponse> results =
                relationshipAnalysisService
                        .getAnalysesByTransaction(
                                transactionId
                        );

        assertEquals(
                1,
                results.size()
        );

        assertEquals(
                created.getRelationshipAnalysisId(),
                results.getFirst()
                        .getRelationshipAnalysisId()
        );

        assertEquals(
                transactionId,
                results.getFirst()
                        .getTransactionId()
        );
    }

    @Test
    void getAnalysesByCorrelationShouldReturnMatchingAnalysis() {

        RelationshipAnalysisResponse created =
                relationshipAnalysisService
                        .createRelationshipAnalysis(
                                buildRequest(
                                        customerId,
                                        transactionId,
                                        correlationId,
                                        "COMPLETED",
                                        "CORRELATION_LINK"
                                )
                        );

        List<RelationshipAnalysisResponse> results =
                relationshipAnalysisService
                        .getAnalysesByCorrelation(
                                correlationId
                        );

        assertEquals(
                1,
                results.size()
        );

        assertEquals(
                created.getRelationshipAnalysisId(),
                results.getFirst()
                        .getRelationshipAnalysisId()
        );

        assertEquals(
                correlationId,
                results.getFirst()
                        .getCorrelationId()
        );
    }

    @Test
    void getAnalysesByTypeShouldReturnMatchingAnalyses() {

        String relationshipType =
                "TYPE_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        RelationshipAnalysisResponse first =
                relationshipAnalysisService
                        .createRelationshipAnalysis(
                                buildRequest(
                                        customerId,
                                        transactionId,
                                        correlationId,
                                        "COMPLETED",
                                        relationshipType
                                )
                        );

        RelationshipAnalysisResponse second =
                relationshipAnalysisService
                        .createRelationshipAnalysis(
                                buildRequest(
                                        customerId,
                                        null,
                                        null,
                                        "PENDING",
                                        relationshipType
                                )
                        );

        List<RelationshipAnalysisResponse> results =
                relationshipAnalysisService
                        .getAnalysesByType(
                                relationshipType
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsAnalysis(
                        results,
                        first.getRelationshipAnalysisId()
                )
        );

        assertTrue(
                containsAnalysis(
                        results,
                        second.getRelationshipAnalysisId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                relationshipType.equals(
                                        result.getRelationshipType()
                                )
                        )
        );
    }

    @Test
    void getAnalysesBySourceShouldReturnMatchingAnalyses() {

        String sourceEntityKey =
                "SRC-" + UUID.randomUUID();

        RelationshipAnalysisRequest firstRequest =
                buildRequest(
                        customerId,
                        transactionId,
                        correlationId,
                        "COMPLETED",
                        "SOURCE_LINK"
                );

        firstRequest.setSourceEntityKey(
                sourceEntityKey
        );

        RelationshipAnalysisResponse first =
                relationshipAnalysisService
                        .createRelationshipAnalysis(
                                firstRequest
                        );

        RelationshipAnalysisRequest secondRequest =
                buildRequest(
                        customerId,
                        transactionId,
                        correlationId,
                        "COMPLETED",
                        "SOURCE_LINK"
                );

        secondRequest.setSourceEntityKey(
                sourceEntityKey
        );

        RelationshipAnalysisResponse second =
                relationshipAnalysisService
                        .createRelationshipAnalysis(
                                secondRequest
                        );

        List<RelationshipAnalysisResponse> results =
                relationshipAnalysisService
                        .getAnalysesBySource(
                                sourceEntityKey
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsAnalysis(
                        results,
                        first.getRelationshipAnalysisId()
                )
        );

        assertTrue(
                containsAnalysis(
                        results,
                        second.getRelationshipAnalysisId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                sourceEntityKey.equals(
                                        result.getSourceEntityKey()
                                )
                        )
        );
    }

    @Test
    void getAnalysesByTargetShouldReturnMatchingAnalyses() {

        String targetEntityKey =
                "TGT-" + UUID.randomUUID();

        RelationshipAnalysisRequest firstRequest =
                buildRequest(
                        customerId,
                        transactionId,
                        correlationId,
                        "COMPLETED",
                        "TARGET_LINK"
                );

        firstRequest.setTargetEntityKey(
                targetEntityKey
        );

        RelationshipAnalysisResponse first =
                relationshipAnalysisService
                        .createRelationshipAnalysis(
                                firstRequest
                        );

        RelationshipAnalysisRequest secondRequest =
                buildRequest(
                        customerId,
                        transactionId,
                        correlationId,
                        "COMPLETED",
                        "TARGET_LINK"
                );

        secondRequest.setTargetEntityKey(
                targetEntityKey
        );

        RelationshipAnalysisResponse second =
                relationshipAnalysisService
                        .createRelationshipAnalysis(
                                secondRequest
                        );

        List<RelationshipAnalysisResponse> results =
                relationshipAnalysisService
                        .getAnalysesByTarget(
                                targetEntityKey
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsAnalysis(
                        results,
                        first.getRelationshipAnalysisId()
                )
        );

        assertTrue(
                containsAnalysis(
                        results,
                        second.getRelationshipAnalysisId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                targetEntityKey.equals(
                                        result.getTargetEntityKey()
                                )
                        )
        );
    }

    @Test
    void getAnalysesByStatusShouldReturnMatchingAnalyses() {

        String status =
                "RA_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        RelationshipAnalysisResponse first =
                relationshipAnalysisService
                        .createRelationshipAnalysis(
                                buildRequest(
                                        customerId,
                                        transactionId,
                                        correlationId,
                                        status,
                                        "STATUS_LINK"
                                )
                        );

        RelationshipAnalysisResponse second =
                relationshipAnalysisService
                        .createRelationshipAnalysis(
                                buildRequest(
                                        customerId,
                                        null,
                                        null,
                                        status,
                                        "STATUS_LINK"
                                )
                        );

        List<RelationshipAnalysisResponse> results =
                relationshipAnalysisService
                        .getAnalysesByStatus(
                                status
                        );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsAnalysis(
                        results,
                        first.getRelationshipAnalysisId()
                )
        );

        assertTrue(
                containsAnalysis(
                        results,
                        second.getRelationshipAnalysisId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                status.equals(
                                        result.getAnalysisStatus()
                                )
                        )
        );
    }

    @Test
    void queryMethodsShouldReturnEmptyListsForUnknownValues() {

        assertTrue(
                relationshipAnalysisService
                        .getAnalysesByCustomer(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                relationshipAnalysisService
                        .getAnalysesByTransaction(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                relationshipAnalysisService
                        .getAnalysesByCorrelation(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                relationshipAnalysisService
                        .getAnalysesByType(
                                "UNKNOWN-" +
                                        UUID.randomUUID()
                                                .toString()
                                                .substring(0, 8)
                        )
                        .isEmpty()
        );

        assertTrue(
                relationshipAnalysisService
                        .getAnalysesBySource(
                                "UNKNOWN-" + UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                relationshipAnalysisService
                        .getAnalysesByTarget(
                                "UNKNOWN-" + UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                relationshipAnalysisService
                        .getAnalysesByStatus(
                                "UNKNOWN-" +
                                        UUID.randomUUID()
                                                .toString()
                                                .substring(0, 8)
                        )
                        .isEmpty()
        );
    }

    private RelationshipAnalysisRequest buildRequest(
            UUID targetCustomerId,
            UUID targetTransactionId,
            UUID targetCorrelationId,
            String analysisStatus,
            String relationshipType) {

        RelationshipAnalysisRequest request =
                new RelationshipAnalysisRequest();

        request.setCustomerId(
                targetCustomerId
        );

        request.setTransactionId(
                targetTransactionId
        );

        request.setCorrelationId(
                targetCorrelationId
        );

        request.setAnalysisStatus(
                analysisStatus
        );

        request.setRelationshipType(
                relationshipType
        );

        request.setSourceEntityType(
                "CUSTOMER"
        );

        request.setSourceEntityKey(
                "SRC-" + UUID.randomUUID()
        );

        request.setTargetEntityType(
                "DEVICE"
        );

        request.setTargetEntityKey(
                "TGT-" + UUID.randomUUID()
        );

        request.setRelationshipIndicators(
                Map.of(
                        "sharedDevice", true,
                        "linkedEntities", 4
                )
        );

        request.setAnalysisContext(
                Map.of(
                        "source", "SERVICE_TEST",
                        "channel", "WEB"
                )
        );

        return request;
    }

    private boolean containsAnalysis(
            List<RelationshipAnalysisResponse> results,
            UUID relationshipAnalysisId) {

        return results.stream()
                .anyMatch(result ->
                        relationshipAnalysisId.equals(
                                result.getRelationshipAnalysisId()
                        )
                );
    }
}