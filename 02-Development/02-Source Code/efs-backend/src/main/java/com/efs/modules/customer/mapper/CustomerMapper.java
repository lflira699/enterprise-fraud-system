package com.efs.modules.customer.mapper;

import com.efs.modules.customer.dto.CustomerRequest;
import com.efs.modules.customer.dto.CustomerResponse;
import com.efs.modules.customer.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerRequest request) {
        Customer customer = new Customer();

        customer.setCustomerNumber(request.getCustomerNumber());
        customer.setCustomerType(request.getCustomerType());
        customer.setFirstName(request.getFirstName());
        customer.setMiddleName(request.getMiddleName());
        customer.setLastName(request.getLastName());
        customer.setSecondLastName(request.getSecondLastName());
        customer.setLegalName(request.getLegalName());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setCountryId(request.getCountryId());
        customer.setRiskLevel(request.getRiskLevel());
        customer.setRiskScore(request.getRiskScore());
        customer.setCustomerStatus(request.getCustomerStatus());
        customer.setTenantId(request.getTenantId());

        return customer;
    }

    public void updateEntity(CustomerRequest request, Customer customer) {
        customer.setCustomerNumber(request.getCustomerNumber());
        customer.setCustomerType(request.getCustomerType());
        customer.setFirstName(request.getFirstName());
        customer.setMiddleName(request.getMiddleName());
        customer.setLastName(request.getLastName());
        customer.setSecondLastName(request.getSecondLastName());
        customer.setLegalName(request.getLegalName());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setCountryId(request.getCountryId());
        customer.setRiskLevel(request.getRiskLevel());
        customer.setRiskScore(request.getRiskScore());
        customer.setCustomerStatus(request.getCustomerStatus());
        customer.setTenantId(request.getTenantId());
    }

    public CustomerResponse toResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();

        response.setCustomerId(customer.getCustomerId());
        response.setCustomerNumber(customer.getCustomerNumber());
        response.setCustomerType(customer.getCustomerType());
        response.setFirstName(customer.getFirstName());
        response.setMiddleName(customer.getMiddleName());
        response.setLastName(customer.getLastName());
        response.setSecondLastName(customer.getSecondLastName());
        response.setLegalName(customer.getLegalName());
        response.setDateOfBirth(customer.getDateOfBirth());
        response.setCountryId(customer.getCountryId());
        response.setRiskLevel(customer.getRiskLevel());
        response.setRiskScore(customer.getRiskScore());
        response.setCustomerStatus(customer.getCustomerStatus());
        response.setCreatedAt(customer.getCreatedAt());
        response.setUpdatedAt(customer.getUpdatedAt());
        response.setRecordStatus(customer.getRecordStatus());
        response.setRecordVersion(customer.getRecordVersion());
        response.setTenantId(customer.getTenantId());

        return response;
    }
}
