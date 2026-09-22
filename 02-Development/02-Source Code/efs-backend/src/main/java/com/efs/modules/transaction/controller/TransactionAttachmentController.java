package com.efs.modules.transaction.controller;

import com.efs.modules.transaction.dto.TransactionAttachmentRequest;
import com.efs.modules.transaction.dto.TransactionAttachmentResponse;
import com.efs.modules.transaction.service.TransactionAttachmentServiceInterface;
import com.efs.modules.transaction.service.TransactionChildAccessServiceInterface;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionAttachmentController {

    private final TransactionAttachmentServiceInterface
            transactionAttachmentService;

    private final TransactionChildAccessServiceInterface
            transactionChildAccessService;

    private final SecurityContextProvider
            securityContextProvider;

    public TransactionAttachmentController(
            TransactionAttachmentServiceInterface
                    transactionAttachmentService,
            TransactionChildAccessServiceInterface
                    transactionChildAccessService,
            SecurityContextProvider securityContextProvider) {

        this.transactionAttachmentService =
                transactionAttachmentService;

        this.transactionChildAccessService =
                transactionChildAccessService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @PostMapping("/{transactionId}/attachments")
    public ResponseEntity<TransactionAttachmentResponse>
    createAttachment(
            @PathVariable UUID transactionId,
            @Valid @RequestBody TransactionAttachmentRequest request) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        TransactionAttachmentResponse response =
                transactionChildAccessService
                        .create(
                                securityContext,
                                transactionId,
                                request,
                                transactionAttachmentService
                                        ::createAttachment
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/attachments/{attachmentId}")
    public ResponseEntity<TransactionAttachmentResponse>
    getAttachmentById(
            @PathVariable UUID attachmentId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .getById(
                                securityContext,
                                attachmentId,
                                transactionAttachmentService
                                        ::getAttachmentById,
                                TransactionAttachmentResponse
                                        ::getTransactionId,
                                "Transaction attachment not found: "
                                        + attachmentId
                        )
        );
    }

    @GetMapping("/{transactionId}/attachments")
    public ResponseEntity<List<TransactionAttachmentResponse>>
    getAttachmentsByTransactionId(
            @PathVariable UUID transactionId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .getByTransactionId(
                                securityContext,
                                transactionId,
                                transactionAttachmentService
                                        ::getAttachmentsByTransactionId
                        )
        );
    }

    @GetMapping("/attachments/type/{fileType}")
    public ResponseEntity<List<TransactionAttachmentResponse>>
    getAttachmentsByFileType(
            @PathVariable String fileType) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .filterVisible(
                                securityContext,
                                () ->
                                        transactionAttachmentService
                                                .getAttachmentsByFileType(
                                                        fileType
                                                ),
                                TransactionAttachmentResponse
                                        ::getTransactionId
                        )
        );
    }

    @GetMapping("/attachments/uploaded-by/{uploadedBy}")
    public ResponseEntity<List<TransactionAttachmentResponse>>
    getAttachmentsByUploadedBy(
            @PathVariable UUID uploadedBy) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .filterVisible(
                                securityContext,
                                () ->
                                        transactionAttachmentService
                                                .getAttachmentsByUploadedBy(
                                                        uploadedBy
                                                ),
                                TransactionAttachmentResponse
                                        ::getTransactionId
                        )
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Void> handleAccessDenied(
            AccessDeniedException exception) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .build();
    }
}