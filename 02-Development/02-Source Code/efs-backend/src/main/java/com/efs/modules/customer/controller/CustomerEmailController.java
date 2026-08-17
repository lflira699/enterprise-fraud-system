package com.efs.modules.customer.controller;

import com.efs.modules.customer.dto.CustomerEmailRequest;
import com.efs.modules.customer.dto.CustomerEmailResponse;
import com.efs.modules.customer.service.CustomerEmailServiceInterface;
import com.efs.shared.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerEmailController {

    private final CustomerEmailServiceInterface customerEmailService;

    public CustomerEmailController(
            CustomerEmailServiceInterface customerEmailService) {
        this.customerEmailService = customerEmailService;
    }

    @PostMapping("/{customerId}/emails")
    public ResponseEntity<CustomerEmailResponse> createEmail(
            @PathVariable UUID customerId,
            @Valid @RequestBody CustomerEmailRequest request) {

        CustomerEmailResponse response =
                customerEmailService.createEmail(customerId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{customerId}/emails")
    public ResponseEntity<List<CustomerEmailResponse>> getEmailsByCustomer(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                customerEmailService.getEmailsByCustomerId(customerId)
        );
    }

    @GetMapping("/{customerId}/emails/{emailId}")
    public ResponseEntity<CustomerEmailResponse> getEmailById(
            @PathVariable UUID customerId,
            @PathVariable UUID emailId) {

        CustomerEmailResponse response =
                customerEmailService.getEmailById(emailId);

        if (!response.getCustomerId().equals(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer email not found: " + emailId
            );
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{customerId}/emails/{emailId}")
    public ResponseEntity<CustomerEmailResponse> updateEmail(
            @PathVariable UUID customerId,
            @PathVariable UUID emailId,
            @Valid @RequestBody CustomerEmailRequest request) {

        CustomerEmailResponse existing =
                customerEmailService.getEmailById(emailId);

        if (!existing.getCustomerId().equals(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer email not found: " + emailId
            );
        }

        return ResponseEntity.ok(
                customerEmailService.updateEmail(emailId, request)
        );
    }

    @DeleteMapping("/{customerId}/emails/{emailId}")
    public ResponseEntity<Void> deleteEmail(
            @PathVariable UUID customerId,
            @PathVariable UUID emailId,
            @RequestParam(required = false) UUID deletedBy) {

        CustomerEmailResponse existing =
                customerEmailService.getEmailById(emailId);

        if (!existing.getCustomerId().equals(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer email not found: " + emailId
            );
        }

        customerEmailService.deleteEmail(emailId, deletedBy);

        return ResponseEntity.noContent().build();
    }
}