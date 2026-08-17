package com.efs.modules.customer.mapper;

import com.efs.modules.customer.dto.CustomerPhoneRequest;
import com.efs.modules.customer.dto.CustomerPhoneResponse;
import com.efs.modules.customer.entity.CustomerPhone;
import org.springframework.stereotype.Component;

@Component
public class CustomerPhoneMapper {

    public CustomerPhone toEntity(CustomerPhoneRequest request) {
        CustomerPhone phone = new CustomerPhone();

        phone.setPhoneType(request.getPhoneType());
        phone.setCountryCode(request.getCountryCode());
        phone.setPhoneNumber(request.getPhoneNumber());
        phone.setPrimary(request.getPrimary());
        phone.setVerified(request.getVerified());
        phone.setCreatedBy(request.getCreatedBy());
        phone.setUpdatedBy(request.getUpdatedBy());

        return phone;
    }

    public void updateEntity(
            CustomerPhoneRequest request,
            CustomerPhone phone) {

        phone.setPhoneType(request.getPhoneType());
        phone.setCountryCode(request.getCountryCode());
        phone.setPhoneNumber(request.getPhoneNumber());
        phone.setPrimary(request.getPrimary());
        phone.setVerified(request.getVerified());
        phone.setUpdatedBy(request.getUpdatedBy());
    }

    public CustomerPhoneResponse toResponse(CustomerPhone phone) {
        CustomerPhoneResponse response = new CustomerPhoneResponse();

        response.setCustomerPhoneId(phone.getCustomerPhoneId());
        response.setCustomerId(phone.getCustomerId());
        response.setPhoneType(phone.getPhoneType());
        response.setCountryCode(phone.getCountryCode());
        response.setPhoneNumber(phone.getPhoneNumber());
        response.setPrimary(phone.getPrimary());
        response.setVerified(phone.getVerified());
        response.setVerifiedAt(phone.getVerifiedAt());
        response.setCreatedAt(phone.getCreatedAt());
        response.setCreatedBy(phone.getCreatedBy());
        response.setUpdatedAt(phone.getUpdatedAt());
        response.setUpdatedBy(phone.getUpdatedBy());
        response.setDeletedAt(phone.getDeletedAt());
        response.setDeletedBy(phone.getDeletedBy());
        response.setRecordVersion(phone.getRecordVersion());

        return response;
    }
}