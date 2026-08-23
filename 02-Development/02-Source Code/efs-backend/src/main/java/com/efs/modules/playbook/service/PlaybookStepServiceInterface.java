package com.efs.modules.playbook.service;

import com.efs.modules.playbook.dto.PlaybookStepRequest;
import com.efs.modules.playbook.dto.PlaybookStepResponse;

import java.util.List;
import java.util.UUID;

public interface PlaybookStepServiceInterface {

    PlaybookStepResponse create(PlaybookStepRequest request);

    PlaybookStepResponse getById(UUID playbookStepId);

    List<PlaybookStepResponse> getByPlaybookVersionId(
            UUID playbookVersionId
    );

    PlaybookStepResponse update(
            UUID playbookStepId,
            PlaybookStepRequest request
    );
}