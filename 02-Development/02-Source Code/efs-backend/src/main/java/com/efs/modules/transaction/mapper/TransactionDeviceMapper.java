package com.efs.modules.transaction.mapper;

import com.efs.modules.transaction.dto.TransactionDeviceRequest;
import com.efs.modules.transaction.dto.TransactionDeviceResponse;
import com.efs.modules.transaction.entity.TransactionDevice;
import org.springframework.stereotype.Component;

@Component
public class TransactionDeviceMapper {

    public TransactionDevice toEntity(
            TransactionDeviceRequest request) {

        TransactionDevice device = new TransactionDevice();

        device.setDeviceId(request.getDeviceId());
        device.setDeviceFingerprint(request.getDeviceFingerprint());
        device.setDeviceType(request.getDeviceType());
        device.setOperatingSystem(request.getOperatingSystem());
        device.setOsVersion(request.getOsVersion());
        device.setBrowser(request.getBrowser());
        device.setBrowserVersion(request.getBrowserVersion());
        device.setScreenResolution(request.getScreenResolution());
        device.setLanguage(request.getLanguage());
        device.setTimezone(request.getTimezone());
        device.setTrustScore(request.getTrustScore());

        return device;
    }

    public TransactionDeviceResponse toResponse(
            TransactionDevice device) {

        TransactionDeviceResponse response =
                new TransactionDeviceResponse();

        response.setDeviceTransactionId(
                device.getDeviceTransactionId()
        );

        response.setTransactionId(
                device.getTransactionId()
        );

        response.setDeviceId(
                device.getDeviceId()
        );

        response.setDeviceFingerprint(
                device.getDeviceFingerprint()
        );

        response.setDeviceType(
                device.getDeviceType()
        );

        response.setOperatingSystem(
                device.getOperatingSystem()
        );

        response.setOsVersion(
                device.getOsVersion()
        );

        response.setBrowser(
                device.getBrowser()
        );

        response.setBrowserVersion(
                device.getBrowserVersion()
        );

        response.setScreenResolution(
                device.getScreenResolution()
        );

        response.setLanguage(
                device.getLanguage()
        );

        response.setTimezone(
                device.getTimezone()
        );

        response.setTrustScore(
                device.getTrustScore()
        );

        response.setCreatedAt(
                device.getCreatedAt()
        );

        return response;
    }
}