package com.efs.modules.detection.repository;

import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.detection.entity.Correlation;
import com.efs.modules.detection.entity.DeviceAnalysis;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class DeviceAnalysisRepositoryIntegrationTest {

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

        LocalDateTime now = LocalDateTime.now();

        Customer customer = new Customer();

        customer.setCustomerNumber(
                "V107-" + UUID.randomUUID()
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
                customerRepository.saveAndFlush(customer);

        customerId =
                savedCustomer.getCustomerId();

        Transaction transaction = new Transaction();

        transaction.setTransactionReference(
                "EFS-V107-TXN-" + UUID.randomUUID()
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
                transactionRepository.saveAndFlush(transaction);

        transactionId =
                savedTransaction.getTransactionId();

        Correlation correlation = new Correlation();

        correlation.setCustomerId(
                customerId
        );

        correlation.setTransactionId(
                transactionId
        );

        correlation.setCorrelationKey(
                "V107-CORR-" + UUID.randomUUID()
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
                correlationRepository.saveAndFlush(correlation);

        correlationId =
                savedCorrelation.getCorrelationId();
    }

    @Test
    void shouldSaveAndFindDeviceAnalysisById() {

        DeviceAnalysis analysis =
                createDeviceAnalysis(
                        "COMPLETED",
                        "DEVICE-" + UUID.randomUUID(),
                        "FP-" + UUID.randomUUID(),
                        "10.107.0.1",
                        LocalDateTime.now()
                );

        DeviceAnalysis saved =
                deviceAnalysisRepository.saveAndFlush(analysis);

        assertNotNull(
                saved.getDeviceAnalysisId()
        );

        Optional<DeviceAnalysis> result =
                deviceAnalysisRepository
                        .findByDeviceAnalysisId(
                                saved.getDeviceAnalysisId()
                        );

        assertTrue(
                result.isPresent()
        );

        DeviceAnalysis found = result.get();

        assertEquals(
                saved.getDeviceAnalysisId(),
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
                "COMPLETED",
                found.getAnalysisStatus()
        );
    }

    @Test
    void shouldPersistDeviceAttributesAndJsonFields() {

        DeviceAnalysis analysis =
                createDeviceAnalysis(
                        "COMPLETED",
                        "DEVICE-" + UUID.randomUUID(),
                        "FP-" + UUID.randomUUID(),
                        "192.168.107.10",
                        LocalDateTime.now()
                );

        analysis.setDeviceType(
                "MOBILE"
        );

        analysis.setOperatingSystem(
                "ANDROID"
        );

        analysis.setBrowser(
                "CHROME"
        );

        analysis.setDeviceConfidence(
                new BigDecimal("0.8750")
        );

        analysis.setGeolocationContext(
                Map.of(
                        "country", "GT",
                        "city", "Guatemala"
                )
        );

        analysis.setDeviceIndicators(
                Map.of(
                        "knownDevice", true,
                        "historicalMatches", 8
                )
        );

        analysis.setAnalysisContext(
                Map.of(
                        "channel", "WEB",
                        "source", "V107_TEST"
                )
        );

        DeviceAnalysis saved =
                deviceAnalysisRepository.saveAndFlush(analysis);

        DeviceAnalysis found =
                deviceAnalysisRepository
                        .findById(saved.getDeviceAnalysisId())
                        .orElseThrow();

        assertEquals(
                "MOBILE",
                found.getDeviceType()
        );

        assertEquals(
                "ANDROID",
                found.getOperatingSystem()
        );

        assertEquals(
                "CHROME",
                found.getBrowser()
        );

        assertEquals(
                new BigDecimal("0.8750"),
                found.getDeviceConfidence()
        );

        assertEquals(
                "GT",
                found.getGeolocationContext().get("country")
        );

        assertEquals(
                "Guatemala",
                found.getGeolocationContext().get("city")
        );

        assertEquals(
                true,
                found.getDeviceIndicators().get("knownDevice")
        );

        assertEquals(
                8,
                ((Number) found.getDeviceIndicators()
                        .get("historicalMatches")).intValue()
        );

        assertEquals(
                "WEB",
                found.getAnalysisContext().get("channel")
        );

        assertEquals(
                "V107_TEST",
                found.getAnalysisContext().get("source")
        );
    }

    @Test
    void shouldFindByCustomerIdOrderedByAnalyzedAtDescending() {

        LocalDateTime now = LocalDateTime.now();

        DeviceAnalysis older =
                deviceAnalysisRepository.saveAndFlush(
                        createDeviceAnalysis(
                                "COMPLETED",
                                "DEVICE-OLD-" + UUID.randomUUID(),
                                "FP-OLD-" + UUID.randomUUID(),
                                "10.107.1.1",
                                now.minusMinutes(10)
                        )
                );

        DeviceAnalysis newer =
                deviceAnalysisRepository.saveAndFlush(
                        createDeviceAnalysis(
                                "COMPLETED",
                                "DEVICE-NEW-" + UUID.randomUUID(),
                                "FP-NEW-" + UUID.randomUUID(),
                                "10.107.1.2",
                                now
                        )
                );

        List<DeviceAnalysis> results =
                deviceAnalysisRepository
                        .findByCustomerIdOrderByAnalyzedAtDesc(
                                customerId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getDeviceAnalysisId(),
                results.get(0).getDeviceAnalysisId()
        );

        assertEquals(
                older.getDeviceAnalysisId(),
                results.get(1).getDeviceAnalysisId()
        );
    }

    @Test
    void shouldFindByTransactionIdOrderedByAnalyzedAtDescending() {

        LocalDateTime now = LocalDateTime.now();

        DeviceAnalysis older =
                deviceAnalysisRepository.saveAndFlush(
                        createDeviceAnalysis(
                                "COMPLETED",
                                "DEVICE-TXN-OLD-" + UUID.randomUUID(),
                                "FP-TXN-OLD-" + UUID.randomUUID(),
                                "10.107.2.1",
                                now.minusMinutes(15)
                        )
                );

        DeviceAnalysis newer =
                deviceAnalysisRepository.saveAndFlush(
                        createDeviceAnalysis(
                                "COMPLETED",
                                "DEVICE-TXN-NEW-" + UUID.randomUUID(),
                                "FP-TXN-NEW-" + UUID.randomUUID(),
                                "10.107.2.2",
                                now
                        )
                );

        List<DeviceAnalysis> results =
                deviceAnalysisRepository
                        .findByTransactionIdOrderByAnalyzedAtDesc(
                                transactionId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getDeviceAnalysisId(),
                results.get(0).getDeviceAnalysisId()
        );

        assertEquals(
                older.getDeviceAnalysisId(),
                results.get(1).getDeviceAnalysisId()
        );
    }

    @Test
    void shouldFindByCorrelationIdOrderedByAnalyzedAtDescending() {

        LocalDateTime now = LocalDateTime.now();

        DeviceAnalysis older =
                deviceAnalysisRepository.saveAndFlush(
                        createDeviceAnalysis(
                                "COMPLETED",
                                "DEVICE-CORR-OLD-" + UUID.randomUUID(),
                                "FP-CORR-OLD-" + UUID.randomUUID(),
                                "10.107.3.1",
                                now.minusMinutes(20)
                        )
                );

        DeviceAnalysis newer =
                deviceAnalysisRepository.saveAndFlush(
                        createDeviceAnalysis(
                                "COMPLETED",
                                "DEVICE-CORR-NEW-" + UUID.randomUUID(),
                                "FP-CORR-NEW-" + UUID.randomUUID(),
                                "10.107.3.2",
                                now
                        )
                );

        List<DeviceAnalysis> results =
                deviceAnalysisRepository
                        .findByCorrelationIdOrderByAnalyzedAtDesc(
                                correlationId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getDeviceAnalysisId(),
                results.get(0).getDeviceAnalysisId()
        );

        assertEquals(
                older.getDeviceAnalysisId(),
                results.get(1).getDeviceAnalysisId()
        );
    }

    @Test
    void shouldFindByDeviceIdOrderedByAnalyzedAtDescending() {

        String deviceId =
                "V107-DEVICE-" + UUID.randomUUID();

        LocalDateTime now = LocalDateTime.now();

        DeviceAnalysis older =
                deviceAnalysisRepository.saveAndFlush(
                        createDeviceAnalysis(
                                "COMPLETED",
                                deviceId,
                                "FP-DEVICE-OLD-" + UUID.randomUUID(),
                                "10.107.4.1",
                                now.minusMinutes(5)
                        )
                );

        DeviceAnalysis newer =
                deviceAnalysisRepository.saveAndFlush(
                        createDeviceAnalysis(
                                "COMPLETED",
                                deviceId,
                                "FP-DEVICE-NEW-" + UUID.randomUUID(),
                                "10.107.4.2",
                                now
                        )
                );

        List<DeviceAnalysis> results =
                deviceAnalysisRepository
                        .findByDeviceIdOrderByAnalyzedAtDesc(
                                deviceId
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getDeviceAnalysisId(),
                results.get(0).getDeviceAnalysisId()
        );

        assertEquals(
                older.getDeviceAnalysisId(),
                results.get(1).getDeviceAnalysisId()
        );
    }

    @Test
    void shouldFindByDeviceFingerprintOrderedByAnalyzedAtDescending() {

        String fingerprint =
                "V107-FP-" + UUID.randomUUID();

        LocalDateTime now = LocalDateTime.now();

        DeviceAnalysis older =
                deviceAnalysisRepository.saveAndFlush(
                        createDeviceAnalysis(
                                "COMPLETED",
                                "DEVICE-FP-OLD-" + UUID.randomUUID(),
                                fingerprint,
                                "10.107.5.1",
                                now.minusMinutes(5)
                        )
                );

        DeviceAnalysis newer =
                deviceAnalysisRepository.saveAndFlush(
                        createDeviceAnalysis(
                                "COMPLETED",
                                "DEVICE-FP-NEW-" + UUID.randomUUID(),
                                fingerprint,
                                "10.107.5.2",
                                now
                        )
                );

        List<DeviceAnalysis> results =
                deviceAnalysisRepository
                        .findByDeviceFingerprintOrderByAnalyzedAtDesc(
                                fingerprint
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getDeviceAnalysisId(),
                results.get(0).getDeviceAnalysisId()
        );

        assertEquals(
                older.getDeviceAnalysisId(),
                results.get(1).getDeviceAnalysisId()
        );
    }

    @Test
    void shouldFindByIpAddressOrderedByAnalyzedAtDescending() {

        String ipAddress =
                "10.107.6." + (10 + Math.abs(
                        UUID.randomUUID().hashCode() % 200
                ));

        LocalDateTime now = LocalDateTime.now();

        DeviceAnalysis older =
                deviceAnalysisRepository.saveAndFlush(
                        createDeviceAnalysis(
                                "COMPLETED",
                                "DEVICE-IP-OLD-" + UUID.randomUUID(),
                                "FP-IP-OLD-" + UUID.randomUUID(),
                                ipAddress,
                                now.minusMinutes(5)
                        )
                );

        DeviceAnalysis newer =
                deviceAnalysisRepository.saveAndFlush(
                        createDeviceAnalysis(
                                "COMPLETED",
                                "DEVICE-IP-NEW-" + UUID.randomUUID(),
                                "FP-IP-NEW-" + UUID.randomUUID(),
                                ipAddress,
                                now
                        )
                );

        List<DeviceAnalysis> results =
                deviceAnalysisRepository
                        .findByIpAddressOrderByAnalyzedAtDesc(
                                ipAddress
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getDeviceAnalysisId(),
                results.get(0).getDeviceAnalysisId()
        );

        assertEquals(
                older.getDeviceAnalysisId(),
                results.get(1).getDeviceAnalysisId()
        );
    }

    @Test
    void shouldFindByAnalysisStatusOrderedByAnalyzedAtDescending() {

        String status =
                "V107_STATUS_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        LocalDateTime now = LocalDateTime.now();

        DeviceAnalysis older =
                deviceAnalysisRepository.saveAndFlush(
                        createDeviceAnalysis(
                                status,
                                "DEVICE-STATUS-OLD-" + UUID.randomUUID(),
                                "FP-STATUS-OLD-" + UUID.randomUUID(),
                                "10.107.7.1",
                                now.minusMinutes(5)
                        )
                );

        DeviceAnalysis newer =
                deviceAnalysisRepository.saveAndFlush(
                        createDeviceAnalysis(
                                status,
                                "DEVICE-STATUS-NEW-" + UUID.randomUUID(),
                                "FP-STATUS-NEW-" + UUID.randomUUID(),
                                "10.107.7.2",
                                now
                        )
                );

        List<DeviceAnalysis> results =
                deviceAnalysisRepository
                        .findByAnalysisStatusOrderByAnalyzedAtDesc(
                                status
                        );

        assertEquals(
                2,
                results.size()
        );

        assertEquals(
                newer.getDeviceAnalysisId(),
                results.get(0).getDeviceAnalysisId()
        );

        assertEquals(
                older.getDeviceAnalysisId(),
                results.get(1).getDeviceAnalysisId()
        );
    }

    @Test
    void shouldAllowOptionalReferencesAndDeviceAttributes() {

        DeviceAnalysis analysis = new DeviceAnalysis();

        analysis.setAnalysisStatus(
                "COMPLETED"
        );

        analysis.setAnalyzedAt(
                LocalDateTime.now()
        );

        analysis.setCreatedAt(
                LocalDateTime.now()
        );

        DeviceAnalysis saved =
                deviceAnalysisRepository.saveAndFlush(analysis);

        assertNotNull(
                saved.getDeviceAnalysisId()
        );

        assertNull(
                saved.getCustomerId()
        );

        assertNull(
                saved.getTransactionId()
        );

        assertNull(
                saved.getCorrelationId()
        );

        assertNull(
                saved.getDeviceId()
        );

        assertNull(
                saved.getDeviceFingerprint()
        );

        assertNull(
                saved.getIpAddress()
        );
    }

    @Test
    void shouldReturnEmptyResultsForUnknownValues() {

        assertTrue(
                deviceAnalysisRepository
                        .findByDeviceAnalysisId(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                deviceAnalysisRepository
                        .findByCustomerIdOrderByAnalyzedAtDesc(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                deviceAnalysisRepository
                        .findByTransactionIdOrderByAnalyzedAtDesc(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                deviceAnalysisRepository
                        .findByCorrelationIdOrderByAnalyzedAtDesc(
                                UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                deviceAnalysisRepository
                        .findByDeviceIdOrderByAnalyzedAtDesc(
                                "UNKNOWN-" + UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                deviceAnalysisRepository
                        .findByDeviceFingerprintOrderByAnalyzedAtDesc(
                                "UNKNOWN-" + UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                deviceAnalysisRepository
                        .findByIpAddressOrderByAnalyzedAtDesc(
                                "UNKNOWN-" + UUID.randomUUID()
                        )
                        .isEmpty()
        );

        assertTrue(
                deviceAnalysisRepository
                        .findByAnalysisStatusOrderByAnalyzedAtDesc(
                                "UNKNOWN-" + UUID.randomUUID()
                        )
                        .isEmpty()
        );
    }

    private DeviceAnalysis createDeviceAnalysis(
            String analysisStatus,
            String deviceId,
            String deviceFingerprint,
            String ipAddress,
            LocalDateTime analyzedAt) {

        DeviceAnalysis analysis =
                new DeviceAnalysis();

        analysis.setCustomerId(
                customerId
        );

        analysis.setTransactionId(
                transactionId
        );

        analysis.setCorrelationId(
                correlationId
        );

        analysis.setAnalysisStatus(
                analysisStatus
        );

        analysis.setDeviceId(
                deviceId
        );

        analysis.setDeviceFingerprint(
                deviceFingerprint
        );

        analysis.setIpAddress(
                ipAddress
        );

        analysis.setAnalyzedAt(
                analyzedAt
        );

        analysis.setCreatedAt(
                analyzedAt
        );

        return analysis;
    }
}