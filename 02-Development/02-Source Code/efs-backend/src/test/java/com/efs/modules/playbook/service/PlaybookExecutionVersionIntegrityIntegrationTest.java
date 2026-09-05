package com.efs.modules.playbook.service;

import com.efs.modules.playbook.dto.PlaybookExecutionRequest;
import com.efs.modules.playbook.dto.PlaybookExecutionResponse;
import com.efs.modules.playbook.dto.PlaybookRequest;
import com.efs.modules.playbook.dto.PlaybookResponse;
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
class PlaybookExecutionVersionIntegrityIntegrationTest {

    @Autowired
    private PlaybookServiceInterface playbookService;

    @Autowired
    private PlaybookVersionServiceInterface playbookVersionService;

    @Autowired
    private PlaybookExecutionServiceInterface playbookExecutionService;

    @Test
    void shouldRejectPlaybookExecutionVersionReassignment() {

        String code =
                "PB-EXEC-XVER-"
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

        PlaybookExecutionRequest createRequest =
                new PlaybookExecutionRequest();

        createRequest.setPlaybookVersionId(
                versionOne.getPlaybookVersionId()
        );

        createRequest.setStatus("STARTED");

        PlaybookExecutionResponse execution =
                playbookExecutionService.create(
                        createRequest
                );

        PlaybookExecutionRequest updateRequest =
                new PlaybookExecutionRequest();

        updateRequest.setPlaybookVersionId(
                versionTwo.getPlaybookVersionId()
        );

        updateRequest.setStatus("COMPLETED");

        assertThrows(
                IllegalArgumentException.class,
                () -> playbookExecutionService.update(
                        execution.getPlaybookExecutionId(),
                        updateRequest
                )
        );
    }

    private PlaybookVersionResponse createVersion(
            PlaybookResponse playbook,
            int versionNumber
    ) {
        PlaybookVersionRequest request =
                new PlaybookVersionRequest();

        request.setPlaybookId(
                playbook.getPlaybookId()
        );

        request.setVersionNumber(
                versionNumber
        );

        request.setStatus("TEST");

        return playbookVersionService.create(
                request
        );
    }
}
