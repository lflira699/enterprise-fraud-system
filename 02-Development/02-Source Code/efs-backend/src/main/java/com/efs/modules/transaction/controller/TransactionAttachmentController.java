package com.efs.modules.transaction.controller;

import com.efs.modules.transaction.dto.TransactionAttachmentRequest;
import com.efs.modules.transaction.dto.TransactionAttachmentResponse;
import com.efs.modules.transaction.service.TransactionAttachmentServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionAttachmentController {

    private final TransactionAttachmentServiceInterface transactionAttachmentService;

    public TransactionAttachmentController(
            TransactionAttachmentServiceInterface transactionAttachmentService) {

        this.transactionAttachmentService = transactionAttachmentService;
    }

    @PostMapping("/{transactionId}/attachments")
    public ResponseEntity<TransactionAttachmentResponse> createAttachment(
            @PathVariable UUID transactionId,
            @Valid @RequestBody TransactionAttachmentRequest request) {

        TransactionAttachmentResponse response =
                transactionAttachmentService.createAttachment(
                        transactionId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/attachments/{attachmentId}")
    public ResponseEntity<TransactionAttachmentResponse> getAttachmentById(
            @PathVariable UUID attachmentId) {

        return ResponseEntity.ok(
                transactionAttachmentService
                        .getAttachmentById(attachmentId)
        );
    }

    @GetMapping("/{transactionId}/attachments")
    public ResponseEntity<List<TransactionAttachmentResponse>>
    getAttachmentsByTransactionId(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                transactionAttachmentService
                        .getAttachmentsByTransactionId(transactionId)
        );
    }

    @GetMapping("/attachments/type/{fileType}")
    public ResponseEntity<List<TransactionAttachmentResponse>>
    getAttachmentsByFileType(
            @PathVariable String fileType) {

        return ResponseEntity.ok(
                transactionAttachmentService
                        .getAttachmentsByFileType(fileType)
        );
    }

    @GetMapping("/attachments/uploaded-by/{uploadedBy}")
    public ResponseEntity<List<TransactionAttachmentResponse>>
    getAttachmentsByUploadedBy(
            @PathVariable UUID uploadedBy) {

        return ResponseEntity.ok(
                transactionAttachmentService
                        .getAttachmentsByUploadedBy(uploadedBy)
        );
    }
}