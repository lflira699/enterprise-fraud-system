package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerAddressRequest;
import com.efs.modules.customer.dto.CustomerAddressResponse;

import java.util.List;
import java.util.UUID;

public interface CustomerAddressServiceInterface {

    CustomerAddressResponse createAddress(
            UUID customerId,
            CustomerAddressRequest request
    );

    CustomerAddressResponse getAddressById(UUID customerAddressId);

    List<CustomerAddressResponse> getAddressesByCustomerId(UUID customerId);

    CustomerAddressResponse updateAddress(
            UUID customerAddressId,
            CustomerAddressRequest request
    );

    void deleteAddress(
            UUID customerAddressId,
            UUID deletedBy
    );
}