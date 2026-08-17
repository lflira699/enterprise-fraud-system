package com.efs.modules.customer.mapper;

import com.efs.modules.customer.dto.CustomerEmailRequest;
import com.efs.modules.customer.dto.CustomerEmailResponse;
import com.efs.modules.customer.entity.CustomerEmail;
import org.springframework.stereotype.Component;

@Component
public class CustomerEmailMapper {

    public CustomerEmail toEntity(CustomerEmailRequest request) {
        CustomerEmail email = new CustomerEmail();

        email.setEmailType(request.getEmailType());
        email.setEmailAddress(request.getEmailAddress());
        email.setPrimary(request.getPrimary());
        email.setVerified(request.getVerified());
        email.setCreatedBy(request.getCreatedBy());
        email.setUpdatedBy(request.getUpdatedBy());

        return email;
    }

    public void updateEntity(
            CustomerEmailRequest request,
            CustomerEmail email) {

        email.setEmailType(request.getEmailType());
        email.setEmailAddress(request.getEmailAddress());
        email.setPrimary(request.getPrimary());
        email.setVerified(request.getVerified());
        email.setUpdatedBy(request.getUpdatedBy());
    }

    public CustomerEmailResponse toResponse(CustomerEmail email) {
        CustomerEmailResponse response = new CustomerEmailResponse();

        response.setCustomerEmailId(email.getCustomerEmailId());
        response.setCustomerId(email.getCustomerId());
        response.setEmailType(email.getEmailType());
        response.setEmailAddress(email.getEmailAddress());
        response.setPrimary(email.getPrimary());
        response.setVerified(email.getVerified());
        response.setVerifiedAt(email.getVerifiedAt());
        response.setCreatedAt(email.getCreatedAt());
        response.setCreatedBy(email.getCreatedBy());
        response.setUpdatedAt(email.getUpdatedAt());
        response.setUpdatedBy(email.getUpdatedBy());
        response.setDeletedAt(email.getDeletedAt());
        response.setDeletedBy(email.getDeletedBy());
        response.setRecordVersion(email.getRecordVersion());

        return response;
    }
}