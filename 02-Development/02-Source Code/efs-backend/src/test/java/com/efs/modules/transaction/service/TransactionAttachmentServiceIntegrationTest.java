package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionAttachmentRequest;
import com.efs.modules.transaction.dto.TransactionAttachmentResponse;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TransactionAttachmentServiceIntegrationTest {

    @Autowired
    private TransactionAttachmentServiceInterface service;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateAndRetrieveAttachmentById() {

        UUID organizationId = createOrganization();
        UUID uploadedBy = createUser(organizationId);
        UUID transactionId = createTransaction(organizationId);

        TransactionAttachmentRequest request =
                createRequest(
                        "evidence.pdf",
                        "EVIDENCE",
                        "application/pdf",
                        2048L,
                        "efs://transactions/evidence.pdf",
                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                        uploadedBy
                );

        TransactionAttachmentResponse created =
                service.createAttachment(
                        transactionId,
                        request
                );

        assertNotNull(created);
        assertNotNull(created.getAttachmentId());
        assertEquals(transactionId, created.getTransactionId());
        assertEquals("evidence.pdf", created.getFileName());
        assertEquals("EVIDENCE", created.getFileType());
        assertEquals("application/pdf", created.getMimeType());
        assertEquals(2048L, created.getFileSize());
        assertEquals(
                "efs://transactions/evidence.pdf",
                created.getStorageUri()
        );
        assertEquals(uploadedBy, created.getUploadedBy());
        assertNotNull(created.getUploadedAt());

        TransactionAttachmentResponse retrieved =
                service.getAttachmentById(
                        created.getAttachmentId()
                );

        assertEquals(
                created.getAttachmentId(),
                retrieved.getAttachmentId()
        );
        assertEquals(transactionId, retrieved.getTransactionId());
        assertEquals("evidence.pdf", retrieved.getFileName());
    }

    @Test
    void shouldReturnAttachmentsByTransactionOrderedByUploadedAtDescending() {

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

        List<TransactionAttachmentResponse> result =
                service.getAttachmentsByTransactionId(
                        transactionId
                );

        assertEquals(2, result.size());
        assertEquals("second.pdf", result.get(0).getFileName());
        assertEquals("first.pdf", result.get(1).getFileName());
    }

    @Test
    void shouldReturnAttachmentsByFileTypeOrderedByUploadedAtDescending() {

        UUID organizationId = createOrganization();

        UUID transactionOne =
                createTransaction(organizationId);

        UUID transactionTwo =
                createTransaction(organizationId);

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

        List<TransactionAttachmentResponse> result =
                service.getAttachmentsByFileType(
                        "EVIDENCE"
                );

        assertEquals(2, result.size());
        assertEquals(
                "evidence-two.pdf",
                result.get(0).getFileName()
        );
        assertEquals(
                "evidence-one.pdf",
                result.get(1).getFileName()
        );

        assertTrue(
                result.stream()
                        .allMatch(
                                attachment ->
                                        "EVIDENCE".equals(
                                                attachment.getFileType()
                                        )
                        )
        );
    }

    @Test
    void shouldReturnAttachmentsByUploadedByOrderedByUploadedAtDescending() {

        UUID organizationId = createOrganization();

        UUID uploadedBy =
                createUser(organizationId);

        UUID otherUser =
                createUser(organizationId);

        UUID transactionId =
                createTransaction(organizationId);

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

        List<TransactionAttachmentResponse> result =
                service.getAttachmentsByUploadedBy(
                        uploadedBy
                );

        assertEquals(2, result.size());
        assertEquals(
                "user-second.pdf",
                result.get(0).getFileName()
        );
        assertEquals(
                "user-first.pdf",
                result.get(1).getFileName()
        );

        assertTrue(
                result.stream()
                        .allMatch(
                                attachment ->
                                        uploadedBy.equals(
                                                attachment.getUploadedBy()
                                        )
                        )
        );
    }

    @Test
    void shouldReturnEmptyListWhenFileTypeDoesNotExist() {

        List<TransactionAttachmentResponse> result =
                service.getAttachmentsByFileType(
                        "NON_EXISTENT_FILE_TYPE"
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenUploadedByDoesNotExist() {

        List<TransactionAttachmentResponse> result =
                service.getAttachmentsByUploadedBy(
                        UUID.randomUUID()
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowWhenAttachmentIdDoesNotExist() {

        UUID attachmentId =
                UUID.randomUUID();

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () ->
                                service.getAttachmentById(
                                        attachmentId
                                )
                );

        assertTrue(
                exception.getMessage()
                        .contains(
                                attachmentId.toString()
                        )
        );
    }

    @Test
    void shouldThrowWhenCreatingAttachmentForUnknownTransaction() {

        UUID transactionId =
                UUID.randomUUID();

        TransactionAttachmentRequest request =
                createRequest(
                        "unknown.pdf",
                        "EVIDENCE",
                        "application/pdf",
                        1024L,
                        "efs://transactions/unknown.pdf",
                        null,
                        null
                );

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () ->
                                service.createAttachment(
                                        transactionId,
                                        request
                                )
                );

        assertTrue(
                exception.getMessage()
                        .contains(
                                transactionId.toString()
                        )
        );
    }

    @Test
    void shouldThrowWhenRetrievingAttachmentsForUnknownTransaction() {

        UUID transactionId =
                UUID.randomUUID();

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () ->
                                service.getAttachmentsByTransactionId(
                                        transactionId
                                )
                );

        assertTrue(
                exception.getMessage()
                        .contains(
                                transactionId.toString()
                        )
        );
    }

    private TransactionAttachmentRequest createRequest(
            String fileName,
            String fileType,
            String mimeType,
            Long fileSize,
            String storageUri,
            String checksumSha256,
            UUID uploadedBy) {

        TransactionAttachmentRequest request =
                new TransactionAttachmentRequest();

        request.setFileName(fileName);
        request.setFileType(fileType);
        request.setMimeType(mimeType);
        request.setFileSize(fileSize);
        request.setStorageUri(storageUri);
        request.setChecksumSha256(checksumSha256);
        request.setUploadedBy(uploadedBy);

        return request;
    }

    private UUID createOrganization() {

        UUID organizationId =
                UUID.randomUUID();

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
                "Transaction Attachment Test Organization",
                "GT",
                "America/Guatemala",
                "ACTIVE"
        );

        return organizationId;
    }

    private UUID createUser(
            UUID organizationId) {

        UUID userId =
                UUID.randomUUID();

        String uniqueValue =
                userId.toString();

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
                "Transaction Attachment Test User",
                "user-" + uniqueValue + "@efs.test",
                "LOCAL",
                false,
                "ACTIVE",
                0
        );

        return userId;
    }

    private UUID createTransaction(
            UUID organizationId) {

        UUID customerId =
                UUID.randomUUID();

        UUID transactionId =
                UUID.randomUUID();

        UUID createdBy =
                UUID.randomUUID();

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
                "TA-" + customerId.toString().substring(0, 8),
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
                "TRANSACTION-ATTACHMENT-" + transactionId,
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