package com.efs.modules.playbook.service;

import com.efs.modules.playbook.dto.PlaybookRequest;
import com.efs.modules.playbook.dto.PlaybookResponse;
import com.efs.modules.playbook.repository.PlaybookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class PlaybookServiceIntegrationTest {

    @Autowired
    private PlaybookServiceInterface playbookService;

    @Autowired
    private PlaybookRepository playbookRepository;

    @AfterEach
    void cleanUp() {
        playbookRepository.deleteAll();
    }

    @Test
    void shouldCreatePlaybook() {
        PlaybookRequest request = new PlaybookRequest();
        request.setPlaybookCode("PB-TEST-001");
        request.setPlaybookName("Test Playbook");
        request.setDescription("Integration test playbook");
        request.setStatus("TEST");

        PlaybookResponse response =
                playbookService.create(request);

        assertNotNull(response.getPlaybookId());
        assertEquals(
                "PB-TEST-001",
                response.getPlaybookCode()
        );
        assertEquals(
                "Test Playbook",
                response.getPlaybookName()
        );
        assertEquals(
                "Integration test playbook",
                response.getDescription()
        );
        assertEquals(
                "TEST",
                response.getStatus()
        );
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
    }

    @Test
    void shouldGetPlaybookByCode() {
        PlaybookRequest request = new PlaybookRequest();
        request.setPlaybookCode("PB-TEST-002");
        request.setPlaybookName("Lookup Playbook");
        request.setStatus("TEST");

        playbookService.create(request);

        PlaybookResponse response =
                playbookService.getByCode("PB-TEST-002");

        assertEquals(
                "PB-TEST-002",
                response.getPlaybookCode()
        );
        assertEquals(
                "Lookup Playbook",
                response.getPlaybookName()
        );
    }

    @Test
    void shouldRejectDuplicatePlaybookCode() {
        PlaybookRequest request = new PlaybookRequest();
        request.setPlaybookCode("PB-TEST-003");
        request.setPlaybookName("Duplicate Test");
        request.setStatus("TEST");

        playbookService.create(request);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> playbookService.create(request)
                );

        assertEquals(
                "Playbook code already exists: PB-TEST-003",
                exception.getMessage()
        );
    }

    @Test
    void shouldFilterPlaybooksByStatus() {
        PlaybookRequest first = new PlaybookRequest();
        first.setPlaybookCode("PB-TEST-004");
        first.setPlaybookName("Alpha Playbook");
        first.setStatus("FILTER_TEST");

        PlaybookRequest second = new PlaybookRequest();
        second.setPlaybookCode("PB-TEST-005");
        second.setPlaybookName("Beta Playbook");
        second.setStatus("OTHER_TEST");

        playbookService.create(first);
        playbookService.create(second);

        List<PlaybookResponse> results =
                playbookService.getByStatus("FILTER_TEST");

        assertEquals(1, results.size());
        assertEquals(
                "PB-TEST-004",
                results.get(0).getPlaybookCode()
        );
    }
}