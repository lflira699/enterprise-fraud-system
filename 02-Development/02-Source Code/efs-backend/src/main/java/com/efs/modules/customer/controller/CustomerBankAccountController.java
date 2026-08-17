package com.efs.modules.customer.controller;

import com.efs.modules.customer.dto.CustomerBankAccountRequest;
import com.efs.modules.customer.dto.CustomerBankAccountResponse;
import com.efs.modules.customer.service.CustomerBankAccountServiceInterface;
import com.efs.shared.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerBankAccountController {

    private final CustomerBankAccountServiceInterface customerBankAccountService;

    public CustomerBankAccountController(
            CustomerBankAccountServiceInterface customerBankAccountService) {
        this.customerBankAccountService = customerBankAccountService;
    }

    @PostMapping("/{customerId}/bank-accounts")
    public ResponseEntity<CustomerBankAccountResponse> createBankAccount(
            @PathVariable UUID customerId,
            @Valid @RequestBody CustomerBankAccountRequest request) {

        CustomerBankAccountResponse response =
                customerBankAccountService.createBankAccount(customerId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{customerId}/bank-accounts")
    public ResponseEntity<List<CustomerBankAccountResponse>> getBankAccountsByCustomer(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                customerBankAccountService.getBankAccountsByCustomerId(customerId)
        );
    }

    @GetMapping("/{customerId}/bank-accounts/{bankAccountId}")
    public ResponseEntity<CustomerBankAccountResponse> getBankAccountById(
            @PathVariable UUID customerId,
            @PathVariable UUID bankAccountId) {

        CustomerBankAccountResponse response =
                customerBankAccountService.getBankAccountById(bankAccountId);

        if (!response.getCustomerId().equals(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer bank account not found: " + bankAccountId
            );
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{customerId}/bank-accounts/{bankAccountId}")
    public ResponseEntity<CustomerBankAccountResponse> updateBankAccount(
            @PathVariable UUID customerId,
            @PathVariable UUID bankAccountId,
            @Valid @RequestBody CustomerBankAccountRequest request) {

        CustomerBankAccountResponse existing =
                customerBankAccountService.getBankAccountById(bankAccountId);

        if (!existing.getCustomerId().equals(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer bank account not found: " + bankAccountId
            );
        }

        return ResponseEntity.ok(
                customerBankAccountService.updateBankAccount(bankAccountId, request)
        );
    }

    @DeleteMapping("/{customerId}/bank-accounts/{bankAccountId}")
    public ResponseEntity<Void> deleteBankAccount(
            @PathVariable UUID customerId,
            @PathVariable UUID bankAccountId,
            @RequestParam(required = false) UUID deletedBy) {

        CustomerBankAccountResponse existing =
                customerBankAccountService.getBankAccountById(bankAccountId);

        if (!existing.getCustomerId().equals(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer bank account not found: " + bankAccountId
            );
        }

        customerBankAccountService.deleteBankAccount(bankAccountId, deletedBy);

        return ResponseEntity.noContent().build();
    }
}