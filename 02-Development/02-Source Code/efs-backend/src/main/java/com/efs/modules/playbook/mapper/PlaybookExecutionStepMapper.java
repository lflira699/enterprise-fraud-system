package com.efs.modules.playbook.mapper;

import com.efs.modules.playbook.dto.PlaybookExecutionStepRequest;
import com.efs.modules.playbook.dto.PlaybookExecutionStepResponse;
import com.efs.modules.playbook.entity.PlaybookExecutionStep;
import org.springframework.stereotype.Component;

@Component
public class PlaybookExecutionStepMapper {

    public PlaybookExecutionStep toEntity(
            PlaybookExecutionStepRequest request
    ) {
        PlaybookExecutionStep entity = new PlaybookExecutionStep();

        entity.setPlaybookExecutionId(request.getPlaybookExecutionId());
        entity.setPlaybookStepId(request.getPlaybookStepId());
        entity.setStatus(request.getStatus());
        entity.setResult(request.getResult());
        entity.setStartedAt(request.getStartedAt());
        entity.setCompletedAt(request.getCompletedAt());

        return entity;
    }

    public void updateEntity(
            PlaybookExecutionStep entity,
            PlaybookExecutionStepRequest request
    ) {
        entity.setPlaybookExecutionId(request.getPlaybookExecutionId());
        entity.setPlaybookStepId(request.getPlaybookStepId());
        entity.setStatus(request.getStatus());
        entity.setResult(request.getResult());
        entity.setStartedAt(request.getStartedAt());
        entity.setCompletedAt(request.getCompletedAt());
    }

    public PlaybookExecutionStepResponse toResponse(
            PlaybookExecutionStep entity
    ) {
        PlaybookExecutionStepResponse response =
                new PlaybookExecutionStepResponse();

        response.setPlaybookExecutionStepId(
                entity.getPlaybookExecutionStepId()
        );
        response.setPlaybookExecutionId(
                entity.getPlaybookExecutionId()
        );
        response.setPlaybookStepId(entity.getPlaybookStepId());
        response.setStatus(entity.getStatus());
        response.setResult(entity.getResult());
        response.setStartedAt(entity.getStartedAt());
        response.setCompletedAt(entity.getCompletedAt());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());

        return response;
    }
}