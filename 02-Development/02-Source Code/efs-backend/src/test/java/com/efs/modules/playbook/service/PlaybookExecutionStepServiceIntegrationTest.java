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
import com.efs.modules.playbook.support.PlaybookTestDataCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class PlaybookExecutionStepServiceIntegrationTest {

    @Autowired
    private PlaybookExecutionStepServiceInterface
            playbookExecutionStepService;

    @Autowired
    private PlaybookExecutionServiceInterface
            playbookExecutionService;

    @Autowired
    private PlaybookStepServiceInterface
            playbookStepService;

    @Autowired
    private PlaybookVersionServiceInterface
            playbookVersionService;

    @Autowired
    private PlaybookServiceInterface
            playbookService;

    @Autowired
    private PlaybookTestDataCleaner playbookTestDataCleaner;

    @AfterEach
    void cleanUp() {
        playbookTestDataCleaner.clean();
    }

    @Test
    void shouldCreatePlaybookExecutionStep() {
        TestContext context =
                createContext("PB-EXEC-STEP-001");

        PlaybookExecutionStepRequest request =
                new PlaybookExecutionStepRequest();

        request.setPlaybookExecutionId(
                context.execution().getPlaybookExecutionId()
        );

        request.setPlaybookStepId(
                context.step().getPlaybookStepId()
        );

        request.setStatus("TEST");
        request.setResult("Verification completed");

        request.setStartedAt(
                LocalDateTime.of(2026, 8, 23, 10, 0)
        );

        request.setCompletedAt(
                LocalDateTime.of(2026, 8, 23, 10, 10)
        );

        PlaybookExecutionStepResponse response =
                playbookExecutionStepService.create(request);

        assertNotNull(
                response.getPlaybookExecutionStepId()
        );

        assertEquals(
                context.execution().getPlaybookExecutionId(),
                response.getPlaybookExecutionId()
        );

        assertEquals(
                context.step().getPlaybookStepId(),
                response.getPlaybookStepId()
        );

        assertEquals(
                "TEST",
                response.getStatus()
        );

        assertEquals(
                "Verification completed",
                response.getResult()
        );

        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
    }

    @Test
    void shouldRejectDuplicateExecutionStep() {
        TestContext context =
                createContext("PB-EXEC-STEP-002");

        PlaybookExecutionStepRequest request =
                new PlaybookExecutionStepRequest();

        request.setPlaybookExecutionId(
                context.execution().getPlaybookExecutionId()
        );

        request.setPlaybookStepId(
                context.step().getPlaybookStepId()
        );

        request.setStatus("TEST");

        playbookExecutionStepService.create(request);

        assertThrows(
                IllegalArgumentException.class,
                () -> playbookExecutionStepService.create(request)
        );
    }

    @Test
    void shouldRejectInvalidExecutionPeriod() {
        TestContext context =
                createContext("PB-EXEC-STEP-003");

        PlaybookExecutionStepRequest request =
                new PlaybookExecutionStepRequest();

        request.setPlaybookExecutionId(
                context.execution().getPlaybookExecutionId()
        );

        request.setPlaybookStepId(
                context.step().getPlaybookStepId()
        );

        request.setStatus("TEST");

        request.setStartedAt(
                LocalDateTime.of(2026, 8, 23, 11, 0)
        );

        request.setCompletedAt(
                LocalDateTime.of(2026, 8, 23, 10, 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> playbookExecutionStepService.create(request)
        );
    }

    @Test
    void shouldReturnExecutionSteps() {
        TestContext context =
                createContext("PB-EXEC-STEP-004");

        PlaybookExecutionStepRequest request =
                new PlaybookExecutionStepRequest();

        request.setPlaybookExecutionId(
                context.execution().getPlaybookExecutionId()
        );

        request.setPlaybookStepId(
                context.step().getPlaybookStepId()
        );

        request.setStatus("TEST");

        playbookExecutionStepService.create(request);

        List<PlaybookExecutionStepResponse> results =
                playbookExecutionStepService
                        .getByPlaybookExecutionId(
                                context.execution()
                                        .getPlaybookExecutionId()
                        );

        assertEquals(1, results.size());

        assertEquals(
                context.step().getPlaybookStepId(),
                results.get(0).getPlaybookStepId()
        );
    }

    private TestContext createContext(String code) {
        PlaybookRequest playbookRequest =
                new PlaybookRequest();

        playbookRequest.setPlaybookCode(code);
        playbookRequest.setPlaybookName(code);
        playbookRequest.setStatus("TEST");

        PlaybookResponse playbook =
                playbookService.create(playbookRequest);

        PlaybookVersionRequest versionRequest =
                new PlaybookVersionRequest();

        versionRequest.setPlaybookId(
                playbook.getPlaybookId()
        );

        versionRequest.setVersionNumber(1);
        versionRequest.setStatus("TEST");

        PlaybookVersionResponse version =
                playbookVersionService.create(versionRequest);

        PlaybookStepRequest stepRequest =
                new PlaybookStepRequest();

        stepRequest.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );

        stepRequest.setStepOrder(1);
        stepRequest.setStepName("Investigation Step");
        stepRequest.setExpectedDurationMinutes(10);

        PlaybookStepResponse step =
                playbookStepService.create(stepRequest);

        PlaybookExecutionRequest executionRequest =
                new PlaybookExecutionRequest();

        executionRequest.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );

        executionRequest.setStatus("TEST");

        PlaybookExecutionResponse execution =
                playbookExecutionService.create(
                        executionRequest
                );

        return new TestContext(step, execution);
    }

    private record TestContext(
            PlaybookStepResponse step,
            PlaybookExecutionResponse execution
    ) {
    }
}