package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerDeviceRequest;
import com.efs.modules.customer.dto.CustomerDeviceResponse;
import com.efs.modules.customer.entity.CustomerDevice;
import com.efs.modules.customer.mapper.CustomerDeviceMapper;
import com.efs.modules.customer.repository.CustomerDeviceRepository;
import com.efs.modules.customer.repository.CustomerRepository;
import com.efs.shared.exception.DuplicateRecordException;
import com.efs.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerDeviceService
        implements CustomerDeviceServiceInterface {

    private final CustomerDeviceRepository customerDeviceRepository;
    private final CustomerRepository customerRepository;
    private final CustomerDeviceMapper customerDeviceMapper;

    public CustomerDeviceService(
            CustomerDeviceRepository customerDeviceRepository,
            CustomerRepository customerRepository,
            CustomerDeviceMapper customerDeviceMapper) {

        this.customerDeviceRepository = customerDeviceRepository;
        this.customerRepository = customerRepository;
        this.customerDeviceMapper = customerDeviceMapper;
    }

    @Override
    @Transactional
    public CustomerDeviceResponse createDevice(
            UUID customerId,
            CustomerDeviceRequest request) {

        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer not found: " + customerId
            );
        }

        if (customerDeviceRepository
                .existsByCustomerIdAndDeviceFingerprint(
                        customerId,
                        request.getDeviceFingerprint())) {

            throw new DuplicateRecordException(
                    "Customer device already exists"
            );
        }

        CustomerDevice device =
                customerDeviceMapper.toEntity(request);

        device.setCustomerId(customerId);

        if (device.getActive() == null) {
            device.setActive(Boolean.TRUE);
        }

        CustomerDevice savedDevice =
                customerDeviceRepository.save(device);

        return customerDeviceMapper.toResponse(savedDevice);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDeviceResponse getDeviceById(UUID deviceId) {

        CustomerDevice device =
                customerDeviceRepository.findById(deviceId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer device not found: " + deviceId
                                )
                        );

        return customerDeviceMapper.toResponse(device);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDeviceResponse> getDevicesByCustomerId(
            UUID customerId) {

        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer not found: " + customerId
            );
        }

        return customerDeviceRepository
                .findByCustomerId(customerId)
                .stream()
                .map(customerDeviceMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CustomerDeviceResponse updateDevice(
            UUID deviceId,
            CustomerDeviceRequest request) {

        CustomerDevice device =
                customerDeviceRepository.findById(deviceId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer device not found: " + deviceId
                                )
                        );

        boolean duplicateExists =
                customerDeviceRepository
                        .existsByCustomerIdAndDeviceFingerprint(
                                device.getCustomerId(),
                                request.getDeviceFingerprint()
                        );

        if (duplicateExists
                && !device.getDeviceFingerprint()
                .equals(request.getDeviceFingerprint())) {

            throw new DuplicateRecordException(
                    "Customer device already exists"
            );
        }

        customerDeviceMapper.updateEntity(request, device);

        if (device.getActive() == null) {
            device.setActive(Boolean.TRUE);
        }

        CustomerDevice savedDevice =
                customerDeviceRepository.save(device);

        return customerDeviceMapper.toResponse(savedDevice);
    }
}