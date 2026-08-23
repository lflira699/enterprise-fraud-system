package com.efs.modules.integration.dto;

import java.util.Map;

public class IntegrationEventRequest {

    private String eventName;
    private String eventVersion;
    private Map<String, Object> eventPayload;
    private String publisher;
    private String status;

    public IntegrationEventRequest() {
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventVersion() {
        return eventVersion;
    }

    public void setEventVersion(String eventVersion) {
        this.eventVersion = eventVersion;
    }

    public Map<String, Object> getEventPayload() {
        return eventPayload;
    }

    public void setEventPayload(Map<String, Object> eventPayload) {
        this.eventPayload = eventPayload;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}