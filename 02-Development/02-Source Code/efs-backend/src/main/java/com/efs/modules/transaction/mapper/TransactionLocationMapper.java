package com.efs.modules.transaction.mapper;

import com.efs.modules.transaction.dto.TransactionLocationRequest;
import com.efs.modules.transaction.dto.TransactionLocationResponse;
import com.efs.modules.transaction.entity.TransactionLocation;
import com.efs.modules.transaction.validator.TransactionLocationValueValidator;
import org.springframework.stereotype.Component;


@Component
public class TransactionLocationMapper {

    public TransactionLocation toEntity(
            TransactionLocationRequest request) {

        TransactionLocation location =
                new TransactionLocation();

        if (request.getIpAddress() != null
                && !request.getIpAddress().isBlank()) {

            location.setIpAddress(
                    TransactionLocationValueValidator
                            .parseLiteralIpAddress(
                                    request.getIpAddress()
                            )
            );
        }

        location.setCountryCode(
                TransactionLocationValueValidator
                        .normalizeCountryCode(
                                request.getCountryCode()
                        )
        );

        location.setState(
                request.getState()
        );

        location.setCity(
                request.getCity()
        );

        location.setPostalCode(
                request.getPostalCode()
        );

        location.setLatitude(
                TransactionLocationValueValidator
                        .validateLatitude(
                                request.getLatitude()
                        )
        );

        location.setLongitude(
                TransactionLocationValueValidator
                        .validateLongitude(
                                request.getLongitude()
                        )
        );

        location.setAsn(
                request.getAsn()
        );

        location.setInternetProvider(
                request.getInternetProvider()
        );

        location.setVpnDetected(
                request.getVpnDetected()
        );

        location.setProxyDetected(
                request.getProxyDetected()
        );

        location.setTorDetected(
                request.getTorDetected()
        );

        return location;
    }

    public TransactionLocationResponse toResponse(
            TransactionLocation location) {

        TransactionLocationResponse response =
                new TransactionLocationResponse();

        response.setLocationId(
                location.getLocationId()
        );

        response.setTransactionId(
                location.getTransactionId()
        );

        if (location.getIpAddress() != null) {
            response.setIpAddress(
                    location.getIpAddress()
                            .getHostAddress()
            );
        }

        response.setCountryCode(
                location.getCountryCode()
        );

        response.setState(
                location.getState()
        );

        response.setCity(
                location.getCity()
        );

        response.setPostalCode(
                location.getPostalCode()
        );

        response.setLatitude(
                location.getLatitude()
        );

        response.setLongitude(
                location.getLongitude()
        );

        response.setAsn(
                location.getAsn()
        );

        response.setInternetProvider(
                location.getInternetProvider()
        );

        response.setVpnDetected(
                location.getVpnDetected()
        );

        response.setProxyDetected(
                location.getProxyDetected()
        );

        response.setTorDetected(
                location.getTorDetected()
        );

        response.setCreatedAt(
                location.getCreatedAt()
        );

        return response;
    }
}