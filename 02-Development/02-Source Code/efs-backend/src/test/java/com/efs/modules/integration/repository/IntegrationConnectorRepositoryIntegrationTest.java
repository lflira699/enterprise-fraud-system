package com.efs.modules.integration.repository;

import com.efs.modules.integration.entity.IntegrationConnector;
import com.efs.modules.integration.entity.IntegrationEndpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class IntegrationConnectorRepositoryIntegrationTest {

    @Autowired
    private IntegrationConnectorRepository connectorRepository;

    @Autowired
    private IntegrationEndpointRepository endpointRepository;

    private UUID endpointId;

    @BeforeEach
    void setUp() {

        IntegrationEndpoint endpoint =
                new IntegrationEndpoint();

        endpoint.setEndpointCode(
                "EFS-V84-ENDPOINT"
        );

        endpoint.setEndpointName(
                "V84 Connector Test Endpoint"
        );

        endpoint.setEndpointUrl(
                "https://integration.example.test/v84"
        );

        endpoint.setProtocol(
                "HTTPS"
        );

        endpoint.setAuthenticationType(
                "API_KEY"
        );

        endpoint.setTimeoutSeconds(
                30
        );

        endpoint.setStatus(
                "ACTIVE"
        );

        IntegrationEndpoint savedEndpoint =
                endpointRepository.saveAndFlush(
                        endpoint
                );

        endpointId =
                savedEndpoint.getEndpointId();

        assertNotNull(
                endpointId
        );
    }

    @Test
    void shouldSaveIntegrationConnector() {

        IntegrationConnector connector =
                createConnector(
                        endpointId,
                        "Primary Connector",
                        "REST",
                        "EFS",
                        "1.0",
                        "ACTIVE"
                );

        IntegrationConnector saved =
                connectorRepository.saveAndFlush(
                        connector
                );

        assertNotNull(
                saved.getConnectorId()
        );

        assertEquals(
                endpointId,
                saved.getEndpointId()
        );

        assertEquals(
                "Primary Connector",
                saved.getConnectorName()
        );

        assertEquals(
                "REST",
                saved.getConnectorType()
        );

        assertEquals(
                "EFS",
                saved.getProvider()
        );

        assertEquals(
                "1.0",
                saved.getVersion()
        );

        assertEquals(
                "ACTIVE",
                saved.getStatus()
        );

        assertNotNull(
                saved.getCreatedAt()
        );
    }

    @Test
    void shouldFindIntegrationConnectorById() {

        IntegrationConnector saved =
                connectorRepository.saveAndFlush(
                        createConnector(
                                endpointId,
                                "Lookup Connector",
                                "REST",
                                "EFS",
                                "1.0",
                                "ACTIVE"
                        )
                );

        Optional<IntegrationConnector> result =
                connectorRepository.findById(
                        saved.getConnectorId()
                );

        assertTrue(
                result.isPresent()
        );

        assertEquals(
                saved.getConnectorId(),
                result.get().getConnectorId()
        );

        assertEquals(
                endpointId,
                result.get().getEndpointId()
        );

        assertEquals(
                "Lookup Connector",
                result.get().getConnectorName()
        );
    }

    @Test
    void shouldFindIntegrationConnectorsByEndpointId() {

        connectorRepository.save(
                createConnector(
                        endpointId,
                        "Endpoint Connector One",
                        "REST",
                        "EFS",
                        "1.0",
                        "ACTIVE"
                )
        );

        connectorRepository.save(
                createConnector(
                        endpointId,
                        "Endpoint Connector Two",
                        "REST",
                        "EFS",
                        "2.0",
                        "ACTIVE"
                )
        );

        connectorRepository.flush();

        List<IntegrationConnector> result =
                connectorRepository.findByEndpointId(
                        endpointId
                );

        assertEquals(
                2,
                result.size()
        );

        assertTrue(
                result.stream()
                        .allMatch(
                                connector ->
                                        endpointId.equals(
                                                connector.getEndpointId()
                                        )
                        )
        );
    }

    @Test
    void shouldReturnEmptyListWhenEndpointIdDoesNotExist() {

        List<IntegrationConnector> result =
                connectorRepository.findByEndpointId(
                        UUID.fromString(
                                "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"
                        )
                );

        assertNotNull(
                result
        );

        assertTrue(
                result.isEmpty()
        );
    }

    @Test
    void shouldFindIntegrationConnectorsByStatusOrderedByConnectorName() {

        connectorRepository.save(
                createConnector(
                        endpointId,
                        "Zulu Connector",
                        "REST",
                        "EFS",
                        "1.0",
                        "ACTIVE"
                )
        );

        connectorRepository.save(
                createConnector(
                        endpointId,
                        "Alpha Connector",
                        "REST",
                        "EFS",
                        "1.0",
                        "ACTIVE"
                )
        );

        connectorRepository.save(
                createConnector(
                        endpointId,
                        "Inactive Connector",
                        "REST",
                        "EFS",
                        "1.0",
                        "INACTIVE"
                )
        );

        connectorRepository.flush();

        List<IntegrationConnector> result =
                connectorRepository
                        .findByStatusOrderByConnectorNameAsc(
                                "ACTIVE"
                        );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                "Alpha Connector",
                result.get(0).getConnectorName()
        );

        assertEquals(
                "Zulu Connector",
                result.get(1).getConnectorName()
        );

        assertTrue(
                result.stream()
                        .allMatch(
                                connector ->
                                        "ACTIVE".equals(
                                                connector.getStatus()
                                        )
                        )
        );
    }

    @Test
    void shouldReturnEmptyListWhenStatusDoesNotExist() {

        connectorRepository.saveAndFlush(
                createConnector(
                        endpointId,
                        "Active Connector",
                        "REST",
                        "EFS",
                        "1.0",
                        "ACTIVE"
                )
        );

        List<IntegrationConnector> result =
                connectorRepository
                        .findByStatusOrderByConnectorNameAsc(
                                "UNKNOWN"
                        );

        assertNotNull(
                result
        );

        assertTrue(
                result.isEmpty()
        );
    }

    private IntegrationConnector createConnector(
            UUID endpointId,
            String connectorName,
            String connectorType,
            String provider,
            String version,
            String status) {

        IntegrationConnector connector =
                new IntegrationConnector();

        connector.setEndpointId(
                endpointId
        );

        connector.setConnectorName(
                connectorName
        );

        connector.setConnectorType(
                connectorType
        );

        connector.setProvider(
                provider
        );

        connector.setVersion(
                version
        );

        connector.setStatus(
                status
        );

        connector.setCreatedAt(
                LocalDateTime.now()
        );

        return connector;
    }
}