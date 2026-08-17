package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerBankAccountRequest;
import com.efs.modules.customer.dto.CustomerBankAccountResponse;

import java.util.List;
import java.util.UUID;

public interface CustomerBankAccountServiceInterface {

    CustomerBankAccountResponse createBankAccount(
            UUID customerId,
            CustomerBankAccountRequest request
    );

    CustomerBankAccountResponse getBankAccountById(
            UUID customerBankAccountId
    );

    List<CustomerBankAccountResponse> getBankAccountsByCustomerId(
            UUID customerId
    );

    CustomerBankAccountResponse updateBankAccount(
            UUID customerBankAccountId,
            CustomerBankAccountRequest request
    );

    void deleteBankAccount(
            UUID customerBankAccountId,
            UUID deletedBy
    );
}