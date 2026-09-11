package com.efs.modules.integration.dto;

public class ExternalNotificationDeliveryResult {

    private boolean delivered;
    private String deliveryReference;
    private String deliveryResult;

    public ExternalNotificationDeliveryResult() {
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(
            boolean delivered) {

        this.delivered =
                delivered;
    }

    public String getDeliveryReference() {
        return deliveryReference;
    }

    public void setDeliveryReference(
            String deliveryReference) {

        this.deliveryReference =
                deliveryReference;
    }

    public String getDeliveryResult() {
        return deliveryResult;
    }

    public void setDeliveryResult(
            String deliveryResult) {

        this.deliveryResult =
                deliveryResult;
    }
}