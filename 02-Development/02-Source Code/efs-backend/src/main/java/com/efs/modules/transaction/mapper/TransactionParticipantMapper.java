package com.efs.modules.transaction.mapper;

import com.efs.modules.transaction.dto.TransactionParticipantRequest;
import com.efs.modules.transaction.dto.TransactionParticipantResponse;
import com.efs.modules.transaction.entity.TransactionParticipant;
import org.springframework.stereotype.Component;

@Component
public class TransactionParticipantMapper {

    public TransactionParticipant toEntity(
            TransactionParticipantRequest request) {

        TransactionParticipant participant =
                new TransactionParticipant();

        participant.setParticipantType(
                request.getParticipantType()
        );

        participant.setCustomerId(
                request.getCustomerId()
        );

        participant.setExternalIdentifier(
                request.getExternalIdentifier()
        );

        participant.setInstitutionId(
                request.getInstitutionId()
        );

        participant.setCountryCode(
                request.getCountryCode()
        );

        participant.setRiskLevel(
                request.getRiskLevel()
        );

        return participant;
    }

    public TransactionParticipantResponse toResponse(
            TransactionParticipant participant) {

        TransactionParticipantResponse response =
                new TransactionParticipantResponse();

        response.setParticipantId(
                participant.getParticipantId()
        );

        response.setTransactionId(
                participant.getTransactionId()
        );

        response.setParticipantType(
                participant.getParticipantType()
        );

        response.setCustomerId(
                participant.getCustomerId()
        );

        response.setExternalIdentifier(
                participant.getExternalIdentifier()
        );

        response.setInstitutionId(
                participant.getInstitutionId()
        );

        response.setCountryCode(
                participant.getCountryCode()
        );

        response.setRiskLevel(
                participant.getRiskLevel()
        );

        response.setCreatedAt(
                participant.getCreatedAt()
        );

        return response;
    }
}