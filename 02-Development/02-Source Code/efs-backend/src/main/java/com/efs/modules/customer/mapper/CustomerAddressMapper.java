package com.efs.modules.customer.mapper;

import com.efs.modules.customer.dto.CustomerAddressRequest;
import com.efs.modules.customer.dto.CustomerAddressResponse;
import com.efs.modules.customer.entity.CustomerAddress;
import org.springframework.stereotype.Component;

@Component
public class CustomerAddressMapper {

    public CustomerAddress toEntity(CustomerAddressRequest request) {
        CustomerAddress address = new CustomerAddress();

        address.setAddressType(request.getAddressType());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountryCode(request.getCountryCode());
        address.setPrimary(request.getPrimary());
        address.setEffectiveDate(request.getEffectiveDate());
        address.setExpirationDate(request.getExpirationDate());
        address.setCreatedBy(request.getCreatedBy());
        address.setUpdatedBy(request.getUpdatedBy());

        return address;
    }

    public void updateEntity(
            CustomerAddressRequest request,
            CustomerAddress address) {

        address.setAddressType(request.getAddressType());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountryCode(request.getCountryCode());
        address.setPrimary(request.getPrimary());
        address.setEffectiveDate(request.getEffectiveDate());
        address.setExpirationDate(request.getExpirationDate());
        address.setUpdatedBy(request.getUpdatedBy());
    }

    public CustomerAddressResponse toResponse(CustomerAddress address) {
        CustomerAddressResponse response = new CustomerAddressResponse();

        response.setCustomerAddressId(address.getCustomerAddressId());
        response.setCustomerId(address.getCustomerId());
        response.setAddressType(address.getAddressType());
        response.setAddressLine1(address.getAddressLine1());
        response.setAddressLine2(address.getAddressLine2());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setPostalCode(address.getPostalCode());
        response.setCountryCode(address.getCountryCode());
        response.setPrimary(address.getPrimary());
        response.setEffectiveDate(address.getEffectiveDate());
        response.setExpirationDate(address.getExpirationDate());
        response.setCreatedAt(address.getCreatedAt());
        response.setCreatedBy(address.getCreatedBy());
        response.setUpdatedAt(address.getUpdatedAt());
        response.setUpdatedBy(address.getUpdatedBy());
        response.setDeletedAt(address.getDeletedAt());
        response.setDeletedBy(address.getDeletedBy());
        response.setRecordVersion(address.getRecordVersion());

        return response;
    }
}