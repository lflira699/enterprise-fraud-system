package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerRequest;
import com.efs.modules.customer.dto.CustomerResponse;
import com.efs.modules.customer.entity.Customer;
import com.efs.modules.customer.mapper.CustomerMapper;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.shared.exception.DuplicateRecordException;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerService implements CustomerServiceInterface {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(
            CustomerRepository customerRepository,
            CustomerMapper customerMapper) {

        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {

        if (customerRepository.existsByCustomerNumber(
                request.getCustomerNumber())) {

            throw new DuplicateRecordException(
                    "Customer number already exists: "
                            + request.getCustomerNumber()
            );
        }

        Customer customer =
                customerMapper.toEntity(request);

        customer.setRiskLevel(
                request.getRiskLevel() != null
                        ? request.getRiskLevel()
                        : "LOW"
        );

        customer.setRiskScore(
                request.getRiskScore() != null
                        ? request.getRiskScore()
                        : BigDecimal.ZERO
        );

        customer.setCustomerStatus(
                request.getCustomerStatus() != null
                        ? request.getCustomerStatus()
                        : "ACTIVE"
        );

        customer.setRecordStatus("ACTIVE");
        customer.setRecordVersion(1);

        LocalDateTime now =
                LocalDateTime.now();

        customer.setCreatedAt(now);
        customer.setUpdatedAt(now);

        Customer savedCustomer =
                customerRepository.save(customer);

        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(
            UUID customerId) {

        Customer customer =
                customerRepository
                        .findByCustomerIdAndDeletedAtIsNull(
                                customerId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found: "
                                                + customerId
                                )
                        );

        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerByNumber(
            String customerNumber) {

        Customer customer =
                customerRepository
                        .findByCustomerNumberAndDeletedAtIsNull(
                                customerNumber
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found: "
                                                + customerNumber
                                )
                        );

        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {

        return customerRepository
                .findAllByDeletedAtIsNull()
                .stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomer(
            UUID customerId,
            CustomerRequest request) {

        Customer customer =
                customerRepository
                        .findByCustomerIdAndDeletedAtIsNull(
                                customerId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found: "
                                                + customerId
                                )
                        );

        if (!customer.getCustomerNumber()
                .equals(request.getCustomerNumber())
                && customerRepository.existsByCustomerNumber(
                        request.getCustomerNumber())) {

            throw new DuplicateRecordException(
                    "Customer number already exists: "
                            + request.getCustomerNumber()
            );
        }

        customerMapper.updateEntity(
                request,
                customer
        );

        if (customer.getRiskLevel() == null) {
            customer.setRiskLevel("LOW");
        }

        if (customer.getRiskScore() == null) {
            customer.setRiskScore(BigDecimal.ZERO);
        }

        if (customer.getCustomerStatus() == null) {
            customer.setCustomerStatus("ACTIVE");
        }

        customer.setUpdatedAt(
                LocalDateTime.now()
        );

        Customer savedCustomer =
                customerRepository.save(customer);

        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    @Transactional
    public void deleteCustomer(
            UUID customerId) {

        Customer customer =
                customerRepository
                        .findByCustomerIdAndDeletedAtIsNull(
                                customerId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found: "
                                                + customerId
                                )
                        );

        LocalDateTime now =
                LocalDateTime.now();

        customer.setRecordStatus("DELETED");
        customer.setDeletedAt(now);
        customer.setUpdatedAt(now);

        customerRepository.save(customer);
    }
}