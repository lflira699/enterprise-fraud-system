package com.efs.modules.transaction.controller;

import com.efs.modules.administration.dto.UserAccountReference;
import com.efs.modules.administration.service.UserAccountLookupServiceInterface;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TransactionAttachmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private SecurityContextProvider securityContextProvider;

    @MockitoBean
    private UserAccountLookupServiceInterface
            userAccountLookupService;

    private final UUID actorId =
            UUID.randomUUID();

    private UUID authorizedOrganizationId;

    @Test
    void shouldCreateAttachment() throws Exception {

        UUID organizationId = createOrganization();
        UUID uploadedBy = createUser(organizationId);
        UUID transactionId = createTransaction(organizationId);

        String requestBody =
                """
                {
                  "fileName": "evidence.pdf",
                  "fileType": "EVIDENCE",
                  "mimeType": "application/pdf",
                  "fileSize": 2048,
                  "storageUri": "efs://transactions/evidence.pdf",
                  "checksumSha256": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                  "uploadedBy": "%s"
                }
                """.formatted(uploadedBy);

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/attachments",
                                transactionId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.attachmentId").exists())
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(transactionId.toString())
                )
                .andExpect(
                        jsonPath("$.fileName")
                                .value("evidence.pdf")
                )
                .andExpect(
                        jsonPath("$.fileType")
                                .value("EVIDENCE")
                )
                .andExpect(
                        jsonPath("$.mimeType")
                                .value("application/pdf")
                )
                .andExpect(
                        jsonPath("$.fileSize")
                                .value(2048)
                )
                .andExpect(
                        jsonPath("$.storageUri")
                                .value("efs://transactions/evidence.pdf")
                )
                .andExpect(
                        jsonPath("$.uploadedBy")
                                .value(uploadedBy.toString())
                )
                .andExpect(jsonPath("$.uploadedAt").exists());
    }

    @Test
    void shouldRejectBlankFileName() throws Exception {

        UUID organizationId = createOrganization();
        UUID transactionId = createTransaction(organizationId);

        String requestBody =
                """
                {
                  "fileName": "",
                  "fileType": "EVIDENCE",
                  "storageUri": "efs://transactions/evidence.pdf"
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/attachments",
                                transactionId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectBlankFileType() throws Exception {

        UUID organizationId = createOrganization();
        UUID transactionId = createTransaction(organizationId);

        String requestBody =
                """
                {
                  "fileName": "evidence.pdf",
                  "fileType": "",
                  "storageUri": "efs://transactions/evidence.pdf"
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/attachments",
                                transactionId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectBlankStorageUri() throws Exception {

        UUID organizationId = createOrganization();
        UUID transactionId = createTransaction(organizationId);

        String requestBody =
                """
                {
                  "fileName": "evidence.pdf",
                  "fileType": "EVIDENCE",
                  "storageUri": ""
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/attachments",
                                transactionId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAttachmentById() throws Exception {

        UUID organizationId = createOrganization();
        UUID transactionId = createTransaction(organizationId);
        UUID attachmentId = UUID.randomUUID();

        insertAttachment(
                attachmentId,
                transactionId,
                "evidence-by-id.pdf",
                "EVIDENCE",
                null,
                LocalDateTime.of(2026, 8, 26, 8, 0)
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/attachments/{attachmentId}",
                                attachmentId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.attachmentId")
                                .value(attachmentId.toString())
                )
                .andExpect(
                        jsonPath("$.transactionId")
                                .value(transactionId.toString())
                )
                .andExpect(
                        jsonPath("$.fileName")
                                .value("evidence-by-id.pdf")
                )
                .andExpect(
                        jsonPath("$.fileType")
                                .value("EVIDENCE")
                );
    }

    @Test
    void shouldGetAttachmentsByTransactionOrderedByUploadedAtDescending()
            throws Exception {

        UUID organizationId = createOrganization();
        UUID transactionId = createTransaction(organizationId);

        insertAttachment(
                UUID.randomUUID(),
                transactionId,
                "first.pdf",
                "EVIDENCE",
                null,
                LocalDateTime.of(2026, 8, 26, 8, 0)
        );

        insertAttachment(
                UUID.randomUUID(),
                transactionId,
                "second.pdf",
                "DOCUMENT",
                null,
                LocalDateTime.of(2026, 8, 26, 9, 0)
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}/attachments",
                                transactionId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(
                        jsonPath("$[0].fileName")
                                .value("second.pdf")
                )
                .andExpect(
                        jsonPath("$[1].fileName")
                                .value("first.pdf")
                );
    }

    @Test
    void shouldGetAttachmentsByFileTypeOrderedByUploadedAtDescending()
            throws Exception {

        UUID organizationId = createOrganization();
        UUID transactionOne = createTransaction(organizationId);
        UUID transactionTwo = createTransaction(organizationId);

        insertAttachment(
                UUID.randomUUID(),
                transactionOne,
                "evidence-one.pdf",
                "EVIDENCE",
                null,
                LocalDateTime.of(2026, 8, 26, 8, 0)
        );

        insertAttachment(
                UUID.randomUUID(),
                transactionTwo,
                "evidence-two.pdf",
                "EVIDENCE",
                null,
                LocalDateTime.of(2026, 8, 26, 10, 0)
        );

        insertAttachment(
                UUID.randomUUID(),
                transactionOne,
                "document.pdf",
                "DOCUMENT",
                null,
                LocalDateTime.of(2026, 8, 26, 11, 0)
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/attachments/type/{fileType}",
                                "EVIDENCE"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(
                        jsonPath("$[0].fileName")
                                .value("evidence-two.pdf")
                )
                .andExpect(
                        jsonPath("$[1].fileName")
                                .value("evidence-one.pdf")
                );
    }

    @Test
    void shouldGetAttachmentsByUploadedByOrderedByUploadedAtDescending()
            throws Exception {

        UUID organizationId = createOrganization();
        UUID uploadedBy = createUser(organizationId);
        UUID otherUser = createUser(organizationId);
        UUID transactionId = createTransaction(organizationId);

        insertAttachment(
                UUID.randomUUID(),
                transactionId,
                "user-first.pdf",
                "EVIDENCE",
                uploadedBy,
                LocalDateTime.of(2026, 8, 26, 8, 0)
        );

        insertAttachment(
                UUID.randomUUID(),
                transactionId,
                "user-second.pdf",
                "EVIDENCE",
                uploadedBy,
                LocalDateTime.of(2026, 8, 26, 10, 0)
        );

        insertAttachment(
                UUID.randomUUID(),
                transactionId,
                "other-user.pdf",
                "EVIDENCE",
                otherUser,
                LocalDateTime.of(2026, 8, 26, 11, 0)
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/attachments/uploaded-by/{uploadedBy}",
                                uploadedBy
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(
                        jsonPath("$[0].fileName")
                                .value("user-second.pdf")
                )
                .andExpect(
                        jsonPath("$[1].fileName")
                                .value("user-first.pdf")
                );
    }

    @Test
    void shouldReturnNotFoundForUnknownAttachmentId()
            throws Exception {

        createOrganization();

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/attachments/{attachmentId}",
                                UUID.randomUUID()
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundForUnknownTransaction()
            throws Exception {

        createOrganization();

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}/attachments",
                                UUID.randomUUID()
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenCreatingAttachmentForUnknownTransaction()
            throws Exception {

        createOrganization();

        String requestBody =
                """
                {
                  "fileName": "unknown.pdf",
                  "fileType": "EVIDENCE",
                  "storageUri": "efs://transactions/unknown.pdf"
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/attachments",
                                UUID.randomUUID()
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectNegativeFileSize() throws Exception {

        UUID organizationId = createOrganization();
        UUID transactionId = createTransaction(organizationId);

        String requestBody =
                """
                {
                  "fileName": "negative.pdf",
                  "fileType": "EVIDENCE",
                  "fileSize": -1,
                  "storageUri": "efs://transactions/negative.pdf"
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/attachments",
                                transactionId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectInvalidSha256Checksum() throws Exception {

        UUID organizationId = createOrganization();
        UUID transactionId = createTransaction(organizationId);

        String requestBody =
                """
                {
                  "fileName": "checksum.pdf",
                  "fileType": "EVIDENCE",
                  "storageUri": "efs://transactions/checksum.pdf",
                  "checksumSha256": "not-a-valid-sha256"
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/attachments",
                                transactionId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundForAttachmentWhenParentTransactionIsSoftDeleted()
            throws Exception {

        UUID organizationId = createOrganization();
        UUID transactionId = createTransaction(organizationId);
        UUID attachmentId = UUID.randomUUID();

        insertAttachment(
                attachmentId,
                transactionId,
                "hidden-by-id.pdf",
                "EVIDENCE",
                null,
                LocalDateTime.now()
        );

        softDeleteTransaction(transactionId);

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/attachments/{attachmentId}",
                                attachmentId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldExcludeSoftDeletedParentWhenQueryingAttachmentsByFileType()
            throws Exception {

        UUID organizationId = createOrganization();
        UUID activeTransactionId = createTransaction(organizationId);
        UUID deletedTransactionId = createTransaction(organizationId);

        insertAttachment(
                UUID.randomUUID(),
                activeTransactionId,
                "active-type.pdf",
                "EVIDENCE",
                null,
                LocalDateTime.now().minusMinutes(1)
        );

        insertAttachment(
                UUID.randomUUID(),
                deletedTransactionId,
                "deleted-type.pdf",
                "EVIDENCE",
                null,
                LocalDateTime.now()
        );

        softDeleteTransaction(deletedTransactionId);

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/attachments/type/{fileType}",
                                "EVIDENCE"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(
                        jsonPath("$[0].transactionId")
                                .value(
                                        activeTransactionId.toString()
                                )
                );
    }

    @Test
    void shouldExcludeSoftDeletedParentWhenQueryingAttachmentsByUploadedBy()
            throws Exception {

        UUID organizationId = createOrganization();
        UUID uploadedBy = createUser(organizationId);
        UUID activeTransactionId = createTransaction(organizationId);
        UUID deletedTransactionId = createTransaction(organizationId);

        insertAttachment(
                UUID.randomUUID(),
                activeTransactionId,
                "active-uploader.pdf",
                "EVIDENCE",
                uploadedBy,
                LocalDateTime.now().minusMinutes(1)
        );

        insertAttachment(
                UUID.randomUUID(),
                deletedTransactionId,
                "deleted-uploader.pdf",
                "EVIDENCE",
                uploadedBy,
                LocalDateTime.now()
        );

        softDeleteTransaction(deletedTransactionId);

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/attachments/uploaded-by/{uploadedBy}",
                                uploadedBy
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(
                        jsonPath("$[0].transactionId")
                                .value(
                                        activeTransactionId.toString()
                                )
                );
    }

    @Test
    void shouldReturnNotFoundForAttachmentListWhenParentTransactionIsSoftDeleted()
            throws Exception {

        UUID organizationId = createOrganization();
        UUID transactionId = createTransaction(organizationId);

        softDeleteTransaction(transactionId);

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}/attachments",
                                transactionId
                        )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenCreatingAttachmentForSoftDeletedTransaction()
            throws Exception {

        UUID organizationId = createOrganization();
        UUID transactionId = createTransaction(organizationId);

        softDeleteTransaction(transactionId);

        String requestBody =
                """
                {
                  "fileName": "soft-deleted.pdf",
                  "fileType": "EVIDENCE",
                  "storageUri": "efs://transactions/soft-deleted.pdf"
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/attachments",
                                transactionId
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnForbiddenWhenCreatingWithoutTransactionUpdate()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID transactionId =
                createTransaction(
                        organizationId
                );

        authorize(
                Set.of(
                        "transaction.view"
                ),
                organizationId,
                null
        );

        String requestBody =
                """
                {
                  "fileName": "forbidden-create.pdf",
                  "fileType": "EVIDENCE",
                  "storageUri": "efs://transactions/forbidden-create.pdf"
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/attachments",
                                transactionId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        requestBody
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void shouldReturnForbiddenWhenReadingWithoutTransactionView()
            throws Exception {

        UUID organizationId =
                createOrganization();

        UUID transactionId =
                createTransaction(
                        organizationId
                );

        UUID attachmentId =
                UUID.randomUUID();

        insertAttachment(
                attachmentId,
                transactionId,
                "forbidden-read.pdf",
                "EVIDENCE",
                null,
                LocalDateTime.now()
        );

        authorize(
                Set.of(
                        "transaction.update"
                ),
                organizationId,
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/attachments/{attachmentId}",
                                attachmentId
                        )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void shouldHideCrossOrganizationAttachmentById()
            throws Exception {

        UUID authorizedOrganization =
                createOrganization();

        UUID otherOrganization =
                createOrganization();

        UUID otherTransaction =
                createTransaction(
                        otherOrganization
                );

        UUID attachmentId =
                UUID.randomUUID();

        insertAttachment(
                attachmentId,
                otherTransaction,
                "cross-org-id.pdf",
                "EVIDENCE",
                null,
                LocalDateTime.now()
        );

        authorize(
                allPermissions(),
                authorizedOrganization,
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/attachments/{attachmentId}",
                                attachmentId
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldHideCrossOrganizationAttachmentList()
            throws Exception {

        UUID authorizedOrganization =
                createOrganization();

        UUID otherOrganization =
                createOrganization();

        UUID otherTransaction =
                createTransaction(
                        otherOrganization
                );

        insertAttachment(
                UUID.randomUUID(),
                otherTransaction,
                "cross-org-list.pdf",
                "EVIDENCE",
                null,
                LocalDateTime.now()
        );

        authorize(
                allPermissions(),
                authorizedOrganization,
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/{transactionId}/attachments",
                                otherTransaction
                        )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldHideCrossOrganizationAttachmentCreate()
            throws Exception {

        UUID authorizedOrganization =
                createOrganization();

        UUID otherOrganization =
                createOrganization();

        UUID otherTransaction =
                createTransaction(
                        otherOrganization
                );

        authorize(
                allPermissions(),
                authorizedOrganization,
                null
        );

        String requestBody =
                """
                {
                  "fileName": "cross-org-create.pdf",
                  "fileType": "EVIDENCE",
                  "storageUri": "efs://transactions/cross-org-create.pdf"
                }
                """;

        mockMvc.perform(
                        post(
                                "/api/v1/transactions/{transactionId}/attachments",
                                otherTransaction
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        requestBody
                                )
                )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    void shouldFilterCrossOrganizationAttachmentsByFileType()
            throws Exception {

        UUID authorizedOrganization =
                createOrganization();

        UUID visibleTransaction =
                createTransaction(
                        authorizedOrganization
                );

        UUID otherOrganization =
                createOrganization();

        UUID hiddenTransaction =
                createTransaction(
                        otherOrganization
                );

        insertAttachment(
                UUID.randomUUID(),
                visibleTransaction,
                "visible-scope.pdf",
                "EVIDENCE",
                null,
                LocalDateTime.now().minusMinutes(1)
        );

        insertAttachment(
                UUID.randomUUID(),
                hiddenTransaction,
                "hidden-scope.pdf",
                "EVIDENCE",
                null,
                LocalDateTime.now()
        );

        authorize(
                allPermissions(),
                authorizedOrganization,
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/attachments/type/{fileType}",
                                "EVIDENCE"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].transactionId")
                                .value(
                                        visibleTransaction
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$[0].fileName")
                                .value(
                                        "visible-scope.pdf"
                                )
                );
    }

    @Test
    void shouldFilterCrossOrganizationAttachmentsByUploadedBy()
            throws Exception {

        UUID authorizedOrganization =
                createOrganization();

        UUID uploadedBy =
                createUser(
                        authorizedOrganization
                );

        UUID visibleTransaction =
                createTransaction(
                        authorizedOrganization
                );

        UUID otherOrganization =
                createOrganization();

        UUID hiddenTransaction =
                createTransaction(
                        otherOrganization
                );

        insertAttachment(
                UUID.randomUUID(),
                visibleTransaction,
                "visible-uploader-scope.pdf",
                "EVIDENCE",
                uploadedBy,
                LocalDateTime.now().minusMinutes(1)
        );

        insertAttachment(
                UUID.randomUUID(),
                hiddenTransaction,
                "hidden-uploader-scope.pdf",
                "EVIDENCE",
                uploadedBy,
                LocalDateTime.now()
        );

        authorize(
                allPermissions(),
                authorizedOrganization,
                null
        );

        mockMvc.perform(
                        get(
                                "/api/v1/transactions/attachments/uploaded-by/{uploadedBy}",
                                uploadedBy
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].transactionId")
                                .value(
                                        visibleTransaction
                                                .toString()
                                )
                )
                .andExpect(
                        jsonPath("$[0].fileName")
                                .value(
                                        "visible-uploader-scope.pdf"
                                )
                );
    }

    private void softDeleteTransaction(
            UUID transactionId) {

        int updated =
                jdbcTemplate.update(
                        """
                        UPDATE transaction.transaction
                        SET deleted_at = CURRENT_TIMESTAMP
                        WHERE transaction_id = ?
                        """,
                        transactionId
                );

        if (updated != 1) {
            throw new IllegalStateException(
                    "Expected one soft-deleted transaction, got "
                            + updated
            );
        }
    }

    private UUID createOrganization() {

        UUID organizationId = UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO administration.organization (
                    organization_id,
                    organization_code,
                    legal_name,
                    country_code,
                    timezone,
                    status
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                organizationId,
                "ORG-" + organizationId,
                "Transaction Attachment Controller Test Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );

        if (authorizedOrganizationId == null) {

            authorize(
                    allPermissions(),
                    organizationId,
                    null
            );
        }

        return organizationId;
    }

    private UUID createUser(UUID organizationId) {

        UUID userId = UUID.randomUUID();
        String uniqueValue = userId.toString();

        jdbcTemplate.update(
                """
                INSERT INTO administration.user_account (
                    user_id,
                    organization_id,
                    username,
                    full_name,
                    email,
                    authentication_provider,
                    mfa_enabled,
                    account_status,
                    failed_login_attempts
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                userId,
                organizationId,
                "user-" + uniqueValue,
                "Transaction Attachment Controller Test User",
                "user-" + uniqueValue + "@efs.test",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );

        return userId;
    }

    private UUID createTransaction(UUID organizationId) {

        UUID customerId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();
        UUID createdBy = UUID.randomUUID();

        jdbcTemplate.update(
                """
                INSERT INTO customer.customer (
                    customer_id,
                    customer_number,
                    customer_type,
                    risk_level,
                    risk_score,
                    customer_status,
                    record_status,
                    record_version
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                customerId,
                "TAC-" + customerId.toString().substring(0, 8),
                "INDIVIDUAL",
                "LOW",
                BigDecimal.ZERO,
                "ACTIVE",
                "ACTIVE",
                1
        );

        jdbcTemplate.update(
                """
                INSERT INTO transaction.transaction (
                    transaction_id,
                    transaction_reference,
                    customer_id,
                    organization_id,
                    transaction_type,
                    amount,
                    currency_code,
                    transaction_status,
                    final_decision,
                    fraud_score,
                    created_by,
                    record_version
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                transactionId,
                "TRANSACTION-ATTACHMENT-CONTROLLER-" + transactionId,
                customerId,
                organizationId,
                "TEST",
                new BigDecimal("100.00"),
                "GTQ",
                "RECEIVED",
                "PENDING",
                BigDecimal.ZERO,
                createdBy,
                1
        );

        return transactionId;
    }

    private void authorize(
            Set<String> permissions,
            UUID organizationId,
            UUID tenantId) {

        authorizedOrganizationId =
                organizationId;

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
                        organizationId,
                        tenantId,
                        "transaction-attachment-controller@example.com"
                )
        );
    }

    private Set<String> allPermissions() {

        return Set.of(
                "transaction.view",
                "transaction.update"
        );
    }

    private void insertAttachment(
            UUID attachmentId,
            UUID transactionId,
            String fileName,
            String fileType,
            UUID uploadedBy,
            LocalDateTime uploadedAt) {

        jdbcTemplate.update(
                """
                INSERT INTO transaction.transaction_attachment (
                    attachment_id,
                    transaction_id,
                    file_name,
                    file_type,
                    mime_type,
                    file_size,
                    storage_uri,
                    checksum_sha256,
                    uploaded_by,
                    uploaded_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                attachmentId,
                transactionId,
                fileName,
                fileType,
                "application/pdf",
                1024L,
                "efs://transactions/" + fileName,
                null,
                uploadedBy,
                uploadedAt
        );
    }
}