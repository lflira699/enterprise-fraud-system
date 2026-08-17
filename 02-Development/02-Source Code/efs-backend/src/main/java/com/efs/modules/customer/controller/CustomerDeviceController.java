package com.efs.modules.customer.controller;

import com.efs.modules.customer.dto.CustomerDeviceRequest;
import com.efs.modules.customer.dto.CustomerDeviceResponse;
import com.efs.modules.customer.service.CustomerDeviceServiceInterface;
import com.efs.shared.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerDeviceController {

    private final CustomerDeviceServiceInterface customerDeviceService;

    public CustomerDeviceController(
            CustomerDeviceServiceInterface customerDeviceService) {
        this.customerDeviceService = customerDeviceService;
    }

    @PostMapping("/{customerId}/devices")
    public ResponseEntity<CustomerDeviceResponse> createDevice(
            @PathVariable UUID customerId,
            @Valid @RequestBody CustomerDeviceRequest request) {

        CustomerDeviceResponse response =
                customerDeviceService.createDevice(customerId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{customerId}/devices")
    public ResponseEntity<List<CustomerDeviceResponse>> getDevicesByCustomer(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                customerDeviceService.getDevicesByCustomerId(customerId)
        );
    }

    @GetMapping("/{customerId}/devices/{deviceId}")
    public ResponseEntity<CustomerDeviceResponse> getDeviceById(
            @PathVariable UUID customerId,
            @PathVariable UUID deviceId) {

        CustomerDeviceResponse response =
                customerDeviceService.getDeviceById(deviceId);

        if (!response.getCustomerId().equals(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer device not found: " + deviceId
            );
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{customerId}/devices/{deviceId}")
    public ResponseEntity<CustomerDeviceResponse> updateDevice(
            @PathVariable UUID customerId,
            @PathVariable UUID deviceId,
            @Valid @RequestBody CustomerDeviceRequest request) {

        CustomerDeviceResponse existing =
                customerDeviceService.getDeviceById(deviceId);

        if (!existing.getCustomerId().equals(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer device not found: " + deviceId
            );
        }

        return ResponseEntity.ok(
                customerDeviceService.updateDevice(deviceId, request)
        );
    }
}