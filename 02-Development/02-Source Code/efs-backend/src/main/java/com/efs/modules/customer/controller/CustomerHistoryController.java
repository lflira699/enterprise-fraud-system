package com.efs.modules.customer.controller;

import com.efs.modules.customer.dto.CustomerHistoryRequest;
import com.efs.modules.customer.dto.CustomerHistoryResponse;
import com.efs.modules.customer.service.CustomerHistoryServiceInterface;
import com.efs.shared.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerHistoryController {

    private final CustomerHistoryServiceInterface customerHistoryService;

    public CustomerHistoryController(
            CustomerHistoryServiceInterface customerHistoryService) {
        this.customerHistoryService = customerHistoryService;
    }

    @PostMapping("/{customerId}/history")
    public ResponseEntity<CustomerHistoryResponse> createHistory(
            @PathVariable UUID customerId,
            @Valid @RequestBody CustomerHistoryRequest request) {

        CustomerHistoryResponse response =
                customerHistoryService.createHistory(
                        customerId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{customerId}/history")
    public ResponseEntity<List<CustomerHistoryResponse>>
    getHistoryByCustomer(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                customerHistoryService
                        .getHistoryByCustomerId(customerId)
        );
    }

    @GetMapping("/{customerId}/history/{historyId}")
    public ResponseEntity<CustomerHistoryResponse> getHistoryById(
            @PathVariable UUID customerId,
            @PathVariable UUID historyId) {

        CustomerHistoryResponse response =
                customerHistoryService
                        .getHistoryById(historyId);

        if (!response.getCustomerId().equals(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer history not found: " + historyId
            );
        }

        return ResponseEntity.ok(response);
    }
}