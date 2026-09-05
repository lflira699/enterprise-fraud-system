package com.efs.modules.playbook.service;

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
class PlaybookVersionServiceIntegrationTest {

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
    void shouldCreatePlaybookVersion() {
        PlaybookResponse playbook =
                createPlaybook("PB-VERSION-001");

        PlaybookVersionRequest request =
                new PlaybookVersionRequest();

        request.setPlaybookId(playbook.getPlaybookId());
        request.setVersionNumber(1);
        request.setStatus("TEST");
        request.setEffectiveFrom(
                LocalDateTime.of(2026, 8, 23, 10, 0)
        );
        request.setEffectiveTo(
                LocalDateTime.of(2026, 8, 24, 10, 0)
        );

        PlaybookVersionResponse response =
                playbookVersionService.create(request);

        assertNotNull(response.getPlaybookVersionId());

        assertEquals(
                playbook.getPlaybookId(),
                response.getPlaybookId()
        );

        assertEquals(
                1,
                response.getVersionNumber()
        );

        assertEquals(
                "TEST",
                response.getStatus()
        );

        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
    }

    @Test
    void shouldRejectDuplicateVersionNumber() {
        PlaybookResponse playbook =
                createPlaybook("PB-VERSION-002");

        PlaybookVersionRequest request =
                new PlaybookVersionRequest();

        request.setPlaybookId(playbook.getPlaybookId());
        request.setVersionNumber(1);
        request.setStatus("TEST");

        playbookVersionService.create(request);

        assertThrows(
                IllegalArgumentException.class,
                () -> playbookVersionService.create(request)
        );
    }

    @Test
    void shouldRejectInvalidEffectivePeriod() {
        PlaybookResponse playbook =
                createPlaybook("PB-VERSION-003");

        PlaybookVersionRequest request =
                new PlaybookVersionRequest();

        request.setPlaybookId(playbook.getPlaybookId());
        request.setVersionNumber(1);
        request.setStatus("TEST");

        request.setEffectiveFrom(
                LocalDateTime.of(2026, 8, 24, 10, 0)
        );

        request.setEffectiveTo(
                LocalDateTime.of(2026, 8, 23, 10, 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> playbookVersionService.create(request)
        );
    }

    @Test
    void shouldRejectVersionNumberBelowOne() {
        PlaybookResponse playbook =
                createPlaybook("PB-VERSION-004");

        PlaybookVersionRequest request =
                new PlaybookVersionRequest();

        request.setPlaybookId(playbook.getPlaybookId());
        request.setVersionNumber(0);
        request.setStatus("TEST");

        assertThrows(
                IllegalArgumentException.class,
                () -> playbookVersionService.create(request)
        );
    }

    @Test
    void shouldRejectVersionNumberBelowOneDuringUpdate() {
        PlaybookResponse playbook =
                createPlaybook("PB-VERSION-005");

        PlaybookVersionRequest createRequest =
                new PlaybookVersionRequest();

        createRequest.setPlaybookId(
                playbook.getPlaybookId()
        );

        createRequest.setVersionNumber(1);
        createRequest.setStatus("TEST");

        PlaybookVersionResponse created =
                playbookVersionService.create(
                        createRequest
                );

        PlaybookVersionRequest updateRequest =
                new PlaybookVersionRequest();

        updateRequest.setPlaybookId(
                playbook.getPlaybookId()
        );

        updateRequest.setVersionNumber(0);
        updateRequest.setStatus("TEST");

        assertThrows(
                IllegalArgumentException.class,
                () -> playbookVersionService.update(
                        created.getPlaybookVersionId(),
                        updateRequest
                )
        );
    }

    @Test
    void shouldReturnVersionsOrderedDescending() {
        PlaybookResponse playbook =
                createPlaybook("PB-VERSION-006");

        PlaybookVersionRequest versionOne =
                new PlaybookVersionRequest();

        versionOne.setPlaybookId(
                playbook.getPlaybookId()
        );

        versionOne.setVersionNumber(1);
        versionOne.setStatus("TEST");

        PlaybookVersionRequest versionTwo =
                new PlaybookVersionRequest();

        versionTwo.setPlaybookId(
                playbook.getPlaybookId()
        );

        versionTwo.setVersionNumber(2);
        versionTwo.setStatus("TEST");

        playbookVersionService.create(versionOne);
        playbookVersionService.create(versionTwo);

        List<PlaybookVersionResponse> results =
                playbookVersionService.getByPlaybookId(
                        playbook.getPlaybookId()
                );

        assertEquals(2, results.size());

        assertEquals(
                2,
                results.get(0).getVersionNumber()
        );

        assertEquals(
                1,
                results.get(1).getVersionNumber()
        );
    }

    private PlaybookResponse createPlaybook(
            String code
    ) {
        PlaybookRequest request =
                new PlaybookRequest();

        request.setPlaybookCode(code);
        request.setPlaybookName(code);
        request.setStatus("TEST");

        return playbookService.create(request);
    }
}