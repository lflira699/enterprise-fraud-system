package com.efs.modules.playbook.service;

import com.efs.modules.playbook.dto.PlaybookExecutionRequest;
import com.efs.modules.playbook.dto.PlaybookExecutionResponse;
import com.efs.modules.playbook.dto.PlaybookRequest;
import com.efs.modules.playbook.dto.PlaybookResponse;
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
class PlaybookExecutionServiceIntegrationTest {

    @Autowired
    private PlaybookExecutionServiceInterface playbookExecutionService;

    @Autowired
    private PlaybookVersionServiceInterface playbookVersionService;

    @Autowired
    private PlaybookServiceInterface playbookService;

    @Autowired
    private PlaybookTestDataCleaner playbookTestDataCleaner;

    @AfterEach
    void cleanUp() {
        playbookTestDataCleaner.clean();
    }

    @Test
    void shouldCreatePlaybookExecution() {
        PlaybookVersionResponse version =
                createPlaybookVersion("PB-EXECUTION-001");

        PlaybookExecutionRequest request =
                new PlaybookExecutionRequest();

        request.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );
        request.setStatus("TEST");

        PlaybookExecutionResponse response =
                playbookExecutionService.create(request);

        assertNotNull(response.getPlaybookExecutionId());

        assertEquals(
                version.getPlaybookVersionId(),
                response.getPlaybookVersionId()
        );

        assertEquals(
                "TEST",
                response.getStatus()
        );

        assertNotNull(response.getStartedAt());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
    }

    @Test
    void shouldGetExecutionById() {
        PlaybookVersionResponse version =
                createPlaybookVersion("PB-EXECUTION-002");

        PlaybookExecutionRequest request =
                new PlaybookExecutionRequest();

        request.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );
        request.setStatus("LOOKUP_TEST");

        PlaybookExecutionResponse created =
                playbookExecutionService.create(request);

        PlaybookExecutionResponse response =
                playbookExecutionService.getById(
                        created.getPlaybookExecutionId()
                );

        assertEquals(
                created.getPlaybookExecutionId(),
                response.getPlaybookExecutionId()
        );

        assertEquals(
                "LOOKUP_TEST",
                response.getStatus()
        );
    }

    @Test
    void shouldRejectInvalidExecutionPeriod() {
        PlaybookVersionResponse version =
                createPlaybookVersion("PB-EXECUTION-003");

        PlaybookExecutionRequest request =
                new PlaybookExecutionRequest();

        request.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );

        request.setStatus("TEST");

        request.setStartedAt(
                LocalDateTime.of(2026, 8, 24, 10, 0)
        );

        request.setCompletedAt(
                LocalDateTime.of(2026, 8, 23, 10, 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> playbookExecutionService.create(request)
        );
    }

    @Test
    void shouldReturnExecutionsByPlaybookVersion() {
        PlaybookVersionResponse version =
                createPlaybookVersion("PB-EXECUTION-004");

        PlaybookExecutionRequest first =
                new PlaybookExecutionRequest();

        first.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );
        first.setStatus("TEST");

        first.setStartedAt(
                LocalDateTime.of(2026, 8, 23, 10, 0)
        );

        PlaybookExecutionRequest second =
                new PlaybookExecutionRequest();

        second.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );
        second.setStatus("TEST");

        second.setStartedAt(
                LocalDateTime.of(2026, 8, 23, 11, 0)
        );

        playbookExecutionService.create(first);
        playbookExecutionService.create(second);

        List<PlaybookExecutionResponse> results =
                playbookExecutionService
                        .getByPlaybookVersionId(
                                version.getPlaybookVersionId()
                        );

        assertEquals(2, results.size());

        assertEquals(
                LocalDateTime.of(2026, 8, 23, 11, 0),
                results.get(0).getStartedAt()
        );

        assertEquals(
                LocalDateTime.of(2026, 8, 23, 10, 0),
                results.get(1).getStartedAt()
        );
    }

    @Test
    void shouldPreserveStartedAtWhenOmittedDuringUpdate() {
        PlaybookVersionResponse version =
                createPlaybookVersion("PB-EXECUTION-005");

        LocalDateTime originalStartedAt =
                LocalDateTime.of(
                        2026,
                        9,
                        5,
                        12,
                        0
                );

        PlaybookExecutionRequest createRequest =
                new PlaybookExecutionRequest();

        createRequest.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );

        createRequest.setStatus(
                "TEST"
        );

        createRequest.setStartedAt(
                originalStartedAt
        );

        PlaybookExecutionResponse created =
                playbookExecutionService.create(
                        createRequest
                );

        PlaybookExecutionRequest updateRequest =
                new PlaybookExecutionRequest();

        updateRequest.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );

        updateRequest.setStatus(
                "COMPLETED"
        );

        PlaybookExecutionResponse updated =
                playbookExecutionService.update(
                        created.getPlaybookExecutionId(),
                        updateRequest
                );

        assertEquals(
                "COMPLETED",
                updated.getStatus()
        );

        assertEquals(
                originalStartedAt,
                updated.getStartedAt()
        );
    }

    @Test
    void shouldRejectCompletedAtBeforePersistedStartedAtWhenStartedAtOmittedDuringUpdate() {
        PlaybookVersionResponse version =
                createPlaybookVersion("PB-EXECUTION-006");

        LocalDateTime originalStartedAt =
                LocalDateTime.of(
                        2026,
                        9,
                        5,
                        12,
                        0
                );

        PlaybookExecutionRequest createRequest =
                new PlaybookExecutionRequest();

        createRequest.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );

        createRequest.setStatus(
                "TEST"
        );

        createRequest.setStartedAt(
                originalStartedAt
        );

        PlaybookExecutionResponse created =
                playbookExecutionService.create(
                        createRequest
                );

        PlaybookExecutionRequest updateRequest =
                new PlaybookExecutionRequest();

        updateRequest.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );

        updateRequest.setStatus(
                "COMPLETED"
        );

        updateRequest.setCompletedAt(
                LocalDateTime.of(
                        2026,
                        9,
                        5,
                        11,
                        0
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> playbookExecutionService.update(
                        created.getPlaybookExecutionId(),
                        updateRequest
                )
        );
    }

    private PlaybookVersionResponse createPlaybookVersion(
            String code
    ) {
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

        return playbookVersionService.create(
                versionRequest
        );
    }
}