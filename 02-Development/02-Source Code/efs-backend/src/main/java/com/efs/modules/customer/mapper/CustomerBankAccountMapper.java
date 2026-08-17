package com.efs.modules.customer.mapper;

import com.efs.modules.customer.dto.CustomerBankAccountRequest;
import com.efs.modules.customer.dto.CustomerBankAccountResponse;
import com.efs.modules.customer.entity.CustomerBankAccount;
import org.springframework.stereotype.Component;

@Component
public class CustomerBankAccountMapper {

    public CustomerBankAccount toEntity(CustomerBankAccountRequest request) {
        CustomerBankAccount bankAccount = new CustomerBankAccount();

        bankAccount.setBankName(request.getBankName());
        bankAccount.setAccountNumber(request.getAccountNumber());
        bankAccount.setAccountType(request.getAccountType());
        bankAccount.setCurrencyCode(request.getCurrencyCode());
        bankAccount.setCountryCode(request.getCountryCode());
        bankAccount.setPrimary(request.getPrimary());
        bankAccount.setVerified(request.getVerified());
        bankAccount.setVerificationStatus(request.getVerificationStatus());
        bankAccount.setCreatedBy(request.getCreatedBy());
        bankAccount.setUpdatedBy(request.getUpdatedBy());

        return bankAccount;
    }

    public void updateEntity(
            CustomerBankAccountRequest request,
            CustomerBankAccount bankAccount) {

        bankAccount.setBankName(request.getBankName());
        bankAccount.setAccountNumber(request.getAccountNumber());
        bankAccount.setAccountType(request.getAccountType());
        bankAccount.setCurrencyCode(request.getCurrencyCode());
        bankAccount.setCountryCode(request.getCountryCode());
        bankAccount.setPrimary(request.getPrimary());
        bankAccount.setVerified(request.getVerified());
        bankAccount.setVerificationStatus(request.getVerificationStatus());
        bankAccount.setUpdatedBy(request.getUpdatedBy());
    }

    public CustomerBankAccountResponse toResponse(
            CustomerBankAccount bankAccount) {

        CustomerBankAccountResponse response =
                new CustomerBankAccountResponse();

        response.setCustomerBankAccountId(
                bankAccount.getCustomerBankAccountId()
        );
        response.setCustomerId(bankAccount.getCustomerId());
        response.setBankName(bankAccount.getBankName());
        response.setAccountNumber(bankAccount.getAccountNumber());
        response.setAccountType(bankAccount.getAccountType());
        response.setCurrencyCode(bankAccount.getCurrencyCode());
        response.setCountryCode(bankAccount.getCountryCode());
        response.setPrimary(bankAccount.getPrimary());
        response.setVerified(bankAccount.getVerified());
        response.setVerificationStatus(
                bankAccount.getVerificationStatus()
        );
        response.setCreatedAt(bankAccount.getCreatedAt());
        response.setCreatedBy(bankAccount.getCreatedBy());
        response.setUpdatedAt(bankAccount.getUpdatedAt());
        response.setUpdatedBy(bankAccount.getUpdatedBy());
        response.setDeletedAt(bankAccount.getDeletedAt());
        response.setDeletedBy(bankAccount.getDeletedBy());
        response.setRecordVersion(bankAccount.getRecordVersion());

        return response;
    }
}