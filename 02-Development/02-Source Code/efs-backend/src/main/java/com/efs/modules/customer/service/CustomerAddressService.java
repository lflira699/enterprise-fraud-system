package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerAddressRequest;
import com.efs.modules.customer.dto.CustomerAddressResponse;
import com.efs.modules.customer.entity.CustomerAddress;
import com.efs.modules.customer.mapper.CustomerAddressMapper;
import com.efs.modules.customer.repository.CustomerAddressRepository;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerAddressService
        implements CustomerAddressServiceInterface {

    private final CustomerAddressRepository customerAddressRepository;
    private final CustomerRepository customerRepository;
    private final CustomerAddressMapper customerAddressMapper;

    public CustomerAddressService(
            CustomerAddressRepository customerAddressRepository,
            CustomerRepository customerRepository,
            CustomerAddressMapper customerAddressMapper) {

        this.customerAddressRepository = customerAddressRepository;
        this.customerRepository = customerRepository;
        this.customerAddressMapper = customerAddressMapper;
    }

    @Override
    @Transactional
    public CustomerAddressResponse createAddress(
            UUID customerId,
            CustomerAddressRequest request) {

        customerRepository
                .findByCustomerIdAndDeletedAtIsNull(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found: " + customerId
                        )
                );

        CustomerAddress address =
                customerAddressMapper.toEntity(request);

        address.setCustomerId(customerId);

        if (address.getPrimary() == null) {
            address.setPrimary(Boolean.FALSE);
        }

        LocalDateTime now =
                LocalDateTime.now();

        address.setCreatedAt(now);
        address.setUpdatedAt(now);

        CustomerAddress savedAddress =
                customerAddressRepository.save(address);

        return customerAddressMapper.toResponse(savedAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerAddressResponse getAddressById(
            UUID customerAddressId) {

        CustomerAddress address =
                customerAddressRepository
                        .findByCustomerAddressIdAndDeletedAtIsNull(
                                customerAddressId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer address not found: "
                                                + customerAddressId
                                )
                        );

        return customerAddressMapper.toResponse(address);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerAddressResponse> getAddressesByCustomerId(
            UUID customerId) {

        customerRepository
                .findByCustomerIdAndDeletedAtIsNull(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found: " + customerId
                        )
                );

        return customerAddressRepository
                .findByCustomerIdAndDeletedAtIsNull(customerId)
                .stream()
                .map(customerAddressMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CustomerAddressResponse updateAddress(
            UUID customerAddressId,
            CustomerAddressRequest request) {

        CustomerAddress address =
                customerAddressRepository
                        .findByCustomerAddressIdAndDeletedAtIsNull(
                                customerAddressId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer address not found: "
                                                + customerAddressId
                                )
                        );

        customerAddressMapper.updateEntity(
                request,
                address
        );

        if (address.getPrimary() == null) {
            address.setPrimary(Boolean.FALSE);
        }

        address.setUpdatedAt(
                LocalDateTime.now()
        );

        CustomerAddress savedAddress =
                customerAddressRepository.save(address);

        return customerAddressMapper.toResponse(savedAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(
            UUID customerAddressId,
            UUID deletedBy) {

        CustomerAddress address =
                customerAddressRepository
                        .findByCustomerAddressIdAndDeletedAtIsNull(
                                customerAddressId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer address not found: "
                                                + customerAddressId
                                )
                        );

        LocalDateTime now =
                LocalDateTime.now();

        address.setDeletedAt(now);
        address.setDeletedBy(deletedBy);
        address.setUpdatedAt(now);

        customerAddressRepository.save(address);
    }
}