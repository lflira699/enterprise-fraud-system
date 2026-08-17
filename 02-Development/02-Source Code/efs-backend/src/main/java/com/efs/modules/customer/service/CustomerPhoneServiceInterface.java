package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerPhoneRequest;
import com.efs.modules.customer.dto.CustomerPhoneResponse;

import java.util.List;
import java.util.UUID;

public interface CustomerPhoneServiceInterface {

    CustomerPhoneResponse createPhone(
            UUID customerId,
            CustomerPhoneRequest request
    );

    CustomerPhoneResponse getPhoneById(UUID customerPhoneId);

    List<CustomerPhoneResponse> getPhonesByCustomerId(UUID customerId);

    CustomerPhoneResponse updatePhone(
            UUID customerPhoneId,
            CustomerPhoneRequest request
    );

    void deletePhone(
            UUID customerPhoneId,
            UUID deletedBy
    );
}