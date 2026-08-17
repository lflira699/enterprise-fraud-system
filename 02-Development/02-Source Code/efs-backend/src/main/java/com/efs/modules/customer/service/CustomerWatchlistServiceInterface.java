package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerWatchlistRequest;
import com.efs.modules.customer.dto.CustomerWatchlistResponse;

import java.util.List;
import java.util.UUID;

public interface CustomerWatchlistServiceInterface {

    CustomerWatchlistResponse createWatchlist(
            UUID customerId,
            CustomerWatchlistRequest request
    );

    CustomerWatchlistResponse getWatchlistById(
            UUID watchlistId
    );

    List<CustomerWatchlistResponse> getWatchlistsByCustomerId(
            UUID customerId
    );

    CustomerWatchlistResponse updateWatchlist(
            UUID watchlistId,
            CustomerWatchlistRequest request
    );

    void deleteWatchlist(
            UUID watchlistId
    );
}