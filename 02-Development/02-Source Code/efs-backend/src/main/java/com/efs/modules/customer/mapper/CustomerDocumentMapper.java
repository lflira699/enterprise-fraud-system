package com.efs.modules.customer.mapper;

import com.efs.modules.customer.dto.CustomerDocumentRequest;
import com.efs.modules.customer.dto.CustomerDocumentResponse;
import com.efs.modules.customer.entity.CustomerDocument;
import org.springframework.stereotype.Component;

@Component
public class CustomerDocumentMapper {

    public CustomerDocument toEntity(CustomerDocumentRequest request) {
        CustomerDocument document = new CustomerDocument();

        document.setDocumentType(request.getDocumentType());
        document.setDocumentNumber(request.getDocumentNumber());
        document.setIssuingCountry(request.getIssuingCountry());
        document.setIssueDate(request.getIssueDate());
        document.setExpirationDate(request.getExpirationDate());
        document.setVerificationStatus(request.getVerificationStatus());
        document.setVerifiedBy(request.getVerifiedBy());

        return document;
    }

    public void updateEntity(
            CustomerDocumentRequest request,
            CustomerDocument document) {

        document.setDocumentType(request.getDocumentType());
        document.setDocumentNumber(request.getDocumentNumber());
        document.setIssuingCountry(request.getIssuingCountry());
        document.setIssueDate(request.getIssueDate());
        document.setExpirationDate(request.getExpirationDate());
        document.setVerificationStatus(request.getVerificationStatus());
        document.setVerifiedBy(request.getVerifiedBy());
    }

    public CustomerDocumentResponse toResponse(CustomerDocument document) {
        CustomerDocumentResponse response = new CustomerDocumentResponse();

        response.setDocumentId(document.getDocumentId());
        response.setCustomerId(document.getCustomerId());
        response.setDocumentType(document.getDocumentType());
        response.setDocumentNumber(document.getDocumentNumber());
        response.setIssuingCountry(document.getIssuingCountry());
        response.setIssueDate(document.getIssueDate());
        response.setExpirationDate(document.getExpirationDate());
        response.setVerificationStatus(document.getVerificationStatus());
        response.setVerifiedAt(document.getVerifiedAt());
        response.setVerifiedBy(document.getVerifiedBy());
        response.setCreatedAt(document.getCreatedAt());
        response.setUpdatedAt(document.getUpdatedAt());

        return response;
    }
}