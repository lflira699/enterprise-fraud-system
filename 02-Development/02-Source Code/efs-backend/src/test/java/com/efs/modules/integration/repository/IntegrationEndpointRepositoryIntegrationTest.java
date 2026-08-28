package com.efs.modules.integration.repository;

import com.efs.modules.integration.entity.IntegrationEndpoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class IntegrationEndpointRepositoryIntegrationTest {

    @Autowired
    private IntegrationEndpointRepository repository;

    @Test
    void shouldSaveIntegrationEndpoint() {

        IntegrationEndpoint endpoint = createEndpoint(
                "EFS-ENDPOINT-001",
                "Primary Fraud Endpoint",
                "https://integration.example.test/fraud",
                "HTTPS",
                "API_KEY",
                30,
                "ACTIVE"
        );

        IntegrationEndpoint saved =
                repository.saveAndFlush(endpoint);

        assertNotNull(saved.getEndpointId());

        assertEquals(
                "EFS-ENDPOINT-001",
                saved.getEndpointCode()
        );

        assertEquals(
                "Primary Fraud Endpoint",
                saved.getEndpointName()
        );

        assertEquals(
                "https://integration.example.test/fraud",
                saved.getEndpointUrl()
        );

        assertEquals(
                "HTTPS",
                saved.getProtocol()
        );

        assertEquals(
                "API_KEY",
                saved.getAuthenticationType()
        );

        assertEquals(
                30,
                saved.getTimeoutSeconds()
        );

        assertEquals(
                "ACTIVE",
                saved.getStatus()
        );
    }

    @Test
    void shouldFindIntegrationEndpointById() {

        IntegrationEndpoint saved =
                repository.saveAndFlush(
                        createEndpoint(
                                "EFS-ENDPOINT-002",
                                "Secondary Fraud Endpoint",
                                "https://integration.example.test/secondary",
                                "HTTPS",
                                "API_KEY",
                                45,
                                "ACTIVE"
                        )
                );

        Optional<IntegrationEndpoint> result =
                repository.findById(
                        saved.getEndpointId()
                );

        assertTrue(result.isPresent());

        assertEquals(
                saved.getEndpointId(),
                result.get().getEndpointId()
        );

        assertEquals(
                "EFS-ENDPOINT-002",
                result.get().getEndpointCode()
        );
    }

    @Test
    void shouldFindIntegrationEndpointByEndpointCode() {

        repository.saveAndFlush(
                createEndpoint(
                        "EFS-ENDPOINT-003",
                        "Decision Endpoint",
                        "https://integration.example.test/decision",
                        "HTTPS",
                        "OAUTH2",
                        60,
                        "ACTIVE"
                )
        );

        Optional<IntegrationEndpoint> result =
                repository.findByEndpointCode(
                        "EFS-ENDPOINT-003"
                );

        assertTrue(result.isPresent());

        assertEquals(
                "Decision Endpoint",
                result.get().getEndpointName()
        );

        assertEquals(
                "OAUTH2",
                result.get().getAuthenticationType()
        );
    }

    @Test
    void shouldReturnEmptyWhenEndpointCodeDoesNotExist() {

        Optional<IntegrationEndpoint> result =
                repository.findByEndpointCode(
                        "EFS-ENDPOINT-NOT-FOUND"
                );

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindIntegrationEndpointsByStatusOrderedByEndpointName() {

        repository.save(
                createEndpoint(
                        "EFS-ENDPOINT-004",
                        "Zulu Endpoint",
                        "https://integration.example.test/zulu",
                        "HTTPS",
                        "API_KEY",
                        30,
                        "ACTIVE"
                )
        );

        repository.save(
                createEndpoint(
                        "EFS-ENDPOINT-005",
                        "Alpha Endpoint",
                        "https://integration.example.test/alpha",
                        "HTTPS",
                        "API_KEY",
                        30,
                        "ACTIVE"
                )
        );

        repository.save(
                createEndpoint(
                        "EFS-ENDPOINT-006",
                        "Inactive Endpoint",
                        "https://integration.example.test/inactive",
                        "HTTPS",
                        "NONE",
                        30,
                        "INACTIVE"
                )
        );

        repository.flush();

        List<IntegrationEndpoint> result =
                repository.findByStatusOrderByEndpointNameAsc(
                        "ACTIVE"
                );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                "Alpha Endpoint",
                result.get(0).getEndpointName()
        );

        assertEquals(
                "Zulu Endpoint",
                result.get(1).getEndpointName()
        );

        assertTrue(
                result.stream()
                        .allMatch(
                                endpoint ->
                                        "ACTIVE".equals(
                                                endpoint.getStatus()
                                        )
                        )
        );
    }

    @Test
    void shouldReturnEmptyListWhenStatusDoesNotExist() {

        repository.saveAndFlush(
                createEndpoint(
                        "EFS-ENDPOINT-007",
                        "Active Endpoint",
                        "https://integration.example.test/active",
                        "HTTPS",
                        "API_KEY",
                        30,
                        "ACTIVE"
                )
        );

        List<IntegrationEndpoint> result =
                repository.findByStatusOrderByEndpointNameAsc(
                        "UNKNOWN"
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private IntegrationEndpoint createEndpoint(
            String endpointCode,
            String endpointName,
            String endpointUrl,
            String protocol,
            String authenticationType,
            Integer timeoutSeconds,
            String status) {

        IntegrationEndpoint endpoint =
                new IntegrationEndpoint();

        endpoint.setEndpointCode(
                endpointCode
        );

        endpoint.setEndpointName(
                endpointName
        );

        endpoint.setEndpointUrl(
                endpointUrl
        );

        endpoint.setProtocol(
                protocol
        );

        endpoint.setAuthenticationType(
                authenticationType
        );

        endpoint.setTimeoutSeconds(
                timeoutSeconds
        );

        endpoint.setStatus(
                status
        );

        return endpoint;
    }
}