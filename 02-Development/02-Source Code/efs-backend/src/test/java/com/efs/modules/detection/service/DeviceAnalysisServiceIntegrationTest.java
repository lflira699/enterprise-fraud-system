package com.efs.modules.detection.service;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.detection.dto.DeviceAnalysisRequest;
import com.efs.modules.detection.dto.DeviceAnalysisResponse;
import com.efs.modules.detection.entity.Correlation;
import com.efs.modules.detection.repository.CorrelationRepository;
import com.efs.modules.detection.repository.DeviceAnalysisRepository;
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
class DeviceAnalysisServiceIntegrationTest {

    @Autowired
    private DeviceAnalysisServiceInterface deviceAnalysisService;

    @Autowired
    private DeviceAnalysisRepository deviceAnalysisRepository;

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
                "DA-SVC-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Device"
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
                "EFS-DA-SVC-" + UUID.randomUUID()
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
                new BigDecimal("900.00")
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
                "DA-CORR-" + UUID.randomUUID()
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
    void createDeviceAnalysisShouldPersistProvidedValuesAndGenerateTimestamps() {

        DeviceAnalysisRequest request =
                buildRequest(
                        customerId,
                        transactionId,
                        correlationId,
                        "COMPLETED"
                );

        DeviceAnalysisResponse response =
                deviceAnalysisService.createDeviceAnalysis(
                        request
                );

        assertNotNull(
                response
        );

        assertNotNull(
                response.getDeviceAnalysisId()
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
                request.getDeviceId(),
                response.getDeviceId()
        );

        assertEquals(
                request.getDeviceFingerprint(),
                response.getDeviceFingerprint()
        );

        assertEquals(
                "MOBILE",
                response.getDeviceType()
        );

        assertEquals(
                "ANDROID",
                response.getOperatingSystem()
        );

        assertEquals(
                "CHROME",
                response.getBrowser()
        );

        assertEquals(
                "10.107.10.25",
                response.getIpAddress()
        );

        assertNotNull(
                response.getGeolocationContext()
        );

        assertEquals(
                "GT",
                response.getGeolocationContext()
                        .get("country")
        );

        assertEquals(
                "Guatemala",
                response.getGeolocationContext()
                        .get("city")
        );

        assertNotNull(
                response.getDeviceIndicators()
        );

        assertEquals(
                true,
                response.getDeviceIndicators()
                        .get("knownDevice")
        );

        assertEquals(
                8,
                ((Number) response.getDeviceIndicators()
                        .get("historicalMatches"))
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

        assertNull(
                response.getDeviceConfidence()
        );

        assertNotNull(
                response.getAnalyzedAt()
        );

        assertNotNull(
                response.getCreatedAt()
        );

        assertTrue(
                deviceAnalysisRepository.existsById(
                        response.getDeviceAnalysisId()
                )
        );
    }

    @Test
    void createDeviceAnalysisShouldAllowOptionalReferencesAndDeviceAttributes() {

        DeviceAnalysisRequest request =
                new DeviceAnalysisRequest();

        request.setAnalysisStatus(
                "COMPLETED"
        );

        DeviceAnalysisResponse response =
                deviceAnalysisService.createDeviceAnalysis(
                        request
                );

        assertNotNull(
                response.getDeviceAnalysisId()
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

        assertNull(
                response.getDeviceId()
        );

        assertNull(
                response.getDeviceFingerprint()
        );

        assertNull(
                response.getDeviceType()
        );

        assertNull(
                response.getOperatingSystem()
        );

        assertNull(
                response.getBrowser()
        );

        assertNull(
                response.getIpAddress()
        );

        assertNull(
                response.getGeolocationContext()
        );

        assertNull(
                response.getDeviceIndicators()
        );

        assertNull(
                response.getAnalysisContext()
        );

        assertNotNull(
                response.getAnalyzedAt()
        );

        assertNotNull(
                response.getCreatedAt()
        );
    }

    @Test
    void getDeviceAnalysisByIdShouldReturnExistingAnalysis() {

        DeviceAnalysisResponse created =
                deviceAnalysisService.createDeviceAnalysis(
                        buildRequest(
                                customerId,
                                transactionId,
                                correlationId,
                                "COMPLETED"
                        )
                );

        DeviceAnalysisResponse found =
                deviceAnalysisService.getDeviceAnalysisById(
                        created.getDeviceAnalysisId()
                );

        assertEquals(
                created.getDeviceAnalysisId(),
                found.getDeviceAnalysisId()
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
                created.getDeviceId(),
                found.getDeviceId()
        );

        assertEquals(
                created.getDeviceFingerprint(),
                found.getDeviceFingerprint()
        );

        assertEquals(
                created.getIpAddress(),
                found.getIpAddress()
        );
    }

    @Test
    void getDeviceAnalysisByIdShouldThrowWhenAnalysisDoesNotExist() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> deviceAnalysisService
                        .getDeviceAnalysisById(
                                UUID.randomUUID()
                        )
        );
    }

    @Test
    void getAnalysesByCustomerShouldReturnMatchingAnalyses() {

        DeviceAnalysisResponse first =
                deviceAnalysisService.createDeviceAnalysis(
                        buildRequest(
                                customerId,
                                transactionId,
                                correlationId,
                                "COMPLETED"
                        )
                );

        DeviceAnalysisResponse second =
                deviceAnalysisService.createDeviceAnalysis(
                        buildRequest(
                                customerId,
                                null,
                                null,
                                "PENDING"
                        )
                );

        List<DeviceAnalysisResponse> results =
                deviceAnalysisService.getAnalysesByCustomer(
                        customerId
                );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsAnalysis(
                        results,
                        first.getDeviceAnalysisId()
                )
        );

        assertTrue(
                containsAnalysis(
                        results,
                        second.getDeviceAnalysisId()
                )
        );
    }

    @Test
    void getAnalysesByTransactionShouldReturnMatchingAnalysis() {

        DeviceAnalysisResponse created =
                deviceAnalysisService.createDeviceAnalysis(
                        buildRequest(
                                customerId,
                                transactionId,
                                correlationId,
                                "COMPLETED"
                        )
                );

        List<DeviceAnalysisResponse> results =
                deviceAnalysisService.getAnalysesByTransaction(
                        transactionId
                );

        assertEquals(
                1,
                results.size()
        );

        assertEquals(
                created.getDeviceAnalysisId(),
                results.getFirst().getDeviceAnalysisId()
        );

        assertEquals(
                transactionId,
                results.getFirst().getTransactionId()
        );
    }

    @Test
    void getAnalysesByCorrelationShouldReturnMatchingAnalysis() {

        DeviceAnalysisResponse created =
                deviceAnalysisService.createDeviceAnalysis(
                        buildRequest(
                                customerId,
                                transactionId,
                                correlationId,
                                "COMPLETED"
                        )
                );

        List<DeviceAnalysisResponse> results =
                deviceAnalysisService.getAnalysesByCorrelation(
                        correlationId
                );

        assertEquals(
                1,
                results.size()
        );

        assertEquals(
                created.getDeviceAnalysisId(),
                results.getFirst().getDeviceAnalysisId()
        );

        assertEquals(
                correlationId,
                results.getFirst().getCorrelationId()
        );
    }

    @Test
    void getAnalysesByDeviceIdShouldReturnMatchingAnalyses() {

        String deviceId =
                "DEVICE-" + UUID.randomUUID();

        DeviceAnalysisRequest firstRequest =
                buildRequest(
                        customerId,
                        transactionId,
                        correlationId,
                        "COMPLETED"
                );

        firstRequest.setDeviceId(
                deviceId
        );

        DeviceAnalysisResponse first =
                deviceAnalysisService.createDeviceAnalysis(
                        firstRequest
                );

        DeviceAnalysisRequest secondRequest =
                buildRequest(
                        customerId,
                        transactionId,
                        correlationId,
                        "COMPLETED"
                );

        secondRequest.setDeviceId(
                deviceId
        );

        DeviceAnalysisResponse second =
                deviceAnalysisService.createDeviceAnalysis(
                        secondRequest
                );

        List<DeviceAnalysisResponse> results =
                deviceAnalysisService.getAnalysesByDeviceId(
                        deviceId
                );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsAnalysis(
                        results,
                        first.getDeviceAnalysisId()
                )
        );

        assertTrue(
                containsAnalysis(
                        results,
                        second.getDeviceAnalysisId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                deviceId.equals(
                                        result.getDeviceId()
                                )
                        )
        );
    }

    @Test
    void getAnalysesByFingerprintShouldReturnMatchingAnalyses() {

        String fingerprint =
                "FP-" + UUID.randomUUID();

        DeviceAnalysisRequest firstRequest =
                buildRequest(
                        customerId,
                        transactionId,
                        correlationId,
                        "COMPLETED"
                );

        firstRequest.setDeviceFingerprint(
                fingerprint
        );

        DeviceAnalysisResponse first =
                deviceAnalysisService.createDeviceAnalysis(
                        firstRequest
                );

        DeviceAnalysisRequest secondRequest =
                buildRequest(
                        customerId,
                        transactionId,
                        correlationId,
                        "COMPLETED"
                );

        secondRequest.setDeviceFingerprint(
                fingerprint
        );

        DeviceAnalysisResponse second =
                deviceAnalysisService.createDeviceAnalysis(
                        secondRequest
                );

        List<DeviceAnalysisResponse> results =
                deviceAnalysisService.getAnalysesByFingerprint(
                        fingerprint
                );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsAnalysis(
                        results,
                        first.getDeviceAnalysisId()
                )
        );

        assertTrue(
                containsAnalysis(
                        results,
                        second.getDeviceAnalysisId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                fingerprint.equals(
                                        result.getDeviceFingerprint()
                                )
                        )
        );
    }

    @Test
    void getAnalysesByIpAddressShouldReturnMatchingAnalyses() {

        String ipAddress =
                "10.107.20.55";

        DeviceAnalysisRequest firstRequest =
                buildRequest(
                        customerId,
                        transactionId,
                        correlationId,
                        "COMPLETED"
                );

        firstRequest.setIpAddress(
                ipAddress
        );

        DeviceAnalysisResponse first =
                deviceAnalysisService.createDeviceAnalysis(
                        firstRequest
                );

        DeviceAnalysisRequest secondRequest =
                buildRequest(
                        customerId,
                        transactionId,
                        correlationId,
                        "COMPLETED"
                );

        secondRequest.setIpAddress(
                ipAddress
        );

        DeviceAnalysisResponse second =
                deviceAnalysisService.createDeviceAnalysis(
                        secondRequest
                );

        List<DeviceAnalysisResponse> results =
                deviceAnalysisService.getAnalysesByIpAddress(
                        ipAddress
                );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsAnalysis(
                        results,
                        first.getDeviceAnalysisId()
                )
        );

        assertTrue(
                containsAnalysis(
                        results,
                        second.getDeviceAnalysisId()
                )
        );

        assertTrue(
                results.stream()
                        .allMatch(result ->
                                ipAddress.equals(
                                        result.getIpAddress()
                                )
                        )
        );
    }

    @Test
    void getAnalysesByStatusShouldReturnMatchingAnalyses() {

        String status =
                "DA_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        DeviceAnalysisResponse first =
                deviceAnalysisService.createDeviceAnalysis(
                        buildRequest(
                                customerId,
                                transactionId,
                                correlationId,
                                status
                        )
                );

        DeviceAnalysisResponse second =
                deviceAnalysisService.createDeviceAnalysis(
                        buildRequest(
                                customerId,
                                null,
                                null,
                                status
                        )
                );

        List<DeviceAnalysisResponse> results =
                deviceAnalysisService.getAnalysesByStatus(
                        status
                );

        assertEquals(
                2,
                results.size()
        );

        assertTrue(
                containsAnalysis(
                        results,
                        first.getDeviceAnalysisId()
                )
        );

        assertTrue(
                containsAnalysis(
                        results,
                        second.getDeviceAnalysisId()
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
                deviceAnalysisService
                        .getAnalysesByCustomer(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                deviceAnalysisService
                        .getAnalysesByTransaction(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                deviceAnalysisService
                        .getAnalysesByCorrelation(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                deviceAnalysisService
                        .getAnalysesByDeviceId(
                                "UNKNOWN-" + UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                deviceAnalysisService
                        .getAnalysesByFingerprint(
                                "UNKNOWN-" + UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                deviceAnalysisService
                        .getAnalysesByIpAddress(
                                "10.255.255.254"
                        )
                        .isEmpty()
        );

        assertTrue(
                deviceAnalysisService
                        .getAnalysesByStatus(
                                "UNKNOWN-" +
                                        UUID.randomUUID()
                                                .toString()
                                                .substring(0, 8)
                        )
                        .isEmpty()
        );
    }

    private DeviceAnalysisRequest buildRequest(
            UUID targetCustomerId,
            UUID targetTransactionId,
            UUID targetCorrelationId,
            String analysisStatus) {

        DeviceAnalysisRequest request =
                new DeviceAnalysisRequest();

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

        request.setDeviceId(
                "DEVICE-" + UUID.randomUUID()
        );

        request.setDeviceFingerprint(
                "FP-" + UUID.randomUUID()
        );

        request.setDeviceType(
                "MOBILE"
        );

        request.setOperatingSystem(
                "ANDROID"
        );

        request.setBrowser(
                "CHROME"
        );

        request.setIpAddress(
                "10.107.10.25"
        );

        request.setGeolocationContext(
                Map.of(
                        "country", "GT",
                        "city", "Guatemala"
                )
        );

        request.setDeviceIndicators(
                Map.of(
                        "knownDevice", true,
                        "historicalMatches", 8
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
            List<DeviceAnalysisResponse> results,
            UUID deviceAnalysisId) {

        return results.stream()
                .anyMatch(result ->
                        deviceAnalysisId.equals(
                                result.getDeviceAnalysisId()
                        )
                );
    }
}