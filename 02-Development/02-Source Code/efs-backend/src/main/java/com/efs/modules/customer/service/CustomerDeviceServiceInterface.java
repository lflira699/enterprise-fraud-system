package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerDeviceRequest;
import com.efs.modules.customer.dto.CustomerDeviceResponse;

import java.util.List;
import java.util.UUID;

public interface CustomerDeviceServiceInterface {

    CustomerDeviceResponse createDevice(
            UUID customerId,
            CustomerDeviceRequest request
    );

    CustomerDeviceResponse getDeviceById(UUID deviceId);

    List<CustomerDeviceResponse> getDevicesByCustomerId(UUID customerId);

    CustomerDeviceResponse updateDevice(
            UUID deviceId,
            CustomerDeviceRequest request
    );
}