package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerWatchlistRequest;
import com.efs.modules.customer.dto.CustomerWatchlistResponse;
import com.efs.modules.customer.entity.CustomerWatchlist;
import com.efs.modules.customer.mapper.CustomerWatchlistMapper;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.modules.customer.repository.CustomerWatchlistRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerWatchlistService
        implements CustomerWatchlistServiceInterface {

    private final CustomerWatchlistRepository customerWatchlistRepository;
    private final CustomerRepository customerRepository;
    private final CustomerWatchlistMapper customerWatchlistMapper;

    public CustomerWatchlistService(
            CustomerWatchlistRepository customerWatchlistRepository,
            CustomerRepository customerRepository,
            CustomerWatchlistMapper customerWatchlistMapper) {

        this.customerWatchlistRepository = customerWatchlistRepository;
        this.customerRepository = customerRepository;
        this.customerWatchlistMapper = customerWatchlistMapper;
    }

    @Override
    @Transactional
    public CustomerWatchlistResponse createWatchlist(
            UUID customerId,
            CustomerWatchlistRequest request) {

        customerRepository
                .findByCustomerIdAndDeletedAtIsNull(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found: " + customerId
                        )
                );

        CustomerWatchlist watchlist =
                customerWatchlistMapper.toEntity(request);

        watchlist.setCustomerId(customerId);

        LocalDateTime now = LocalDateTime.now();

        if (watchlist.getDetectedAt() == null) {
            watchlist.setDetectedAt(now);
        }

        if (watchlist.getActive() == null) {
            watchlist.setActive(Boolean.TRUE);
        }

        watchlist.setCreatedAt(now);
        watchlist.setUpdatedAt(now);

        CustomerWatchlist savedWatchlist =
                customerWatchlistRepository.save(watchlist);

        return customerWatchlistMapper.toResponse(savedWatchlist);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerWatchlistResponse getWatchlistById(
            UUID watchlistId) {

        CustomerWatchlist watchlist =
                customerWatchlistRepository
                        .findByWatchlistIdAndDeletedAtIsNull(
                                watchlistId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer watchlist not found: "
                                                + watchlistId
                                )
                        );

        return customerWatchlistMapper.toResponse(watchlist);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerWatchlistResponse> getWatchlistsByCustomerId(
            UUID customerId) {

        customerRepository
                .findByCustomerIdAndDeletedAtIsNull(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found: " + customerId
                        )
                );

        return customerWatchlistRepository
                .findByCustomerIdAndDeletedAtIsNull(customerId)
                .stream()
                .map(customerWatchlistMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CustomerWatchlistResponse updateWatchlist(
            UUID watchlistId,
            CustomerWatchlistRequest request) {

        CustomerWatchlist watchlist =
                customerWatchlistRepository
                        .findByWatchlistIdAndDeletedAtIsNull(
                                watchlistId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer watchlist not found: "
                                                + watchlistId
                                )
                        );

        customerWatchlistMapper.updateEntity(
                request,
                watchlist
        );

        if (watchlist.getDetectedAt() == null) {
            watchlist.setDetectedAt(LocalDateTime.now());
        }

        if (watchlist.getActive() == null) {
            watchlist.setActive(Boolean.TRUE);
        }

        watchlist.setUpdatedAt(LocalDateTime.now());

        CustomerWatchlist savedWatchlist =
                customerWatchlistRepository.save(watchlist);

        return customerWatchlistMapper.toResponse(savedWatchlist);
    }

    @Override
    @Transactional
    public void deleteWatchlist(UUID watchlistId) {

        CustomerWatchlist watchlist =
                customerWatchlistRepository
                        .findByWatchlistIdAndDeletedAtIsNull(
                                watchlistId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer watchlist not found: "
                                                + watchlistId
                                )
                        );

        LocalDateTime now = LocalDateTime.now();

        watchlist.setDeletedAt(now);
        watchlist.setUpdatedAt(now);
        watchlist.setActive(Boolean.FALSE);

        customerWatchlistRepository.save(watchlist);
    }
}