package com.efs.modules.transaction.mapper;

import com.efs.modules.transaction.dto.TransactionMetadataRequest;
import com.efs.modules.transaction.dto.TransactionMetadataResponse;
import com.efs.modules.transaction.entity.TransactionMetadata;
import org.springframework.stereotype.Component;

@Component
public class TransactionMetadataMapper {

    public TransactionMetadata toEntity(
            TransactionMetadataRequest request) {

        TransactionMetadata metadata =
                new TransactionMetadata();

        metadata.setMetadataType(
                request.getMetadataType()
        );

        metadata.setMetadataJson(
                request.getMetadataJson()
        );

        return metadata;
    }

    public TransactionMetadataResponse toResponse(
            TransactionMetadata metadata) {

        TransactionMetadataResponse response =
                new TransactionMetadataResponse();

        response.setMetadataId(
                metadata.getMetadataId()
        );

        response.setTransactionId(
                metadata.getTransactionId()
        );

        response.setMetadataType(
                metadata.getMetadataType()
        );

        response.setMetadataJson(
                metadata.getMetadataJson()
        );

        response.setCreatedAt(
                metadata.getCreatedAt()
        );

        return response;
    }
}