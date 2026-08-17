package com.efs.modules.customer.controller;

import com.efs.modules.customer.dto.CustomerPhoneRequest;
import com.efs.modules.customer.dto.CustomerPhoneResponse;
import com.efs.modules.customer.service.CustomerPhoneServiceInterface;
import com.efs.shared.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerPhoneController {

    private final CustomerPhoneServiceInterface customerPhoneService;

    public CustomerPhoneController(
            CustomerPhoneServiceInterface customerPhoneService) {
        this.customerPhoneService = customerPhoneService;
    }

    @PostMapping("/{customerId}/phones")
    public ResponseEntity<CustomerPhoneResponse> createPhone(
            @PathVariable UUID customerId,
            @Valid @RequestBody CustomerPhoneRequest request) {

        CustomerPhoneResponse response =
                customerPhoneService.createPhone(customerId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{customerId}/phones")
    public ResponseEntity<List<CustomerPhoneResponse>> getPhonesByCustomer(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                customerPhoneService.getPhonesByCustomerId(customerId)
        );
    }

    @GetMapping("/{customerId}/phones/{phoneId}")
    public ResponseEntity<CustomerPhoneResponse> getPhoneById(
            @PathVariable UUID customerId,
            @PathVariable UUID phoneId) {

        CustomerPhoneResponse response =
                customerPhoneService.getPhoneById(phoneId);

        if (!response.getCustomerId().equals(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer phone not found: " + phoneId
            );
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{customerId}/phones/{phoneId}")
    public ResponseEntity<CustomerPhoneResponse> updatePhone(
            @PathVariable UUID customerId,
            @PathVariable UUID phoneId,
            @Valid @RequestBody CustomerPhoneRequest request) {

        CustomerPhoneResponse existing =
                customerPhoneService.getPhoneById(phoneId);

        if (!existing.getCustomerId().equals(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer phone not found: " + phoneId
            );
        }

        return ResponseEntity.ok(
                customerPhoneService.updatePhone(phoneId, request)
        );
    }

    @DeleteMapping("/{customerId}/phones/{phoneId}")
    public ResponseEntity<Void> deletePhone(
            @PathVariable UUID customerId,
            @PathVariable UUID phoneId,
            @RequestParam(required = false) UUID deletedBy) {

        CustomerPhoneResponse existing =
                customerPhoneService.getPhoneById(phoneId);

        if (!existing.getCustomerId().equals(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer phone not found: " + phoneId
            );
        }

        customerPhoneService.deletePhone(phoneId, deletedBy);

        return ResponseEntity.noContent().build();
    }
}