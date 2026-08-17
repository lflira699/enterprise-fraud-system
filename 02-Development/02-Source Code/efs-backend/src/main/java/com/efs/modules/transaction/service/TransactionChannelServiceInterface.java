package com.efs.modules.transaction.service;

import com.efs.modules.transaction.dto.TransactionChannelRequest;
import com.efs.modules.transaction.dto.TransactionChannelResponse;

import java.util.List;
import java.util.UUID;

public interface TransactionChannelServiceInterface {

    TransactionChannelResponse createChannel(
            UUID transactionId,
            TransactionChannelRequest request
    );

    TransactionChannelResponse getChannelById(
            UUID channelTransactionId
    );

    List<TransactionChannelResponse> getChannelsByTransactionId(
            UUID transactionId
    );

    List<TransactionChannelResponse> getChannelsByType(
            String channelType
    );

    List<TransactionChannelResponse> getChannelsByApplicationName(
            String applicationName
    );
}