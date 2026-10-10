package com.shopsmart.orders.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ShipmentEventDto {
    private UUID id;
    private UUID orderId;
    private String status;
    private LocalDateTime timestamp;
    private LocalDateTime estimatedDelivery;

    public ShipmentEventDto() {}

    public ShipmentEventDto(UUID id, UUID orderId, String status, LocalDateTime timestamp, LocalDateTime estimatedDelivery) {
        this.id = id;
        this.orderId = orderId;
        this.status = status;
        this.timestamp = timestamp;
        this.estimatedDelivery = estimatedDelivery;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDateTime getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(LocalDateTime estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }
}
