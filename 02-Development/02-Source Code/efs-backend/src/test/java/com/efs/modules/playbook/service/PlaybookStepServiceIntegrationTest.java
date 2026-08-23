package com.efs.modules.playbook.service;

import com.efs.modules.playbook.dto.PlaybookRequest;
import com.efs.modules.playbook.dto.PlaybookResponse;
import com.efs.modules.playbook.dto.PlaybookStepRequest;
import com.efs.modules.playbook.dto.PlaybookStepResponse;
import com.efs.modules.playbook.dto.PlaybookVersionRequest;
import com.efs.modules.playbook.dto.PlaybookVersionResponse;
import com.efs.modules.playbook.repository.PlaybookRepository;
import com.efs.modules.playbook.repository.PlaybookStepRepository;
import com.efs.modules.playbook.repository.PlaybookVersionRepository;
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
    private PlaybookStepRepository playbookStepRepository;

    @Autowired
    private PlaybookVersionRepository playbookVersionRepository;

    @Autowired
    private PlaybookRepository playbookRepository;

    @AfterEach
    void cleanUp() {
        playbookStepRepository.deleteAll();
        playbookVersionRepository.deleteAll();
        playbookRepository.deleteAll();
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
    void shouldReturnStepsOrderedAscending() {
        PlaybookVersionResponse version =
                createPlaybookVersion("PB-STEP-004");

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