package com.shopsmart.orders.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shipment_event")
public class ShipmentEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private String status; // ORDER_PLACED, PROCESSING, CARRIER_PICKED_UP, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(name = "estimated_delivery")
    private LocalDateTime estimatedDelivery;

    public ShipmentEvent() {}

    public ShipmentEvent(UUID id, UUID orderId, String status, LocalDateTime timestamp, LocalDateTime estimatedDelivery) {
        this.id = id;
        this.orderId = orderId;
        this.status = status;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
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
