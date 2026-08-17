package com.efs.modules.customer.mapper;

import com.efs.modules.customer.dto.CustomerDeviceRequest;
import com.efs.modules.customer.dto.CustomerDeviceResponse;
import com.efs.modules.customer.entity.CustomerDevice;
import org.springframework.stereotype.Component;

@Component
public class CustomerDeviceMapper {

    public CustomerDevice toEntity(CustomerDeviceRequest request) {
        CustomerDevice device = new CustomerDevice();

        device.setDeviceFingerprint(request.getDeviceFingerprint());
        device.setDeviceType(request.getDeviceType());
        device.setOperatingSystem(request.getOperatingSystem());
        device.setBrowser(request.getBrowser());
        device.setIpAddress(request.getIpAddress());
        device.setCountry(request.getCountry());
        device.setCity(request.getCity());
        device.setTrustLevel(request.getTrustLevel());
        device.setLastSeen(request.getLastSeen());
        device.setActive(request.getActive());

        return device;
    }

    public void updateEntity(
            CustomerDeviceRequest request,
            CustomerDevice device) {

        device.setDeviceFingerprint(request.getDeviceFingerprint());
        device.setDeviceType(request.getDeviceType());
        device.setOperatingSystem(request.getOperatingSystem());
        device.setBrowser(request.getBrowser());
        device.setIpAddress(request.getIpAddress());
        device.setCountry(request.getCountry());
        device.setCity(request.getCity());
        device.setTrustLevel(request.getTrustLevel());
        device.setLastSeen(request.getLastSeen());
        device.setActive(request.getActive());
    }

    public CustomerDeviceResponse toResponse(CustomerDevice device) {
        CustomerDeviceResponse response = new CustomerDeviceResponse();

        response.setDeviceId(device.getDeviceId());
        response.setCustomerId(device.getCustomerId());
        response.setDeviceFingerprint(device.getDeviceFingerprint());
        response.setDeviceType(device.getDeviceType());
        response.setOperatingSystem(device.getOperatingSystem());
        response.setBrowser(device.getBrowser());
        response.setIpAddress(device.getIpAddress());
        response.setCountry(device.getCountry());
        response.setCity(device.getCity());
        response.setTrustLevel(device.getTrustLevel());
        response.setLastSeen(device.getLastSeen());
        response.setActive(device.getActive());

        return response;
    }
}