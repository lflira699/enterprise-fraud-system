package com.efs.modules.integration.mapper;

import com.efs.modules.integration.dto.IntegrationQueueRequest;
import com.efs.modules.integration.dto.IntegrationQueueResponse;
import com.efs.modules.integration.entity.IntegrationQueue;
import org.springframework.stereotype.Component;

@Component
public class IntegrationQueueMapper {

    public IntegrationQueue toEntity(
            IntegrationQueueRequest request
    ) {
        IntegrationQueue entity =
                new IntegrationQueue();

        entity.setQueueName(request.getQueueName());
        entity.setBroker(request.getBroker());
        entity.setTopic(request.getTopic());
        entity.setPartition(request.getPartition());
        entity.setConsumerGroup(request.getConsumerGroup());
        entity.setStatus(request.getStatus());

        return entity;
    }

    public void updateEntity(
            IntegrationQueue entity,
            IntegrationQueueRequest request
    ) {
        entity.setQueueName(request.getQueueName());
        entity.setBroker(request.getBroker());
        entity.setTopic(request.getTopic());
        entity.setPartition(request.getPartition());
        entity.setConsumerGroup(request.getConsumerGroup());
        entity.setStatus(request.getStatus());
    }

    public IntegrationQueueResponse toResponse(
            IntegrationQueue entity
    ) {
        IntegrationQueueResponse response =
                new IntegrationQueueResponse();

        response.setQueueId(entity.getQueueId());
        response.setQueueName(entity.getQueueName());
        response.setBroker(entity.getBroker());
        response.setTopic(entity.getTopic());
        response.setPartition(entity.getPartition());
        response.setConsumerGroup(entity.getConsumerGroup());
        response.setStatus(entity.getStatus());
        response.setCreatedAt(entity.getCreatedAt());

        return response;
    }
}