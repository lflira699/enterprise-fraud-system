package com.efs.modules.risk.service;

import com.efs.modules.customer.dto.CustomerRiskProfileRequest;
import com.efs.modules.customer.dto.CustomerRiskProfileResponse;
import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.integration.event.DomainEventRoutingKeyResolver;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class CustomerRiskAssessmentAuditOutboxIntegrationTest {

    private static final UUID ACTOR_ID =
            UUID.fromString(
                    "31313131-3131-3131-3131-313131313131"
            );

    @Autowired
    private CustomerRiskAssessmentServiceInterface service;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldRecordSuccessfulAuditAndRiskProfileChangedOutbox() {

        UUID customerId =
                createCustomer();

        UUID correlationId =
                UUID.randomUUID();

        CustomerRiskProfileResponse response =
                service.createRiskAssessment(
                        customerId,
                        request(
                                correlationId,
                                "50"
                        )
                );

        entityManager.flush();

        Integer auditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE event_type =
                              'CUSTOMER_RISK_ASSESSED'
                          AND entity_type =
                              'CUSTOMER_RISK_PROFILE'
                          AND entity_id = ?
                          AND action = 'CALCULATE'
                          AND source_component =
                              'RISK_ENGINE'
                          AND correlation_id = ?
                          AND event_result = 'SUCCESS'
                          AND event_details
                              ->> 'customerId' = ?
                          AND event_details
                              ->> 'profileId' = ?
                          AND event_details
                              ->> 'modelName'
                              = 'EFS-CUSTOMER-RISK'
                          AND event_details
                              ->> 'modelVersion' = '1.0'
                          AND (
                              event_details
                                  ->> 'currentRiskScore'
                          )::numeric = 50
                          AND event_details
                              ->> 'riskLevel'
                              = 'MEDIUM'
                          AND event_details
                              ->> 'reused'
                              = 'false'
                          AND jsonb_exists(event_details -> 'factorScores', 'BEHAVIOR')
                          AND jsonb_exists(event_details -> 'factorScores', 'WATCHLIST')
                          AND jsonb_exists(event_details -> 'factorWeights', 'BEHAVIOR')
                          AND jsonb_exists(event_details -> 'factorWeights', 'WATCHLIST')
                        """,
                        Integer.class,
                        response.getProfileId(),
                        correlationId,
                        customerId.toString(),
                        response.getProfileId().toString()
                );

        assertEquals(
                1,
                auditCount
        );

        Integer outboxCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM integration.outbox_event
                        WHERE aggregate_type =
                              'CustomerRiskProfile'
                          AND aggregate_id = ?
                          AND event_type =
                              'RiskProfileChanged'
                          AND correlation_id = ?
                          AND payload
                              ->> 'messageId'
                              IS NOT NULL
                          AND payload
                              ->> 'eventType'
                              = 'RiskProfileChanged'
                          AND payload
                              ->> 'schemaVersion'
                              = '1.0'
                          AND payload
                              ->> 'producer'
                              = 'RISK_ENGINE'
                          AND payload
                              ->> 'correlationId'
                              = ?
                          AND payload
                              ->> 'causationId'
                              IS NULL
                          AND payload
                              ->> 'tenantId'
                              IS NULL
                          AND payload
                              -> 'payload'
                              ->> 'customerId' = ?
                          AND payload
                              -> 'payload'
                              ->> 'riskProfileId' = ?
                          AND (
                              payload
                                  -> 'payload'
                                  ->> 'currentRiskScore'
                          )::numeric = 50
                          AND payload
                              -> 'payload'
                              ->> 'riskLevel'
                              = 'MEDIUM'
                          AND payload
                              -> 'payload'
                              ->> 'modelName'
                              = 'EFS-CUSTOMER-RISK'
                          AND payload
                              -> 'payload'
                              ->> 'modelVersion'
                              = '1.0'
                        """,
                        Integer.class,
                        response.getProfileId(),
                        correlationId,
                        correlationId.toString(),
                        customerId.toString(),
                        response.getProfileId().toString()
                );

        assertEquals(
                1,
                outboxCount
        );
    }

    @Test
    void shouldPublishPreviousAndCurrentRiskOnUpdate() {

        UUID customerId =
                createCustomer();

        CustomerRiskProfileResponse created =
                service.createRiskAssessment(
                        customerId,
                        request(
                                UUID.randomUUID(),
                                "20"
                        )
                );

        UUID correlationId =
                UUID.randomUUID();

        service.updateRiskAssessment(
                customerId,
                request(
                        correlationId,
                        "80"
                )
        );

        entityManager.flush();

        Integer eventCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM integration.outbox_event
                        WHERE aggregate_type =
                              'CustomerRiskProfile'
                          AND aggregate_id = ?
                          AND event_type =
                              'RiskProfileChanged'
                          AND correlation_id = ?
                          AND (
                              payload
                                  -> 'payload'
                                  ->> 'previousRiskScore'
                          )::numeric = 20
                          AND payload
                              -> 'payload'
                              ->> 'previousRiskLevel'
                              = 'LOW'
                          AND (
                              payload
                                  -> 'payload'
                                  ->> 'currentRiskScore'
                          )::numeric = 80
                          AND payload
                              -> 'payload'
                              ->> 'riskLevel'
                              = 'CRITICAL'
                        """,
                        Integer.class,
                        created.getProfileId(),
                        correlationId
                );

        assertEquals(
                1,
                eventCount
        );
    }

    @Test
    void shouldAuditReuseWithoutSecondHistoryOrDomainEvent() {

        UUID customerId =
                createCustomer();

        UUID correlationId =
                UUID.randomUUID();

        CustomerRiskProfileResponse first =
                service.createRiskAssessment(
                        customerId,
                        request(
                                correlationId,
                                "30"
                        )
                );

        CustomerRiskProfileResponse reused =
                service.createRiskAssessment(
                        customerId,
                        request(
                                correlationId,
                                "90"
                        )
                );

        entityManager.flush();

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

        Integer outboxCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM integration.outbox_event
                        WHERE aggregate_type =
                              'CustomerRiskProfile'
                          AND aggregate_id = ?
                          AND event_type =
                              'RiskProfileChanged'
                          AND correlation_id = ?
                        """,
                        Integer.class,
                        first.getProfileId(),
                        correlationId
                );

        assertEquals(
                1,
                outboxCount
        );

        Integer historyCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM customer.customer_history
                        WHERE customer_id = ?
                          AND event_type =
                              'CUSTOMER_RISK_ASSESSED'
                          AND source_reference = ?
                        """,
                        Integer.class,
                        customerId,
                        correlationId.toString()
                );

        assertEquals(
                1,
                historyCount
        );

        Integer auditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE event_type =
                              'CUSTOMER_RISK_ASSESSED'
                          AND entity_type =
                              'CUSTOMER_RISK_PROFILE'
                          AND entity_id = ?
                          AND correlation_id = ?
                          AND event_result = 'SUCCESS'
                        """,
                        Integer.class,
                        first.getProfileId(),
                        correlationId
                );

        assertEquals(
                2,
                auditCount
        );

        Integer reusedAuditCount =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM audit.audit_event
                        WHERE event_type =
                              'CUSTOMER_RISK_ASSESSED'
                          AND entity_id = ?
                          AND correlation_id = ?
                          AND event_result = 'SUCCESS'
                          AND event_details
                              ->> 'reused'
                              = 'true'
                        """,
                        Integer.class,
                        first.getProfileId(),
                        correlationId
                );

        assertEquals(
                1,
                reusedAuditCount
        );
    }

    @Test
    void shouldResolveRiskProfileChangedRoutingKey() {

        DomainEventRoutingKeyResolver resolver =
                new DomainEventRoutingKeyResolver();

        assertEquals(
                "risk.profile.changed.v1",
                resolver.resolve(
                        "RiskProfileChanged"
                )
        );
    }

    private UUID createCustomer() {

        LocalDateTime now =
                LocalDateTime.now();

        Customer customer =
                new Customer();

        customer.setCustomerNumber(
                "UC030-AUDIT-"
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
        customer.setCreatedBy(ACTOR_ID);
        customer.setUpdatedBy(ACTOR_ID);

        return customerRepository
                .saveAndFlush(customer)
                .getCustomerId();
    }

    private CustomerRiskProfileRequest request(
            UUID correlationId,
            String score) {

        BigDecimal value =
                new BigDecimal(score);

        CustomerRiskProfileRequest request =
                new CustomerRiskProfileRequest();

        request.setCorrelationId(
                correlationId
        );

        request.setBehaviorScore(value);
        request.setFraudScore(value);
        request.setAmlScore(value);
        request.setKycScore(value);
        request.setDeviceScore(value);
        request.setSanctionsScore(value);
        request.setPepScore(value);
        request.setWatchlistScore(value);

        request.setCreatedBy(ACTOR_ID);
        request.setUpdatedBy(ACTOR_ID);

        return request;
    }
}