package com.efs.modules.casemanagement.mapper;

import com.efs.modules.casemanagement.dto.CaseSlaRequest;
import com.efs.modules.casemanagement.dto.CaseSlaResponse;
import com.efs.modules.casemanagement.entity.CaseSla;
import org.springframework.stereotype.Component;

@Component
public class CaseSlaMapper {

    public CaseSla toEntity(
            CaseSlaRequest request) {

        CaseSla sla =
                new CaseSla();

        sla.setSlaType(
                request.getSlaType()
        );

        sla.setTargetMinutes(
                request.getTargetMinutes()
        );

        if (request.getElapsedMinutes() != null) {
            sla.setElapsedMinutes(
                    request.getElapsedMinutes()
            );
        }

        sla.setDeadline(
                request.getDeadline()
        );

        if (request.getBreached() != null) {
            sla.setBreached(
                    request.getBreached()
            );
        }

        sla.setBreachReason(
                request.getBreachReason()
        );

        return sla;
    }

    public CaseSlaResponse toResponse(
            CaseSla sla) {

        CaseSlaResponse response =
                new CaseSlaResponse();

        response.setSlaId(
                sla.getSlaId()
        );

        response.setCaseId(
                sla.getCaseId()
        );

        response.setSlaType(
                sla.getSlaType()
        );

        response.setTargetMinutes(
                sla.getTargetMinutes()
        );

        response.setElapsedMinutes(
                sla.getElapsedMinutes()
        );

        response.setDeadline(
                sla.getDeadline()
        );

        response.setBreached(
                sla.getBreached()
        );

        response.setBreachReason(
                sla.getBreachReason()
        );

        response.setCalculatedAt(
                sla.getCalculatedAt()
        );

        return response;
    }
}