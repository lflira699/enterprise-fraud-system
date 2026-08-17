package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerRelationshipRequest;
import com.efs.modules.customer.dto.CustomerRelationshipResponse;

import java.util.List;
import java.util.UUID;

public interface CustomerRelationshipServiceInterface {

    CustomerRelationshipResponse createRelationship(
            UUID customerId,
            CustomerRelationshipRequest request
    );

    CustomerRelationshipResponse getRelationshipById(
            UUID customerRelationshipId
    );

    List<CustomerRelationshipResponse> getRelationshipsByCustomerId(
            UUID customerId
    );

    CustomerRelationshipResponse updateRelationship(
            UUID customerRelationshipId,
            CustomerRelationshipRequest request
    );

    void deleteRelationship(
            UUID customerRelationshipId,
            UUID deletedBy
    );
}