package com.efs.modules.playbook.service;

import com.efs.modules.playbook.dto.PlaybookExecutionRequest;
import com.efs.modules.playbook.dto.PlaybookExecutionResponse;
import com.efs.modules.playbook.dto.PlaybookExecutionStepRequest;
import com.efs.modules.playbook.dto.PlaybookExecutionStepResponse;
import com.efs.modules.playbook.dto.PlaybookRequest;
import com.efs.modules.playbook.dto.PlaybookResponse;
import com.efs.modules.playbook.dto.PlaybookStepRequest;
import com.efs.modules.playbook.dto.PlaybookStepResponse;
import com.efs.modules.playbook.dto.PlaybookVersionRequest;
import com.efs.modules.playbook.dto.PlaybookVersionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class PlaybookExecutionStepIdentityIntegrityIntegrationTest {

    @Autowired
    private PlaybookServiceInterface playbookService;

    @Autowired
    private PlaybookVersionServiceInterface playbookVersionService;

    @Autowired
    private PlaybookStepServiceInterface playbookStepService;

    @Autowired
    private PlaybookExecutionServiceInterface playbookExecutionService;

    @Autowired
    private PlaybookExecutionStepServiceInterface
            playbookExecutionStepService;

    @Test
    void shouldRejectExecutionStepReassignmentToDifferentExecution() {

        IdentityContext context =
                createIdentityContext(
                        "PB-EXEC-STEP-ID-EXEC-"
                                + UUID.randomUUID()
                );

        PlaybookExecutionStepResponse created =
                createExecutionStep(
                        context.executionOne(),
                        context.stepOne()
                );

        PlaybookExecutionStepRequest updateRequest =
                new PlaybookExecutionStepRequest();

        updateRequest.setPlaybookExecutionId(
                context.executionTwo()
                        .getPlaybookExecutionId()
        );

        updateRequest.setPlaybookStepId(
                context.stepOne()
                        .getPlaybookStepId()
        );

        updateRequest.setStatus("TEST");

        assertThrows(
                IllegalArgumentException.class,
                () -> playbookExecutionStepService.update(
                        created.getPlaybookExecutionStepId(),
                        updateRequest
                )
        );
    }

    @Test
    void shouldRejectExecutionStepReassignmentToDifferentStep() {

        IdentityContext context =
                createIdentityContext(
                        "PB-EXEC-STEP-ID-STEP-"
                                + UUID.randomUUID()
                );

        PlaybookExecutionStepResponse created =
                createExecutionStep(
                        context.executionOne(),
                        context.stepOne()
                );

        PlaybookExecutionStepRequest updateRequest =
                new PlaybookExecutionStepRequest();

        updateRequest.setPlaybookExecutionId(
                context.executionOne()
                        .getPlaybookExecutionId()
        );

        updateRequest.setPlaybookStepId(
                context.stepTwo()
                        .getPlaybookStepId()
        );

        updateRequest.setStatus("TEST");

        assertThrows(
                IllegalArgumentException.class,
                () -> playbookExecutionStepService.update(
                        created.getPlaybookExecutionStepId(),
                        updateRequest
                )
        );
    }

    private IdentityContext createIdentityContext(
            String code
    ) {
        PlaybookRequest playbookRequest =
                new PlaybookRequest();

        playbookRequest.setPlaybookCode(code);
        playbookRequest.setPlaybookName(code);
        playbookRequest.setStatus("TEST");

        PlaybookResponse playbook =
                playbookService.create(
                        playbookRequest
                );

        PlaybookVersionRequest versionRequest =
                new PlaybookVersionRequest();

        versionRequest.setPlaybookId(
                playbook.getPlaybookId()
        );

        versionRequest.setVersionNumber(1);
        versionRequest.setStatus("TEST");

        PlaybookVersionResponse version =
                playbookVersionService.create(
                        versionRequest
                );

        PlaybookStepResponse stepOne =
                createStep(
                        version,
                        1,
                        "Identity Step One"
                );

        PlaybookStepResponse stepTwo =
                createStep(
                        version,
                        2,
                        "Identity Step Two"
                );

        PlaybookExecutionResponse executionOne =
                createExecution(
                        version
                );

        PlaybookExecutionResponse executionTwo =
                createExecution(
                        version
                );

        return new IdentityContext(
                stepOne,
                stepTwo,
                executionOne,
                executionTwo
        );
    }

    private PlaybookStepResponse createStep(
            PlaybookVersionResponse version,
            int stepOrder,
            String stepName
    ) {
        PlaybookStepRequest request =
                new PlaybookStepRequest();

        request.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );

        request.setStepOrder(stepOrder);
        request.setStepName(stepName);
        request.setExpectedDurationMinutes(10);

        return playbookStepService.create(
                request
        );
    }

    private PlaybookExecutionResponse createExecution(
            PlaybookVersionResponse version
    ) {
        PlaybookExecutionRequest request =
                new PlaybookExecutionRequest();

        request.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );

        request.setStatus("TEST");

        return playbookExecutionService.create(
                request
        );
    }

    private PlaybookExecutionStepResponse createExecutionStep(
            PlaybookExecutionResponse execution,
            PlaybookStepResponse step
    ) {
        PlaybookExecutionStepRequest request =
                new PlaybookExecutionStepRequest();

        request.setPlaybookExecutionId(
                execution.getPlaybookExecutionId()
        );

        request.setPlaybookStepId(
                step.getPlaybookStepId()
        );

        request.setStatus("TEST");

        return playbookExecutionStepService.create(
                request
        );
    }

    private record IdentityContext(
            PlaybookStepResponse stepOne,
            PlaybookStepResponse stepTwo,
            PlaybookExecutionResponse executionOne,
            PlaybookExecutionResponse executionTwo
    ) {
    }
}
