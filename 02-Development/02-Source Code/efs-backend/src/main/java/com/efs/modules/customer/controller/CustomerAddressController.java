package com.efs.modules.customer.controller;

import com.efs.modules.customer.dto.CustomerAddressRequest;
import com.efs.modules.customer.dto.CustomerAddressResponse;
import com.efs.modules.customer.service.CustomerAddressServiceInterface;
import com.efs.shared.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerAddressController {

    private final CustomerAddressServiceInterface customerAddressService;

    public CustomerAddressController(
            CustomerAddressServiceInterface customerAddressService) {
        this.customerAddressService = customerAddressService;
    }

    @PostMapping("/{customerId}/addresses")
    public ResponseEntity<CustomerAddressResponse> createAddress(
            @PathVariable UUID customerId,
            @Valid @RequestBody CustomerAddressRequest request) {

        CustomerAddressResponse response =
                customerAddressService.createAddress(customerId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{customerId}/addresses")
    public ResponseEntity<List<CustomerAddressResponse>> getAddressesByCustomer(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                customerAddressService.getAddressesByCustomerId(customerId)
        );
    }

    @GetMapping("/{customerId}/addresses/{addressId}")
    public ResponseEntity<CustomerAddressResponse> getAddressById(
            @PathVariable UUID customerId,
            @PathVariable UUID addressId) {

        CustomerAddressResponse response =
                customerAddressService.getAddressById(addressId);

        if (!response.getCustomerId().equals(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer address not found: " + addressId
            );
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{customerId}/addresses/{addressId}")
    public ResponseEntity<CustomerAddressResponse> updateAddress(
            @PathVariable UUID customerId,
            @PathVariable UUID addressId,
            @Valid @RequestBody CustomerAddressRequest request) {

        CustomerAddressResponse existing =
                customerAddressService.getAddressById(addressId);

        if (!existing.getCustomerId().equals(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer address not found: " + addressId
            );
        }

        return ResponseEntity.ok(
                customerAddressService.updateAddress(addressId, request)
        );
    }

    @DeleteMapping("/{customerId}/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable UUID customerId,
            @PathVariable UUID addressId,
            @RequestParam(required = false) UUID deletedBy) {

        CustomerAddressResponse existing =
                customerAddressService.getAddressById(addressId);

        if (!existing.getCustomerId().equals(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer address not found: " + addressId
            );
        }

        customerAddressService.deleteAddress(addressId, deletedBy);

        return ResponseEntity.noContent().build();
    }
}