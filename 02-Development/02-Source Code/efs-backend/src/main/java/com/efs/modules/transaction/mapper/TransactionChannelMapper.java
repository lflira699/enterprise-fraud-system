package com.efs.modules.transaction.mapper;

import com.efs.modules.transaction.dto.TransactionChannelRequest;
import com.efs.modules.transaction.dto.TransactionChannelResponse;
import com.efs.modules.transaction.entity.TransactionChannel;
import org.springframework.stereotype.Component;

@Component
public class TransactionChannelMapper {

    public TransactionChannel toEntity(
            TransactionChannelRequest request) {

        TransactionChannel channel =
                new TransactionChannel();

        channel.setChannelType(
                request.getChannelType()
        );

        channel.setApplicationName(
                request.getApplicationName()
        );

        channel.setApplicationVersion(
                request.getApplicationVersion()
        );

        channel.setSdkVersion(
                request.getSdkVersion()
        );

        channel.setApiVersion(
                request.getApiVersion()
        );

        channel.setAuthenticationMethod(
                request.getAuthenticationMethod()
        );

        channel.setSessionDuration(
                request.getSessionDuration()
        );

        return channel;
    }

    public TransactionChannelResponse toResponse(
            TransactionChannel channel) {

        TransactionChannelResponse response =
                new TransactionChannelResponse();

        response.setChannelTransactionId(
                channel.getChannelTransactionId()
        );

        response.setTransactionId(
                channel.getTransactionId()
        );

        response.setChannelType(
                channel.getChannelType()
        );

        response.setApplicationName(
                channel.getApplicationName()
        );

        response.setApplicationVersion(
                channel.getApplicationVersion()
        );

        response.setSdkVersion(
                channel.getSdkVersion()
        );

        response.setApiVersion(
                channel.getApiVersion()
        );

        response.setAuthenticationMethod(
                channel.getAuthenticationMethod()
        );

        response.setSessionDuration(
                channel.getSessionDuration()
        );

        response.setCreatedAt(
                channel.getCreatedAt()
        );

        return response;
    }
}