package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerHistoryRequest;
import com.efs.modules.customer.dto.CustomerHistoryResponse;

import java.util.List;
import java.util.UUID;

public interface CustomerHistoryServiceInterface {

    CustomerHistoryResponse createHistory(
            UUID customerId,
            CustomerHistoryRequest request
    );

    CustomerHistoryResponse getHistoryById(
            UUID customerHistoryId
    );

    List<CustomerHistoryResponse> getHistoryByCustomerId(
            UUID customerId
    );
}