package com.efs.modules.integration.dto;

import java.util.UUID;

public class ExternalNotificationDeliveryRequest {

    private UUID notificationDeliveryId;
    private UUID correlationId;
    private UUID organizationId;
    private UUID tenantId;
    private String channel;
    private UUID recipientUserId;
    private String destination;
    private String subject;
    private String body;

    public ExternalNotificationDeliveryRequest() {
    }

    public UUID getNotificationDeliveryId() {
        return notificationDeliveryId;
    }

    public void setNotificationDeliveryId(
            UUID notificationDeliveryId) {

        this.notificationDeliveryId =
                notificationDeliveryId;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(
            UUID correlationId) {

        this.correlationId =
                correlationId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(
            UUID organizationId) {

        this.organizationId =
                organizationId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(
            UUID tenantId) {

        this.tenantId =
                tenantId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(
            String channel) {

        this.channel =
                channel;
    }

    public UUID getRecipientUserId() {
        return recipientUserId;
    }

    public void setRecipientUserId(
            UUID recipientUserId) {

        this.recipientUserId =
                recipientUserId;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(
            String destination) {

        this.destination =
                destination;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(
            String subject) {

        this.subject =
                subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(
            String body) {

        this.body =
                body;
    }
}