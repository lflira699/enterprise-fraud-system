package com.efs.modules.customer.mapper;

import com.efs.modules.customer.dto.CustomerRelationshipRequest;
import com.efs.modules.customer.dto.CustomerRelationshipResponse;
import com.efs.modules.customer.entity.CustomerRelationship;
import org.springframework.stereotype.Component;

@Component
public class CustomerRelationshipMapper {

    public CustomerRelationship toEntity(
            CustomerRelationshipRequest request) {

        CustomerRelationship relationship =
                new CustomerRelationship();

        relationship.setRelatedCustomerId(request.getRelatedCustomerId());
        relationship.setRelationshipType(request.getRelationshipType());
        relationship.setRelationshipStatus(request.getRelationshipStatus());
        relationship.setRelationshipDescription(
                request.getRelationshipDescription()
        );
        relationship.setEffectiveDate(request.getEffectiveDate());
        relationship.setExpirationDate(request.getExpirationDate());
        relationship.setCreatedBy(request.getCreatedBy());
        relationship.setUpdatedBy(request.getUpdatedBy());

        return relationship;
    }

    public void updateEntity(
            CustomerRelationshipRequest request,
            CustomerRelationship relationship) {

        relationship.setRelatedCustomerId(request.getRelatedCustomerId());
        relationship.setRelationshipType(request.getRelationshipType());
        relationship.setRelationshipStatus(request.getRelationshipStatus());
        relationship.setRelationshipDescription(
                request.getRelationshipDescription()
        );
        relationship.setEffectiveDate(request.getEffectiveDate());
        relationship.setExpirationDate(request.getExpirationDate());
        relationship.setUpdatedBy(request.getUpdatedBy());
    }

    public CustomerRelationshipResponse toResponse(
            CustomerRelationship relationship) {

        CustomerRelationshipResponse response =
                new CustomerRelationshipResponse();

        response.setCustomerRelationshipId(
                relationship.getCustomerRelationshipId()
        );
        response.setCustomerId(relationship.getCustomerId());
        response.setRelatedCustomerId(
                relationship.getRelatedCustomerId()
        );
        response.setRelationshipType(
                relationship.getRelationshipType()
        );
        response.setRelationshipStatus(
                relationship.getRelationshipStatus()
        );
        response.setRelationshipDescription(
                relationship.getRelationshipDescription()
        );
        response.setEffectiveDate(relationship.getEffectiveDate());
        response.setExpirationDate(relationship.getExpirationDate());
        response.setCreatedAt(relationship.getCreatedAt());
        response.setCreatedBy(relationship.getCreatedBy());
        response.setUpdatedAt(relationship.getUpdatedAt());
        response.setUpdatedBy(relationship.getUpdatedBy());
        response.setDeletedAt(relationship.getDeletedAt());
        response.setDeletedBy(relationship.getDeletedBy());
        response.setRecordVersion(relationship.getRecordVersion());

        return response;
    }
}