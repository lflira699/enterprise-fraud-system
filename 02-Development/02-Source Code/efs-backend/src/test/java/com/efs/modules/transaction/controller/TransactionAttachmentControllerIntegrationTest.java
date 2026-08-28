package com.efs.modules.transaction.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

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