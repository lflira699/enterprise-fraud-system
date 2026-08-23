package com.efs.modules.playbook.service;

import com.efs.modules.playbook.dto.PlaybookExecutionStepRequest;
import com.efs.modules.playbook.dto.PlaybookExecutionStepResponse;

import java.util.List;
import java.util.UUID;

public interface PlaybookExecutionStepServiceInterface {

    PlaybookExecutionStepResponse create(
            PlaybookExecutionStepRequest request
    );

    PlaybookExecutionStepResponse getById(
            UUID playbookExecutionStepId
    );

    List<PlaybookExecutionStepResponse> getByPlaybookExecutionId(
            UUID playbookExecutionId
    );

    PlaybookExecutionStepResponse update(
            UUID playbookExecutionStepId,
            PlaybookExecutionStepRequest request
    );
}