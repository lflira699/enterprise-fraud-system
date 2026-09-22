package com.efs.modules.transaction.controller;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.modules.customer.entity.Customer;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.transaction.dto.TransactionParticipantRequest;
import com.efs.modules.transaction.entity.Transaction;
import com.efs.modules.transaction.repository.TransactionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
class TransactionParticipantControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @MockitoBean
    private SecurityContextProvider securityContextProvider;

    @MockitoBean
    private UserAccountLookupServiceInterface
            userAccountLookupService;

    private final UUID actorId =
            UUID.randomUUID();

    private UUID organizationId;

    private UUID transactionId;
    private UUID customerId;

    @BeforeEach
    void setUp() {

        LocalDateTime now =
                LocalDateTime.now();

        Customer customer =
                createCustomer(
                        "TP-CTRL-"
                );

        customerId =
                customer.getCustomerId();

        Transaction transaction =
                new Transaction();

        transaction.setTransactionReference(
                "TP-CTRL-TXN-" + UUID.randomUUID()
        );

        transaction.setCustomerId(
                customerId
        );

        organizationId =
                UUID.randomUUID();

        transaction.setOrganizationId(
                organizationId
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
                "RECEIVED"
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

        authorize(
                allPermissions(),
                organizationId,
                null
        );
    }

    @Test
    void shouldCreateTransactionParticipant()
            throws Exception {

        UUID institutionId =
                UUID.randomUUID();

        String externalIdentifier =
                "EXT-" + UUID.randomUUID();

        TransactionParticipantRequest request =
                buildRequest(
                        "SENDER"
                );

        request.setCustomerId(
                customerId
        );

        request.setExternalIdentifier(
                externalIdentifier
        );

        request.setInstitutionId(
                institutionId
        );

        request.setCountryCode(
                "GT"
        );

        request.setRiskLevel(
                "LOW"
        );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/participants",
                                transactionId
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
                        jsonPath("$.participantId")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(
                                        transactionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.participantType")
                                .value("SENDER")
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(
                                        customerId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.externalIdentifier")
                                .value(
                                        externalIdentifier
                                )
                )
                .andExpect(
                        jsonPath("$.institutionId")
                                .value(
                                        institutionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.countryCode")
                                .value("GT")
                )
                .andExpect(
                        jsonPath("$.riskLevel")
                                .value("LOW")
                )
                .andExpect(
                        jsonPath("$.createdAt")
                                .exists()
                );
    }

    @Test
    void shouldRejectBlankParticipantType()
            throws Exception {

        TransactionParticipantRequest request =
                buildRequest(
                        " "
                );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/participants",
                                transactionId
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
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectInvalidCountryCodeLength()
            throws Exception {

        TransactionParticipantRequest request =
                buildRequest(
                        "SENDER"
                );

        request.setCountryCode(
                "GTM"
        );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/participants",
                                transactionId
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
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundWhenCreatingParticipantForUnknownTransaction()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/participants",
                                UUID.randomUUID()
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                buildRequest(
                                                        "SENDER"
                                                )
                                        )
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenCreatingParticipantForUnknownCustomer()
            throws Exception {

        TransactionParticipantRequest request =
                buildRequest(
                        "SENDER"
                );

        request.setCustomerId(
                UUID.randomUUID()
        );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/participants",
                                transactionId
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
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetTransactionParticipantById()
            throws Exception {

        JsonNode created =
                createParticipant(
                        "SENDER",
                        customerId
                );

        UUID participantId =
                UUID.fromString(
                        created.get(
                                "participantId"
                        ).asText()
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/participants/{participantId}",
                                participantId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.participantId")
                                .value(
                                        participantId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(
                                        transactionId.toString()
                                )
                )
                .andExpect(
                        jsonPath("$.participantType")
                                .value("SENDER")
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .value(
                                        customerId.toString()
                                )
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownParticipant()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/participants/{participantId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetParticipantsByTransactionId()
            throws Exception {

        JsonNode first =
                createParticipant(
                        "SENDER",
                        null
                );

        JsonNode second =
                createParticipant(
                        "BENEFICIARY",
                        null
                );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}/participants",
                                transactionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].participantId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "participantId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].participantId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "participantId"
                                                ).asText()
                                        )
                                )
                );
    }

    @Test
    void shouldGetParticipantsByCustomerId()
            throws Exception {

        Customer secondCustomer =
                createCustomer(
                        "TP-CTRL-B-"
                );

        JsonNode first =
                createParticipant(
                        "SENDER",
                        customerId
                );

        JsonNode second =
                createParticipant(
                        "BENEFICIARY",
                        customerId
                );

        createParticipant(
                "BENEFICIARY",
                secondCustomer.getCustomerId()
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/participants/customer/{customerId}",
                                customerId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$[*].participantId")
                                .value(
                                        hasItem(
                                                first.get(
                                                        "participantId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].participantId")
                                .value(
                                        hasItem(
                                                second.get(
                                                        "participantId"
                                                ).asText()
                                        )
                                )
                )
                .andExpect(
                        jsonPath("$[*].customerId")
                                .value(
                                        hasItem(
                                                customerId.toString()
                                        )
                                )
                );
    }

    @Test
    void shouldRejectNonAlphabeticCountryCode()
            throws Exception {

        TransactionParticipantRequest request =
                buildRequest("SENDER");

        request.setCountryCode("G1");

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/participants",
                                transactionId
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
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNormalizeLowercaseCountryCode()
            throws Exception {

        TransactionParticipantRequest request =
                buildRequest("SENDER");

        request.setCountryCode("gt");

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/participants",
                                transactionId
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
                        jsonPath("$.countryCode")
                                .value("GT")
                );
    }

    @Test
    void shouldHideParticipantWhenParentTransactionIsSoftDeleted()
            throws Exception {

        JsonNode created =
                createParticipant(
                        "SENDER",
                        customerId
                );

        UUID participantId =
                UUID.fromString(
                        created.get("participantId")
                                .asText()
                );

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

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/participants/{participantId}",
                                participantId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnForbiddenWhenCreatingWithoutTransactionUpdate()
            throws Exception {

        authorize(
                Set.of("transaction.view"),
                organizationId,
                null
        );

        TransactionParticipantRequest request =
                buildRequest("SENDER");

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/participants",
                                transactionId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenReadingWithoutTransactionView()
            throws Exception {

        JsonNode created =
                createParticipant(
                        "SENDER",
                        customerId
                );

        UUID participantId =
                UUID.fromString(
                        created.get("participantId").asText()
                );

        authorize(
                Set.of("transaction.update"),
                organizationId,
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/participants/{participantId}",
                                participantId
                        )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldHideCrossOrganizationParticipantById()
            throws Exception {

        JsonNode created =
                createParticipant(
                        "SENDER",
                        customerId
                );

        UUID participantId =
                UUID.fromString(
                        created.get("participantId").asText()
                );

        authorize(
                allPermissions(),
                UUID.randomUUID(),
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/participants/{participantId}",
                                participantId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldHideCrossOrganizationParticipantList()
            throws Exception {

        createParticipant(
                "SENDER",
                customerId
        );

        authorize(
                allPermissions(),
                UUID.randomUUID(),
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}/participants",
                                transactionId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldHideCrossOrganizationParticipantCreate()
            throws Exception {

        authorize(
                allPermissions(),
                UUID.randomUUID(),
                null
        );

        TransactionParticipantRequest request =
                buildRequest("SENDER");

        request.setCustomerId(
                customerId
        );

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/participants",
                                transactionId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFilterCrossOrganizationParticipantsByCustomer()
            throws Exception {

        createParticipant(
                "SENDER",
                customerId
        );

        authorize(
                allPermissions(),
                UUID.randomUUID(),
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/participants/customer/{customerId}",
                                customerId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    private void authorize(
            Set<String> permissions,
            UUID authorizedOrganizationId,
            UUID tenantId) {

        when(
                securityContextProvider
                        .getCurrentContext()
        ).thenReturn(
                new SecurityContext(
                        actorId,
                        tenantId,
                        null,
                        Set.of(),
                        permissions,
                        Set.of()
                )
        );

        when(
                userAccountLookupService
                        .getAuthorizedUser(
                                actorId
                        )
        ).thenReturn(
                new UserAccountReference(
                        actorId,
                        authorizedOrganizationId,
                        tenantId,
                        "transaction-child-controller@example.com"
                )
        );
    }

    private Set<String> allPermissions() {

        return Set.of(
                "transaction.view",
                "transaction.update"
        );
    }
    private JsonNode createParticipant(
            String participantType,
            UUID participantCustomerId)
            throws Exception {

        TransactionParticipantRequest request =
                buildRequest(
                        participantType
                );

        request.setCustomerId(
                participantCustomerId
        );

        MvcResult result =
                mockMvc.perform(
                                post(
                                        "/api/v1/transactions/{transactionId}/participants",
                                        transactionId
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

    private TransactionParticipantRequest buildRequest(
            String participantType) {

        TransactionParticipantRequest request =
                new TransactionParticipantRequest();

        request.setParticipantType(
                participantType
        );

        return request;
    }

    private Customer createCustomer(
            String prefix) {

        LocalDateTime now =
                LocalDateTime.now();

        Customer customer =
                new Customer();

        customer.setCustomerNumber(
                prefix + UUID.randomUUID()
        );

        customer.setCustomerType(
                "INDIVIDUAL"
        );

        customer.setFirstName(
                "Participant"
        );

        customer.setLastName(
                "Controller"
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

        return customerRepository.saveAndFlush(
                customer
        );
    }
}
