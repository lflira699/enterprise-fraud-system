package com.efs.modules.customer.controller;

import com.efs.modules.customer.dto.CustomerBiometricRequest;
import com.efs.modules.customer.dto.CustomerBiometricResponse;
import com.efs.modules.customer.service.CustomerBiometricServiceInterface;
import com.efs.shared.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerBiometricController {

    private final CustomerBiometricServiceInterface customerBiometricService;

    public CustomerBiometricController(
            CustomerBiometricServiceInterface customerBiometricService) {
        this.customerBiometricService = customerBiometricService;
    }

    @PostMapping("/{customerId}/biometrics")
    public ResponseEntity<CustomerBiometricResponse> createBiometric(
            @PathVariable UUID customerId,
            @Valid @RequestBody CustomerBiometricRequest request) {

        CustomerBiometricResponse response =
                customerBiometricService.createBiometric(
                        customerId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{customerId}/biometrics")
    public ResponseEntity<List<CustomerBiometricResponse>>
    getBiometricsByCustomer(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                customerBiometricService
                        .getBiometricsByCustomerId(customerId)
        );
    }

    @GetMapping("/{customerId}/biometrics/{biometricId}")
    public ResponseEntity<CustomerBiometricResponse> getBiometricById(
            @PathVariable UUID customerId,
            @PathVariable UUID biometricId) {

        CustomerBiometricResponse response =
                customerBiometricService
                        .getBiometricById(biometricId);

        if (!response.getCustomerId().equals(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer biometric not found: " + biometricId
            );
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{customerId}/biometrics/{biometricId}")
    public ResponseEntity<CustomerBiometricResponse> updateBiometric(
            @PathVariable UUID customerId,
            @PathVariable UUID biometricId,
            @Valid @RequestBody CustomerBiometricRequest request) {

        CustomerBiometricResponse existing =
                customerBiometricService
                        .getBiometricById(biometricId);

        if (!existing.getCustomerId().equals(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer biometric not found: " + biometricId
            );
        }

        return ResponseEntity.ok(
                customerBiometricService.updateBiometric(
                        biometricId,
                        request
                )
        );
    }

    @DeleteMapping("/{customerId}/biometrics/{biometricId}")
    public ResponseEntity<Void> deleteBiometric(
            @PathVariable UUID customerId,
            @PathVariable UUID biometricId) {

        CustomerBiometricResponse existing =
                customerBiometricService
                        .getBiometricById(biometricId);

        if (!existing.getCustomerId().equals(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer biometric not found: " + biometricId
            );
        }

        customerBiometricService.deleteBiometric(biometricId);

        return ResponseEntity.noContent().build();
    }
}