package com.efs.modules.casemanagement.mapper;

import com.efs.modules.casemanagement.dto.CaseEvidenceRequest;
import com.efs.modules.casemanagement.dto.CaseEvidenceResponse;
import com.efs.modules.casemanagement.entity.CaseEvidence;
import org.springframework.stereotype.Component;

@Component
public class CaseEvidenceMapper {

    public CaseEvidence toEntity(
            CaseEvidenceRequest request) {

        CaseEvidence evidence =
                new CaseEvidence();

        evidence.setTransactionId(
                request.getTransactionId()
        );

        evidence.setEvidenceType(
                request.getEvidenceType()
        );

        evidence.setSourceSystem(
                request.getSourceSystem()
        );

        evidence.setStorageUri(
                request.getStorageUri()
        );

        evidence.setChecksumSha256(
                request.getChecksumSha256()
        );

        evidence.setUploadedBy(
                request.getUploadedBy()
        );

        return evidence;
    }

    public CaseEvidenceResponse toResponse(
            CaseEvidence evidence) {

        CaseEvidenceResponse response =
                new CaseEvidenceResponse();

        response.setEvidenceId(
                evidence.getEvidenceId()
        );

        response.setCaseId(
                evidence.getCaseId()
        );

        response.setTransactionId(
                evidence.getTransactionId()
        );

        response.setEvidenceType(
                evidence.getEvidenceType()
        );

        response.setSourceSystem(
                evidence.getSourceSystem()
        );

        response.setStorageUri(
                evidence.getStorageUri()
        );

        response.setChecksumSha256(
                evidence.getChecksumSha256()
        );

        response.setUploadedBy(
                evidence.getUploadedBy()
        );

        response.setUploadedAt(
                evidence.getUploadedAt()
        );

        return response;
    }
}