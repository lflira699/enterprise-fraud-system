package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerDocumentRequest;
import com.efs.modules.customer.dto.CustomerDocumentResponse;

import java.util.List;
import java.util.UUID;

public interface CustomerDocumentServiceInterface {

    CustomerDocumentResponse createDocument(
            UUID customerId,
            CustomerDocumentRequest request
    );

    CustomerDocumentResponse getDocumentById(UUID documentId);

    List<CustomerDocumentResponse> getDocumentsByCustomerId(UUID customerId);

    CustomerDocumentResponse updateDocument(
            UUID documentId,
            CustomerDocumentRequest request
    );

    void deleteDocument(UUID documentId);
}