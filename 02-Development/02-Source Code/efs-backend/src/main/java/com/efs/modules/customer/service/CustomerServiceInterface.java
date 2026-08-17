package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerRequest;
import com.efs.modules.customer.dto.CustomerResponse;

import java.util.List;
import java.util.UUID;

public interface CustomerServiceInterface {

    CustomerResponse createCustomer(CustomerRequest request);

    CustomerResponse getCustomerById(UUID customerId);

    CustomerResponse getCustomerByNumber(String customerNumber);

    List<CustomerResponse> getAllCustomers();

    CustomerResponse updateCustomer(UUID customerId, CustomerRequest request);

    void deleteCustomer(UUID customerId);
}