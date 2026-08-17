package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerEmailRequest;
import com.efs.modules.customer.dto.CustomerEmailResponse;

import java.util.List;
import java.util.UUID;

public interface CustomerEmailServiceInterface {

    CustomerEmailResponse createEmail(
            UUID customerId,
            CustomerEmailRequest request
    );

    CustomerEmailResponse getEmailById(UUID customerEmailId);

    List<CustomerEmailResponse> getEmailsByCustomerId(UUID customerId);

    CustomerEmailResponse updateEmail(
            UUID customerEmailId,
            CustomerEmailRequest request
    );

    void deleteEmail(
            UUID customerEmailId,
            UUID deletedBy
    );
}