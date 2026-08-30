package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerHistoryRequest;
import com.efs.modules.customer.dto.CustomerHistoryResponse;
import com.efs.modules.customer.entity.CustomerHistory;
import com.efs.modules.customer.mapper.CustomerHistoryMapper;
import com.efs.modules.customer.repository.CustomerHistoryRepository;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerHistoryService
        implements CustomerHistoryServiceInterface {

    private final CustomerHistoryRepository customerHistoryRepository;
    private final CustomerRepository customerRepository;
    private final CustomerHistoryMapper customerHistoryMapper;

    public CustomerHistoryService(
            CustomerHistoryRepository customerHistoryRepository,
            CustomerRepository customerRepository,
            CustomerHistoryMapper customerHistoryMapper) {

        this.customerHistoryRepository = customerHistoryRepository;
        this.customerRepository = customerRepository;
        this.customerHistoryMapper = customerHistoryMapper;
    }

    @Override
    @Transactional
    public CustomerHistoryResponse createHistory(
            UUID customerId,
            CustomerHistoryRequest request) {

        customerRepository
                .findByCustomerIdAndDeletedAtIsNull(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found: " + customerId
                        )
                );

        CustomerHistory history =
                customerHistoryMapper.toEntity(request);

        history.setCustomerId(customerId);

        LocalDateTime now =
                LocalDateTime.now();

        if (history.getEventTimestamp() == null) {
            history.setEventTimestamp(now);
        }

        history.setCreatedAt(now);

        CustomerHistory savedHistory =
                customerHistoryRepository.save(history);

        return customerHistoryMapper.toResponse(savedHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerHistoryResponse getHistoryById(
            UUID customerHistoryId) {

        CustomerHistory history =
                customerHistoryRepository
                        .findByCustomerHistoryId(customerHistoryId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer history not found: "
                                                + customerHistoryId
                                )
                        );

        return customerHistoryMapper.toResponse(history);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerHistoryResponse> getHistoryByCustomerId(
            UUID customerId) {

        customerRepository
                .findByCustomerIdAndDeletedAtIsNull(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found: " + customerId
                        )
                );

        return customerHistoryRepository
                .findByCustomerIdOrderByEventTimestampDesc(customerId)
                .stream()
                .map(customerHistoryMapper::toResponse)
                .toList();
    }
}