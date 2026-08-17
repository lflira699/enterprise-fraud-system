package com.efs.modules.transaction.mapper;

import com.efs.modules.transaction.dto.TransactionPaymentMethodRequest;
import com.efs.modules.transaction.dto.TransactionPaymentMethodResponse;
import com.efs.modules.transaction.entity.TransactionPaymentMethod;
import org.springframework.stereotype.Component;

@Component
public class TransactionPaymentMethodMapper {

    public TransactionPaymentMethod toEntity(
            TransactionPaymentMethodRequest request) {

        TransactionPaymentMethod paymentMethod =
                new TransactionPaymentMethod();

        paymentMethod.setPaymentType(
                request.getPaymentType()
        );

        paymentMethod.setNetwork(
                request.getNetwork()
        );

        paymentMethod.setIssuer(
                request.getIssuer()
        );

        paymentMethod.setMaskedIdentifier(
                request.getMaskedIdentifier()
        );

        paymentMethod.setTokenReference(
                request.getTokenReference()
        );

        paymentMethod.setExpirationDate(
                request.getExpirationDate()
        );

        return paymentMethod;
    }

    public TransactionPaymentMethodResponse toResponse(
            TransactionPaymentMethod paymentMethod) {

        TransactionPaymentMethodResponse response =
                new TransactionPaymentMethodResponse();

        response.setPaymentMethodId(
                paymentMethod.getPaymentMethodId()
        );

        response.setTransactionId(
                paymentMethod.getTransactionId()
        );

        response.setPaymentType(
                paymentMethod.getPaymentType()
        );

        response.setNetwork(
                paymentMethod.getNetwork()
        );

        response.setIssuer(
                paymentMethod.getIssuer()
        );

        response.setMaskedIdentifier(
                paymentMethod.getMaskedIdentifier()
        );

        response.setTokenReference(
                paymentMethod.getTokenReference()
        );

        response.setExpirationDate(
                paymentMethod.getExpirationDate()
        );

        response.setCreatedAt(
                paymentMethod.getCreatedAt()
        );

        return response;
    }
}