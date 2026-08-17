package com.efs.modules.customer.controller;

import com.efs.modules.customer.dto.CustomerDocumentRequest;
import com.efs.modules.customer.dto.CustomerDocumentResponse;
import com.efs.modules.customer.service.CustomerDocumentServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerDocumentController {

    private final CustomerDocumentServiceInterface customerDocumentService;

    public CustomerDocumentController(
            CustomerDocumentServiceInterface customerDocumentService) {
        this.customerDocumentService = customerDocumentService;
    }

    @PostMapping("/{customerId}/documents")
    public ResponseEntity<CustomerDocumentResponse> createDocument(
            @PathVariable UUID customerId,
            @Valid @RequestBody CustomerDocumentRequest request) {

        CustomerDocumentResponse response =
                customerDocumentService.createDocument(customerId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{customerId}/documents")
    public ResponseEntity<List<CustomerDocumentResponse>> getDocumentsByCustomer(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                customerDocumentService.getDocumentsByCustomerId(customerId)
        );
    }

    @GetMapping("/{customerId}/documents/{documentId}")
    public ResponseEntity<CustomerDocumentResponse> getDocumentById(
            @PathVariable UUID customerId,
            @PathVariable UUID documentId) {

        CustomerDocumentResponse response =
                customerDocumentService.getDocumentById(documentId);

        if (!response.getCustomerId().equals(customerId)) {
            throw new com.efs.shared.exception.ResourceNotFoundException(
                    "Customer document not found: " + documentId
            );
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{customerId}/documents/{documentId}")
    public ResponseEntity<CustomerDocumentResponse> updateDocument(
            @PathVariable UUID customerId,
            @PathVariable UUID documentId,
            @Valid @RequestBody CustomerDocumentRequest request) {

        CustomerDocumentResponse existing =
                customerDocumentService.getDocumentById(documentId);

        if (!existing.getCustomerId().equals(customerId)) {
            throw new com.efs.shared.exception.ResourceNotFoundException(
                    "Customer document not found: " + documentId
            );
        }

        return ResponseEntity.ok(
                customerDocumentService.updateDocument(documentId, request)
        );
    }
}