package com.efs.modules.integration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "integration_queue",
        schema = "integration"
)
public class IntegrationQueue {

    @Id
    @Generated
    @ColumnDefault("uuidv7()")
    @Column(name = "queue_id", nullable = false)
    private UUID queueId;

    @Column(
            name = "queue_name",
            nullable = false,
            length = 150
    )
    private String queueName;

    @Column(
            name = "broker",
            nullable = false,
            length = 100
    )
    private String broker;

    @Column(
            name = "topic",
            nullable = false,
            length = 150
    )
    private String topic;

    @Column(name = "partition")
    private Integer partition;

    @Column(
            name = "consumer_group",
            length = 150
    )
    private String consumerGroup;

    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private String status;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    public IntegrationQueue() {
    }

    public UUID getQueueId() {
        return queueId;
    }

    public void setQueueId(UUID queueId) {
        this.queueId = queueId;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getPartition() {
        return partition;
    }

    public void setPartition(Integer partition) {
        this.partition = partition;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}