package com.efs.modules.playbook.service;

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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class PlaybookStepServiceIntegrationTest {

    @Autowired
    private PlaybookStepServiceInterface playbookStepService;

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
    void shouldCreatePlaybookStep() {
        PlaybookVersionResponse version =
                createPlaybookVersion("PB-STEP-001");

        PlaybookStepRequest request =
                new PlaybookStepRequest();

        request.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );
        request.setStepOrder(1);
        request.setStepName("Verify customer identity");
        request.setDescription(
                "Verify the available customer identity evidence"
        );
        request.setExpectedResult(
                "Customer identity verification completed"
        );
        request.setExpectedDurationMinutes(10);

        PlaybookStepResponse response =
                playbookStepService.create(request);

        assertNotNull(response.getPlaybookStepId());

        assertEquals(
                version.getPlaybookVersionId(),
                response.getPlaybookVersionId()
        );

        assertEquals(
                1,
                response.getStepOrder()
        );

        assertEquals(
                "Verify customer identity",
                response.getStepName()
        );

        assertEquals(
                10,
                response.getExpectedDurationMinutes()
        );

        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
    }

    @Test
    void shouldRejectInvalidStepOrder() {
        PlaybookVersionResponse version =
                createPlaybookVersion("PB-STEP-002");

        PlaybookStepRequest request =
                new PlaybookStepRequest();

        request.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );
        request.setStepOrder(0);
        request.setStepName("Invalid Step");

        assertThrows(
                IllegalArgumentException.class,
                () -> playbookStepService.create(request)
        );
    }

    @Test
    void shouldRejectNegativeExpectedDuration() {
        PlaybookVersionResponse version =
                createPlaybookVersion("PB-STEP-003");

        PlaybookStepRequest request =
                new PlaybookStepRequest();

        request.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );
        request.setStepOrder(1);
        request.setStepName("Invalid Duration");
        request.setExpectedDurationMinutes(-1);

        assertThrows(
                IllegalArgumentException.class,
                () -> playbookStepService.create(request)
        );
    }

    @Test
    void shouldRejectDuplicateStepOrder() {
        PlaybookVersionResponse version =
                createPlaybookVersion("PB-STEP-004");

        PlaybookStepRequest first =
                new PlaybookStepRequest();

        first.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );
        first.setStepOrder(1);
        first.setStepName("First Step");

        playbookStepService.create(first);

        PlaybookStepRequest duplicate =
                new PlaybookStepRequest();

        duplicate.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );
        duplicate.setStepOrder(1);
        duplicate.setStepName("Duplicate Step");

        assertThrows(
                IllegalArgumentException.class,
                () -> playbookStepService.create(duplicate)
        );
    }

    @Test
    void shouldRejectDuplicateStepOrderDuringUpdate() {
        PlaybookVersionResponse version =
                createPlaybookVersion("PB-STEP-005");

        PlaybookStepRequest firstRequest =
                new PlaybookStepRequest();

        firstRequest.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );
        firstRequest.setStepOrder(1);
        firstRequest.setStepName("First Step");

        playbookStepService.create(firstRequest);

        PlaybookStepRequest secondRequest =
                new PlaybookStepRequest();

        secondRequest.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );
        secondRequest.setStepOrder(2);
        secondRequest.setStepName("Second Step");

        PlaybookStepResponse second =
                playbookStepService.create(secondRequest);

        PlaybookStepRequest updateRequest =
                new PlaybookStepRequest();

        updateRequest.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );
        updateRequest.setStepOrder(1);
        updateRequest.setStepName("Conflicting Step");

        assertThrows(
                IllegalArgumentException.class,
                () -> playbookStepService.update(
                        second.getPlaybookStepId(),
                        updateRequest
                )
        );
    }

    @Test
    void shouldAllowSameStepOrderDuringUpdate() {
        PlaybookVersionResponse version =
                createPlaybookVersion("PB-STEP-006");

        PlaybookStepRequest createRequest =
                new PlaybookStepRequest();

        createRequest.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );
        createRequest.setStepOrder(1);
        createRequest.setStepName("Original Step");
        createRequest.setExpectedDurationMinutes(10);

        PlaybookStepResponse created =
                playbookStepService.create(createRequest);

        PlaybookStepRequest updateRequest =
                new PlaybookStepRequest();

        updateRequest.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );
        updateRequest.setStepOrder(1);
        updateRequest.setStepName("Updated Step");
        updateRequest.setExpectedDurationMinutes(15);

        PlaybookStepResponse updated =
                playbookStepService.update(
                        created.getPlaybookStepId(),
                        updateRequest
                );

        assertEquals(
                created.getPlaybookStepId(),
                updated.getPlaybookStepId()
        );

        assertEquals(
                1,
                updated.getStepOrder()
        );

        assertEquals(
                "Updated Step",
                updated.getStepName()
        );

        assertEquals(
                15,
                updated.getExpectedDurationMinutes()
        );
    }

    @Test
    void shouldReturnStepsOrderedAscending() {
        PlaybookVersionResponse version =
                createPlaybookVersion("PB-STEP-007");

        PlaybookStepRequest second =
                new PlaybookStepRequest();

        second.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );
        second.setStepOrder(2);
        second.setStepName("Second Step");

        PlaybookStepRequest first =
                new PlaybookStepRequest();

        first.setPlaybookVersionId(
                version.getPlaybookVersionId()
        );
        first.setStepOrder(1);
        first.setStepName("First Step");

        playbookStepService.create(second);
        playbookStepService.create(first);

        List<PlaybookStepResponse> results =
                playbookStepService.getByPlaybookVersionId(
                        version.getPlaybookVersionId()
                );

        assertEquals(2, results.size());

        assertEquals(
                1,
                results.get(0).getStepOrder()
        );

        assertEquals(
                2,
                results.get(1).getStepOrder()
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