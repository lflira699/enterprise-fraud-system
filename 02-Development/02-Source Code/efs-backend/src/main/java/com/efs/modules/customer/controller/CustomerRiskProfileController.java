package com.efs.modules.customer.controller;

import com.efs.modules.customer.dto.CustomerRiskProfileRequest;
import com.efs.modules.customer.dto.CustomerRiskProfileResponse;
import com.efs.modules.customer.service.CustomerRiskProfileServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerRiskProfileController {

    private final CustomerRiskProfileServiceInterface customerRiskProfileService;

    public CustomerRiskProfileController(
            CustomerRiskProfileServiceInterface customerRiskProfileService) {
        this.customerRiskProfileService = customerRiskProfileService;
    }

    @PostMapping("/{customerId}/risk-profile")
    public ResponseEntity<CustomerRiskProfileResponse> createRiskProfile(
            @PathVariable UUID customerId,
            @Valid @RequestBody CustomerRiskProfileRequest request) {

        CustomerRiskProfileResponse response =
                customerRiskProfileService.createRiskProfile(customerId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{customerId}/risk-profile")
    public ResponseEntity<CustomerRiskProfileResponse> getRiskProfile(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                customerRiskProfileService.getRiskProfileByCustomerId(customerId)
        );
    }

    @PutMapping("/{customerId}/risk-profile")
    public ResponseEntity<CustomerRiskProfileResponse> updateRiskProfile(
            @PathVariable UUID customerId,
            @Valid @RequestBody CustomerRiskProfileRequest request) {

        return ResponseEntity.ok(
                customerRiskProfileService.updateRiskProfile(customerId, request)
        );
    }

    @DeleteMapping("/{customerId}/risk-profile")
    public ResponseEntity<Void> deleteRiskProfile(
            @PathVariable UUID customerId) {

        customerRiskProfileService.deleteRiskProfile(customerId);

        return ResponseEntity.noContent().build();
    }
}