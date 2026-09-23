package com.efs.modules.detection.controller;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.detection.entity.DetectionScenario;
import com.efs.modules.detection.entity.ScenarioVersion;
import com.efs.modules.detection.repository.DetectionScenarioRepository;
import com.efs.modules.detection.repository.ScenarioVersionRepository;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ScenarioActivationControllerIntegrationTest {

    private static final UUID SECURITY_USER_ID =
            UUID.fromString(
                    "ca182ca1-82ca-182c-a182-ca182ca182ca"
            );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DetectionScenarioRepository detectionScenarioRepository;

    @Autowired
    private ScenarioVersionRepository scenarioVersionRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EntityManager entityManager;

    @MockitoBean
    private SecurityContextProvider securityContextProvider;

    @MockitoBean
    private UserAccountLookupServiceInterface
            userAccountLookupService;

    private UUID organizationId;
    private UUID tenantId;
    private UUID scenarioId;
    private UUID scenarioVersionId;
    private UUID transactionId;
    private UUID customerId;

    @BeforeEach
    void setUp() {

        LocalDateTime now =
                LocalDateTime.now();

        organizationId =
                UUID.randomUUID();

        tenantId =
                UUID.randomUUID();

        String scopeSuffix =
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8);

        entityManager
                .createNativeQuery(
                        """
                        INSERT INTO administration.organization (
                            organization_id,
                            organization_code,
                            legal_name,
                            country_code,
                            timezone,
                            status
                        )
                        VALUES (
                            :organizationId,
                            :organizationCode,
                            :legalName,
                            'GT',
                            'America/Guatemala',
                            'ACTIVE'
                        )
                        """
                )
                .setParameter(
                        "organizationId",
                        organizationId
                )
                .setParameter(
                        "organizationCode",
                        "SA-CTRL-ORG-" + scopeSuffix
                )
                .setParameter(
                        "legalName",
                        "Scenario Activation Controller Organization "
                                + scopeSuffix
                )
                .executeUpdate();

        entityManager
                .createNativeQuery(
                        """
                        INSERT INTO administration.tenant (
                            tenant_id,
                            organization_id,
                            tenant_code,
                            tenant_name,
                            status,
                            environment
                        )
                        VALUES (
                            :tenantId,
                            :organizationId,
                            :tenantCode,
                            :tenantName,
                            'ACTIVE',
                            'TEST'
                        )
                        """
                )
                .setParameter(
                        "tenantId",
                        tenantId
                )
                .setParameter(
                        "organizationId",
                        organizationId
                )
                .setParameter(
                        "tenantCode",
                        "SA-CTRL-TEN-" + scopeSuffix
                )
                .setParameter(
                        "tenantName",
                        "Scenario Activation Controller Tenant "
                                + scopeSuffix
                )
                .executeUpdate();
        Customer customer =
                new Customer();

        customer.setCustomerNumber(
                "SA-CTRL-" + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Scenario"
        );

        customer.setLastName(
                "Activation Controller"
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

        customer.setTenantId(
                tenantId
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
                "EFS-SA-CTRL-" + UUID.randomUUID()
        );

        transaction.setCustomerId(
                customerId
        );

        transaction.setOrganizationId(
                organizationId
        );

        transaction.setTenantId(
                tenantId
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

        DetectionScenario scenario =
                new DetectionScenario();

        scenario.setScenarioCode(
                "SA-CTRL-" + UUID.randomUUID()
        );

        scenario.setScenarioName(
                "Scenario Activation Controller Test"
        );

        scenario.setObjective(
                "Validate scenario activation controller behavior"
        );

        scenario.setDescription(
                "Scenario used by ScenarioActivationControllerIntegrationTest"
        );

        scenario.setCategory(
                "DETECTION"
        );

        scenario.setCriticality(
                "HIGH"
        );

        scenario.setStatus(
                "ACTIVE"
        );

        scenario.setOwner(
                "Detection Team"
        );

        scenario.setVersion(
                1
        );

        scenario.setCreatedAt(
                now
        );

        scenario.setUpdatedAt(
                now
        );

        DetectionScenario savedScenario =
                detectionScenarioRepository.saveAndFlush(
                        scenario
                );

        scenarioId =
                savedScenario.getScenarioId();

        ScenarioVersion scenarioVersion =
                new ScenarioVersion();

        scenarioVersion.setScenarioId(
                scenarioId
        );

        scenarioVersion.setVersionNumber(
                1
        );

        scenarioVersion.setVersionStatus(
                "ACTIVE"
        );

        scenarioVersion.setCorrelationWindowSeconds(
                1800L
        );

        scenarioVersion.setActivationMode(
                "AUTOMATIC"
        );

        scenarioVersion.setEffectiveFrom(
                now
        );

        scenarioVersion.setCreatedAt(
                now
        );

        scenarioVersion.setUpdatedAt(
                now
        );

        ScenarioVersion savedScenarioVersion =
                scenarioVersionRepository.saveAndFlush(
                        scenarioVersion
                );

        scenarioVersionId =
                savedScenarioVersion.getScenarioVersionId();

        authorizeAll();
    }

    @Test
    void shouldCreateScenarioActivationWithFullPayload()
            throws Exception {

        Map<String, Object> request =
                fullRequest(
                        "TRIGGERED",
                        "HIGH"
                );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/scenario-activations"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.activationId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.scenarioId")
                                .value(scenarioId.toString())
                )
                .andExpect(
                        jsonPath("$.scenarioVersionId")
                                .value(scenarioVersionId.toString())
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(transactionId.toString())
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.activationStatus")
                                .value("TRIGGERED")
                )
                .andExpect(
                        jsonPath("$.severity")
                                .value("HIGH")
                )
                .andExpect(
                        jsonPath("$.confidence")
                                .value(0.95)
                )
                .andExpect(
                        jsonPath("$.riskScore")
                                .value(87.5)
                )
                .andExpect(
                        jsonPath("$.activationReason")
                                .value(
                                        "Scenario activation controller integration test"
                                )
                )
                .andExpect(
                        jsonPath("$.decisionContext.source")
                                .value("controller-test")
                )
                .andExpect(
                        jsonPath("$.decisionContext.validated")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.triggeredAt")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.createdAt")
                                .exists()
                );
    }

    @Test
    void shouldCreateScenarioActivationWithoutTransactionReference()
            throws Exception {

        Map<String, Object> request =
                requiredRequest(
                        "TRIGGERED",
                        "MEDIUM"
                );

        request.put(
                "customerId",
                customerId
        );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/scenario-activations"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.activationId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.scenarioId")
                                .value(scenarioId.toString())
                )
                .andExpect(
                        jsonPath("$.scenarioVersionId")
                                .value(scenarioVersionId.toString())
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.activationStatus")
                                .value("TRIGGERED")
                )
                .andExpect(
                        jsonPath("$.severity")
                                .value("MEDIUM")
                )
                .andExpect(
                        jsonPath("$.triggeredAt")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.createdAt")
                                .exists()
                );
    }

    @Test
    void shouldRejectCreateWhenRequiredFieldsAreMissing()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/api/v1/detection/scenario-activations"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("{}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.errorCode")
                                .value("VALIDATION_ERROR")
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.scenarioId"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.scenarioVersionId"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.activationStatus"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.severity"
                        )
                                .exists()
                );
    }

    @Test
    void shouldRejectCreateWhenStatusAndSeverityExceedMaximumLength()
            throws Exception {

        Map<String, Object> request =
                requiredRequest(
                        "1234567890123456789012345678901",
                        "123456789012345678901"
                );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/scenario-activations"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.errorCode")
                                .value("VALIDATION_ERROR")
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.activationStatus"
                        )
                                .exists()
                )
                .andExpect(
                        jsonPath(
                                "$.validationErrors.severity"
                        )
                                .exists()
                );
    }

    @Test
    void shouldGetScenarioActivationById()
            throws Exception {

        JsonNode created =
                createActivation(
                        "TRIGGERED",
                        "HIGH"
                );

        UUID activationId =
                UUID.fromString(
                        created.get(
                                "activationId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-activations/{activationId}",
                                activationId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.activationId")
                                .value(activationId.toString())
                )
                .andExpect(
                        jsonPath("$.scenarioId")
                                .value(scenarioId.toString())
                )
                .andExpect(
                        jsonPath("$.scenarioVersionId")
                                .value(scenarioVersionId.toString())
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(transactionId.toString())
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(customerId.toString())
                )
                .andExpect(
                        jsonPath("$.activationStatus")
                                .value("TRIGGERED")
                )
                .andExpect(
                        jsonPath("$.severity")
                                .value("HIGH")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownScenarioActivation()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-activations/{activationId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.status")
                                .value(404)
                )
                .andExpect(
                        jsonPath("$.errorCode")
                                .value(
                                        "CUSTOMER_RESOURCE_NOT_FOUND"
                                )
                );
    }

    @Test
    void shouldGetActivationsByScenario()
            throws Exception {

        JsonNode first =
                createActivation(
                        "TRIGGERED",
                        "HIGH"
                );

        JsonNode second =
                createActivation(
                        "PENDING",
                        "MEDIUM"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-activations/scenario/{scenarioId}",
                                scenarioId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].activationId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "activationId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].activationId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "activationId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetActivationsByScenarioVersion()
            throws Exception {

        JsonNode first =
                createActivation(
                        "TRIGGERED",
                        "HIGH"
                );

        JsonNode second =
                createActivation(
                        "PENDING",
                        "LOW"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-activations/scenario-version/{scenarioVersionId}",
                                scenarioVersionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].activationId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "activationId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].activationId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "activationId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetActivationsByTransaction()
            throws Exception {

        JsonNode first =
                createActivation(
                        "TRIGGERED",
                        "HIGH"
                );

        JsonNode second =
                createActivation(
                        "PENDING",
                        "MEDIUM"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-activations/transaction/{transactionId}",
                                transactionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].activationId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "activationId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].activationId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "activationId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetActivationsByCustomer()
            throws Exception {

        JsonNode first =
                createActivation(
                        "TRIGGERED",
                        "HIGH"
                );

        JsonNode second =
                createActivation(
                        "PENDING",
                        "LOW"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-activations/customer/{customerId}",
                                customerId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].activationId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "activationId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].activationId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "activationId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetActivationsByStatus()
            throws Exception {

        String activationStatus =
                "SA_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(
                                        0,
                                        8
                                );

        JsonNode first =
                createActivation(
                        activationStatus,
                        "HIGH"
                );

        JsonNode second =
                createActivation(
                        activationStatus,
                        "MEDIUM"
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-activations/status/{activationStatus}",
                                activationStatus
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].activationId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "activationId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].activationId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "activationId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].activationStatus")
                                .value(
                                        hasItem(
                                                activationStatus
                                        )
                                )
                );
    }

    @Test
    void shouldGetActivationsBySeverity()
            throws Exception {

        String severity =
                "SV_" +
                        UUID.randomUUID()
                                .toString()
                                .substring(
                                        0,
                                        8
                                );

        JsonNode first =
                createActivation(
                        "TRIGGERED",
                        severity
                );

        JsonNode second =
                createActivation(
                        "PENDING",
                        severity
                );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-activations/severity/{severity}",
                                severity
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[*].activationId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "activationId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].activationId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "activationId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].severity")
                                .value(
                                        hasItem(
                                                severity
                                        )
                                )
                );
    }


    @Test
    void shouldAllowCreateWithScenarioActivationCreatePermissionOnly()
            throws Exception {

        authorize(
                Set.of(
                        "scenario.activation.create"
                ),
                tenantId,
                organizationId,
                tenantId
        );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/scenario-activations"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                fullRequest(
                                                        "TRIGGERED",
                                                        "HIGH"
                                                )
                                        )
                                )
                )
                .andExpect(
                        status().isCreated()
                );
    }

    @Test
    void shouldRejectCreateWithoutScenarioActivationCreatePermission()
            throws Exception {

        authorize(
                Set.of(
                        "scenario.activation.view"
                ),
                tenantId,
                organizationId,
                tenantId
        );

        mockMvc.perform(
                        post(
                                "/api/v1/detection/scenario-activations"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                fullRequest(
                                                        "TRIGGERED",
                                                        "HIGH"
                                                )
                                        )
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void shouldAllowReadWithScenarioActivationViewPermissionOnly()
            throws Exception {

        JsonNode created =
                createActivation(
                        "TRIGGERED",
                        "HIGH"
                );

        UUID activationId =
                UUID.fromString(
                        created.get(
                                "activationId"
                        ).asText()
                );

        authorize(
                Set.of(
                        "scenario.activation.view"
                ),
                tenantId,
                organizationId,
                tenantId
        );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-activations/{activationId}",
                                activationId
                        )
                )
                .andExpect(
                        status().isOk()
                );
    }

    @Test
    void shouldRejectEveryReadEndpointWithoutScenarioActivationViewPermission()
            throws Exception {

        authorize(
                Set.of(
                        "scenario.activation.create"
                ),
                tenantId,
                organizationId,
                tenantId
        );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-activations/{activationId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-activations/scenario/{scenarioId}",
                                scenarioId
                        )
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-activations/scenario-version/{scenarioVersionId}",
                                scenarioVersionId
                        )
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-activations/transaction/{transactionId}",
                                transactionId
                        )
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-activations/customer/{customerId}",
                                customerId
                        )
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-activations/status/{activationStatus}",
                                "TRIGGERED"
                        )
                )
                .andExpect(status().isForbidden());

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-activations/severity/{severity}",
                                "HIGH"
                        )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectAuthenticatedTenantMismatch()
            throws Exception {

        authorize(
                Set.of(
                        "scenario.activation.view"
                ),
                UUID.randomUUID(),
                organizationId,
                tenantId
        );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-activations/{activationId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void organizationLevelActorShouldReadTenantOwnedActivation()
            throws Exception {

        JsonNode created =
                createActivation(
                        "TRIGGERED",
                        "HIGH"
                );

        UUID activationId =
                UUID.fromString(
                        created.get(
                                "activationId"
                        ).asText()
                );

        authorize(
                Set.of(
                        "scenario.activation.view"
                ),
                null,
                organizationId,
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-activations/{activationId}",
                                activationId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.activationId")
                                .value(
                                        activationId.toString()
                                )
                );
    }

    @Test
    void crossOrganizationActivationShouldRemainHiddenAsNotFound()
            throws Exception {

        JsonNode created =
                createActivation(
                        "TRIGGERED",
                        "HIGH"
                );

        UUID activationId =
                UUID.fromString(
                        created.get(
                                "activationId"
                        ).asText()
                );

        authorize(
                Set.of(
                        "scenario.activation.view"
                ),
                tenantId,
                UUID.randomUUID(),
                tenantId
        );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-activations/{activationId}",
                                activationId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void crossTenantActivationShouldRemainHiddenAsNotFound()
            throws Exception {

        JsonNode created =
                createActivation(
                        "TRIGGERED",
                        "HIGH"
                );

        UUID activationId =
                UUID.fromString(
                        created.get(
                                "activationId"
                        ).asText()
                );

        UUID otherTenantId =
                UUID.randomUUID();

        authorize(
                Set.of(
                        "scenario.activation.view"
                ),
                otherTenantId,
                organizationId,
                otherTenantId
        );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-activations/{activationId}",
                                activationId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void crossTenantCollectionShouldReturnEmptyCollection()
            throws Exception {

        createActivation(
                "TRIGGERED",
                "HIGH"
        );

        UUID otherTenantId =
                UUID.randomUUID();

        authorize(
                Set.of(
                        "scenario.activation.view"
                ),
                otherTenantId,
                organizationId,
                otherTenantId
        );

        mockMvc.perform(
                        get(
                                "/api/v1/detection/scenario-activations/scenario/{scenarioId}",
                                scenarioId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$").isEmpty()
                );
    }

    private void authorizeAll() {

        authorize(
                Set.of(
                        "scenario.activation.view",
                        "scenario.activation.create"
                ),
                tenantId,
                organizationId,
                tenantId
        );
    }

    private void authorize(
            Set<String> permissions,
            UUID contextTenantId,
            UUID actorOrganizationId,
            UUID actorTenantId) {

        when(
                securityContextProvider
                        .getCurrentContext()
        ).thenReturn(
                new SecurityContext(
                        SECURITY_USER_ID,
                        contextTenantId,
                        UUID.randomUUID(),
                        Set.of(),
                        permissions,
                        Set.of()
                )
        );

        when(
                userAccountLookupService
                        .getAuthorizedUser(
                                SECURITY_USER_ID
                        )
        ).thenReturn(
                new UserAccountReference(
                        SECURITY_USER_ID,
                        actorOrganizationId,
                        actorTenantId,
                        "scenario.activation.security@example.com"
                )
        );
    }

    private JsonNode createActivation(
            String activationStatus,
            String severity)
            throws Exception {

        Map<String, Object> request =
                fullRequest(
                        activationStatus,
                        severity
                );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/detection/scenario-activations"
                                )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                objectMapper
                                                        .writeValueAsString(
                                                                request
                                                        )
                                        )
                        )
                        .andExpect(status().isCreated())
                        .andReturn();

        return objectMapper.readTree(
                result.getResponse()
                        .getContentAsString()
        );
    }

    private Map<String, Object> fullRequest(
            String activationStatus,
            String severity) {

        Map<String, Object> request =
                requiredRequest(
                        activationStatus,
                        severity
                );

        request.put(
                "transactionId",
                transactionId
        );

        request.put(
                "customerId",
                customerId
        );

        request.put(
                "confidence",
                new BigDecimal("0.9500")
        );

        request.put(
                "riskScore",
                new BigDecimal("87.5000")
        );

        request.put(
                "activationReason",
                "Scenario activation controller integration test"
        );

        request.put(
                "decisionContext",
                Map.of(
                        "source",
                        "controller-test",
                        "validated",
                        true
                )
        );

        return request;
    }

    private Map<String, Object> requiredRequest(
            String activationStatus,
            String severity) {

        Map<String, Object> request =
                new LinkedHashMap<>();

        request.put(
                "scenarioId",
                scenarioId
        );

        request.put(
                "scenarioVersionId",
                scenarioVersionId
        );

        request.put(
                "activationStatus",
                activationStatus
        );

        request.put(
                "severity",
                severity
        );

        return request;
    }
}