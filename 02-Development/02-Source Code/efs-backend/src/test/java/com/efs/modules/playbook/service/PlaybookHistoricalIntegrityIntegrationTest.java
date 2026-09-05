package com.efs.modules.playbook.service;

import com.efs.modules.playbook.dto.PlaybookExecutionRequest;
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

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class PlaybookHistoricalIntegrityIntegrationTest {

    @Autowired
    private PlaybookServiceInterface playbookService;

    @Autowired
    private PlaybookVersionServiceInterface playbookVersionService;

    @Autowired
    private PlaybookStepServiceInterface playbookStepService;

    @Autowired
    private PlaybookExecutionServiceInterface playbookExecutionService;

    @Test
    void shouldRejectPlaybookVersionUpdateAfterExecution() {

        HistoricalContext context =
                createHistoricalContext();

        PlaybookVersionRequest updateRequest =
                new PlaybookVersionRequest();

        updateRequest.setPlaybookId(
                context.playbook().getPlaybookId()
        );

        updateRequest.setVersionNumber(1);

        updateRequest.setStatus("TEST");

        updateRequest.setEffectiveFrom(
                LocalDateTime.of(
                        2026,
                        1,
                        1,
                        0,
                        0
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> playbookVersionService.update(
                        context.version().getPlaybookVersionId(),
                        updateRequest
                )
        );
    }

    @Test
    void shouldRejectPlaybookStepCreationAfterExecution() {

        HistoricalContext context =
                createHistoricalContext();

        PlaybookStepRequest request =
                new PlaybookStepRequest();

        request.setPlaybookVersionId(
                context.version().getPlaybookVersionId()
        );

        request.setStepOrder(2);

        request.setStepName(
                "Late Added Step"
        );

        request.setExpectedDurationMinutes(10);

        assertThrows(
                IllegalArgumentException.class,
                () -> playbookStepService.create(
                        request
                )
        );
    }

    @Test
    void shouldRejectPlaybookStepUpdateAfterExecution() {

        HistoricalContext context =
                createHistoricalContext();

        PlaybookStepRequest updateRequest =
                new PlaybookStepRequest();

        updateRequest.setPlaybookVersionId(
                context.version().getPlaybookVersionId()
        );

        updateRequest.setStepOrder(1);

        updateRequest.setStepName(
                "Modified Historical Step"
        );

        updateRequest.setExpectedDurationMinutes(20);

        assertThrows(
                IllegalArgumentException.class,
                () -> playbookStepService.update(
                        context.step().getPlaybookStepId(),
                        updateRequest
                )
        );
    }

    @Test
    void shouldRejectPlaybookStepMoveToExecutedVersion() {

        String code =
                "PB-HIST-MOVE-"
                        + UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        PlaybookRequest playbookRequest =
                new PlaybookRequest();

        playbookRequest.setPlaybookCode(code);
        playbookRequest.setPlaybookName(code);
        playbookRequest.setStatus("TEST");

        PlaybookResponse playbook =
                playbookService.create(
                        playbookRequest
                );

        PlaybookVersionRequest versionOneRequest =
                new PlaybookVersionRequest();

        versionOneRequest.setPlaybookId(
                playbook.getPlaybookId()
        );

        versionOneRequest.setVersionNumber(1);
        versionOneRequest.setStatus("TEST");

        PlaybookVersionResponse versionOne =
                playbookVersionService.create(
                        versionOneRequest
                );

        PlaybookVersionRequest versionTwoRequest =
                new PlaybookVersionRequest();

        versionTwoRequest.setPlaybookId(
                playbook.getPlaybookId()
        );

        versionTwoRequest.setVersionNumber(2);
        versionTwoRequest.setStatus("TEST");

        PlaybookVersionResponse versionTwo =
                playbookVersionService.create(
                        versionTwoRequest
                );

        PlaybookStepRequest stepRequest =
                new PlaybookStepRequest();

        stepRequest.setPlaybookVersionId(
                versionOne.getPlaybookVersionId()
        );

        stepRequest.setStepOrder(1);
        stepRequest.setStepName("Version One Step");
        stepRequest.setExpectedDurationMinutes(10);

        PlaybookStepResponse step =
                playbookStepService.create(
                        stepRequest
                );

        PlaybookExecutionRequest executionRequest =
                new PlaybookExecutionRequest();

        executionRequest.setPlaybookVersionId(
                versionTwo.getPlaybookVersionId()
        );

        executionRequest.setStatus("TEST");

        playbookExecutionService.create(
                executionRequest
        );

        PlaybookStepRequest updateRequest =
                new PlaybookStepRequest();

        updateRequest.setPlaybookVersionId(
                versionTwo.getPlaybookVersionId()
        );

        updateRequest.setStepOrder(1);
        updateRequest.setStepName("Moved Historical Step");
        updateRequest.setExpectedDurationMinutes(10);

        assertThrows(
                IllegalArgumentException.class,
                () -> playbookStepService.update(
                        step.getPlaybookStepId(),
                        updateRequest
                )
        );
    }

    private HistoricalContext createHistoricalContext() {

        String code =
                "PB-HIST-"
                        + UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

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

        PlaybookStepRequest stepRequest =
                new PlaybookStepRequest();

        stepRequest.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );

        stepRequest.setStepOrder(1);

        stepRequest.setStepName(
                "Original Historical Step"
        );

        stepRequest.setExpectedDurationMinutes(10);

        PlaybookStepResponse step =
                playbookStepService.create(
                        stepRequest
                );

        PlaybookExecutionRequest executionRequest =
                new PlaybookExecutionRequest();

        executionRequest.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );

        executionRequest.setStatus("TEST");

        playbookExecutionService.create(
                executionRequest
        );

        return new HistoricalContext(
                playbook,
                version,
                step
        );
    }

    private record HistoricalContext(
            PlaybookResponse playbook,
            PlaybookVersionResponse version,
            PlaybookStepResponse step) {
    }
}
