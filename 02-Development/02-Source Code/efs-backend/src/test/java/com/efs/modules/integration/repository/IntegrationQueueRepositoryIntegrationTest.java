package com.efs.modules.integration.repository;

import com.efs.modules.integration.entity.IntegrationQueue;
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
class IntegrationQueueRepositoryIntegrationTest {

    @Autowired
    private IntegrationQueueRepository repository;

    @Test
    void shouldSaveIntegrationQueue() {

        IntegrationQueue queue =
                createQueue(
                        "efs.v88.queue.save",
                        "RABBITMQ",
                        "efs.v88.topic.save",
                        0,
                        "efs-v88-consumer-save",
                        "ACTIVE"
                );

        IntegrationQueue saved =
                repository.saveAndFlush(
                        queue
                );

        assertNotNull(
                saved.getQueueId()
        );

        assertEquals(
                "efs.v88.queue.save",
                saved.getQueueName()
        );

        assertEquals(
                "RABBITMQ",
                saved.getBroker()
        );

        assertEquals(
                "efs.v88.topic.save",
                saved.getTopic()
        );

        assertEquals(
                0,
                saved.getPartition()
        );

        assertEquals(
                "efs-v88-consumer-save",
                saved.getConsumerGroup()
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
    void shouldFindIntegrationQueueById() {

        IntegrationQueue saved =
                repository.saveAndFlush(
                        createQueue(
                                "efs.v88.queue.id",
                                "RABBITMQ",
                                "efs.v88.topic.id",
                                1,
                                "efs-v88-consumer-id",
                                "ACTIVE"
                        )
                );

        Optional<IntegrationQueue> result =
                repository.findById(
                        saved.getQueueId()
                );

        assertTrue(
                result.isPresent()
        );

        assertEquals(
                saved.getQueueId(),
                result.get().getQueueId()
        );

        assertEquals(
                "efs.v88.queue.id",
                result.get().getQueueName()
        );
    }

    @Test
    void shouldReturnEmptyWhenIntegrationQueueDoesNotExist() {

        Optional<IntegrationQueue> result =
                repository.findById(
                        java.util.UUID.randomUUID()
                );

        assertTrue(
                result.isEmpty()
        );
    }

    @Test
    void shouldFindQueuesByBrokerOrderedByQueueNameAscending() {

        repository.saveAndFlush(
                createQueue(
                        "efs.v88.queue.broker.charlie",
                        "RABBITMQ-V88-BROKER",
                        "efs.v88.topic.broker.charlie",
                        2,
                        "efs-v88-consumer-broker-charlie",
                        "ACTIVE"
                )
        );

        repository.saveAndFlush(
                createQueue(
                        "efs.v88.queue.broker.alpha",
                        "RABBITMQ-V88-BROKER",
                        "efs.v88.topic.broker.alpha",
                        0,
                        "efs-v88-consumer-broker-alpha",
                        "ACTIVE"
                )
        );

        repository.saveAndFlush(
                createQueue(
                        "efs.v88.queue.broker.bravo",
                        "RABBITMQ-V88-BROKER",
                        "efs.v88.topic.broker.bravo",
                        1,
                        "efs-v88-consumer-broker-bravo",
                        "ACTIVE"
                )
        );

        List<IntegrationQueue> result =
                repository.findByBrokerOrderByQueueNameAsc(
                        "RABBITMQ-V88-BROKER"
                );

        assertEquals(
                3,
                result.size()
        );

        assertEquals(
                "efs.v88.queue.broker.alpha",
                result.get(0).getQueueName()
        );

        assertEquals(
                "efs.v88.queue.broker.bravo",
                result.get(1).getQueueName()
        );

        assertEquals(
                "efs.v88.queue.broker.charlie",
                result.get(2).getQueueName()
        );
    }

    @Test
    void shouldReturnEmptyListWhenBrokerDoesNotExist() {

        List<IntegrationQueue> result =
                repository.findByBrokerOrderByQueueNameAsc(
                        "NON_EXISTENT_V88_BROKER"
                );

        assertNotNull(
                result
        );

        assertTrue(
                result.isEmpty()
        );
    }

    @Test
    void shouldFindQueuesByStatusOrderedByQueueNameAscending() {

        repository.saveAndFlush(
                createQueue(
                        "efs.v88.queue.status.charlie",
                        "RABBITMQ",
                        "efs.v88.topic.status.charlie",
                        2,
                        "efs-v88-consumer-status-charlie",
                        "V88_TEST_ACTIVE"
                )
        );

        repository.saveAndFlush(
                createQueue(
                        "efs.v88.queue.status.alpha",
                        "RABBITMQ",
                        "efs.v88.topic.status.alpha",
                        0,
                        "efs-v88-consumer-status-alpha",
                        "V88_TEST_ACTIVE"
                )
        );

        repository.saveAndFlush(
                createQueue(
                        "efs.v88.queue.status.bravo",
                        "RABBITMQ",
                        "efs.v88.topic.status.bravo",
                        1,
                        "efs-v88-consumer-status-bravo",
                        "V88_TEST_ACTIVE"
                )
        );

        List<IntegrationQueue> result =
                repository.findByStatusOrderByQueueNameAsc(
                        "V88_TEST_ACTIVE"
                );

        assertEquals(
                3,
                result.size()
        );

        assertEquals(
                "efs.v88.queue.status.alpha",
                result.get(0).getQueueName()
        );

        assertEquals(
                "efs.v88.queue.status.bravo",
                result.get(1).getQueueName()
        );

        assertEquals(
                "efs.v88.queue.status.charlie",
                result.get(2).getQueueName()
        );
    }

    @Test
    void shouldReturnEmptyListWhenStatusDoesNotExist() {

        List<IntegrationQueue> result =
                repository.findByStatusOrderByQueueNameAsc(
                        "NON_EXISTENT_V88_STATUS"
                );

        assertNotNull(
                result
        );

        assertTrue(
                result.isEmpty()
        );
    }

    private IntegrationQueue createQueue(
            String queueName,
            String broker,
            String topic,
            Integer partition,
            String consumerGroup,
            String status) {

        IntegrationQueue queue =
                new IntegrationQueue();

        queue.setQueueName(
                queueName
        );

        queue.setBroker(
                broker
        );

        queue.setTopic(
                topic
        );

        queue.setPartition(
                partition
        );

        queue.setConsumerGroup(
                consumerGroup
        );

        queue.setStatus(
                status
        );

        return queue;
    }
}