package com.efs.modules.playbook.mapper;

import com.efs.modules.playbook.dto.PlaybookStepRequest;
import com.efs.modules.playbook.dto.PlaybookStepResponse;
import com.efs.modules.playbook.entity.PlaybookStep;
import org.springframework.stereotype.Component;

@Component
public class PlaybookStepMapper {

    public PlaybookStep toEntity(PlaybookStepRequest request) {
        PlaybookStep entity = new PlaybookStep();

        entity.setPlaybookVersionId(request.getPlaybookVersionId());
        entity.setStepOrder(request.getStepOrder());
        entity.setStepName(request.getStepName());
        entity.setDescription(request.getDescription());
        entity.setExpectedResult(request.getExpectedResult());
        entity.setExpectedDurationMinutes(request.getExpectedDurationMinutes());

        return entity;
    }

    public void updateEntity(
            PlaybookStep entity,
            PlaybookStepRequest request
    ) {
        entity.setPlaybookVersionId(request.getPlaybookVersionId());
        entity.setStepOrder(request.getStepOrder());
        entity.setStepName(request.getStepName());
        entity.setDescription(request.getDescription());
        entity.setExpectedResult(request.getExpectedResult());
        entity.setExpectedDurationMinutes(request.getExpectedDurationMinutes());
    }

    public PlaybookStepResponse toResponse(PlaybookStep entity) {
        PlaybookStepResponse response = new PlaybookStepResponse();

        response.setPlaybookStepId(entity.getPlaybookStepId());
        response.setPlaybookVersionId(entity.getPlaybookVersionId());
        response.setStepOrder(entity.getStepOrder());
        response.setStepName(entity.getStepName());
        response.setDescription(entity.getDescription());
        response.setExpectedResult(entity.getExpectedResult());
        response.setExpectedDurationMinutes(entity.getExpectedDurationMinutes());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());

        return response;
    }
}