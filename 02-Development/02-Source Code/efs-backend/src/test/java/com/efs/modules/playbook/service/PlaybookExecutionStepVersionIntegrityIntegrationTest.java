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
class PlaybookExecutionStepVersionIntegrityIntegrationTest {

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
    void shouldRejectExecutionStepFromDifferentPlaybookVersion() {

        VersionContext context =
                createTwoVersionContext(
                        "PB-XVER-C-"
                                + UUID.randomUUID()
                );

        PlaybookExecutionResponse execution =
                createExecution(
                        context.versionOne()
                );

        PlaybookStepResponse stepFromVersionTwo =
                createStep(
                        context.versionTwo(),
                        1,
                        "Version Two Step"
                );

        PlaybookExecutionStepRequest request =
                new PlaybookExecutionStepRequest();

        request.setPlaybookExecutionId(
                execution.getPlaybookExecutionId()
        );

        request.setPlaybookStepId(
                stepFromVersionTwo.getPlaybookStepId()
        );

        request.setStatus(
                "TEST"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> playbookExecutionStepService.create(
                        request
                )
        );
    }

    @Test
    void shouldRejectUpdateToStepFromDifferentPlaybookVersion() {

        VersionContext context =
                createTwoVersionContext(
                        "PB-XVER-U-"
                                + UUID.randomUUID()
                );

        PlaybookStepResponse stepFromVersionOne =
                createStep(
                        context.versionOne(),
                        1,
                        "Version One Step"
                );

        PlaybookStepResponse stepFromVersionTwo =
                createStep(
                        context.versionTwo(),
                        1,
                        "Version Two Step"
                );

        PlaybookExecutionResponse execution =
                createExecution(
                        context.versionOne()
                );

        PlaybookExecutionStepRequest createRequest =
                new PlaybookExecutionStepRequest();

        createRequest.setPlaybookExecutionId(
                execution.getPlaybookExecutionId()
        );

        createRequest.setPlaybookStepId(
                stepFromVersionOne.getPlaybookStepId()
        );

        createRequest.setStatus(
                "TEST"
        );

        PlaybookExecutionStepResponse created =
                playbookExecutionStepService.create(
                        createRequest
                );

        PlaybookExecutionStepRequest updateRequest =
                new PlaybookExecutionStepRequest();

        updateRequest.setPlaybookExecutionId(
                execution.getPlaybookExecutionId()
        );

        updateRequest.setPlaybookStepId(
                stepFromVersionTwo.getPlaybookStepId()
        );

        updateRequest.setStatus(
                "TEST"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> playbookExecutionStepService.update(
                        created.getPlaybookExecutionStepId(),
                        updateRequest
                )
        );
    }

    private VersionContext createTwoVersionContext(
            String code) {

        PlaybookRequest playbookRequest =
                new PlaybookRequest();

        playbookRequest.setPlaybookCode(
                code
        );

        playbookRequest.setPlaybookName(
                code
        );

        playbookRequest.setStatus(
                "TEST"
        );

        PlaybookResponse playbook =
                playbookService.create(
                        playbookRequest
                );

        PlaybookVersionResponse versionOne =
                createVersion(
                        playbook,
                        1
                );

        PlaybookVersionResponse versionTwo =
                createVersion(
                        playbook,
                        2
                );

        return new VersionContext(
                versionOne,
                versionTwo
        );
    }

    private PlaybookVersionResponse createVersion(
            PlaybookResponse playbook,
            int versionNumber) {

        PlaybookVersionRequest request =
                new PlaybookVersionRequest();

        request.setPlaybookId(
                playbook.getPlaybookId()
        );

        request.setVersionNumber(
                versionNumber
        );

        request.setStatus(
                "TEST"
        );

        return playbookVersionService.create(
                request
        );
    }

    private PlaybookStepResponse createStep(
            PlaybookVersionResponse version,
            int stepOrder,
            String stepName) {

        PlaybookStepRequest request =
                new PlaybookStepRequest();

        request.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );

        request.setStepOrder(
                stepOrder
        );

        request.setStepName(
                stepName
        );

        request.setExpectedDurationMinutes(
                10
        );

        return playbookStepService.create(
                request
        );
    }

    private PlaybookExecutionResponse createExecution(
            PlaybookVersionResponse version) {

        PlaybookExecutionRequest request =
                new PlaybookExecutionRequest();

        request.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );

        request.setStatus(
                "TEST"
        );

        return playbookExecutionService.create(
                request
        );
    }

    private record VersionContext(
            PlaybookVersionResponse versionOne,
            PlaybookVersionResponse versionTwo) {
    }
}
