package com.shopsmart.orders.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderDetailDto {
    private UUID id;
    private UUID customerId;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private List<OrderItemDetailDto> items;
    private List<ShipmentEventDto> shipmentEvents;

    public OrderDetailDto() {}

    public OrderDetailDto(UUID id, UUID customerId, String status, BigDecimal totalAmount, LocalDateTime orderDate,
                          List<OrderItemDetailDto> items, List<ShipmentEventDto> shipmentEvents) {
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.items = items;
        this.shipmentEvents = shipmentEvents;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public List<OrderItemDetailDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDetailDto> items) {
        this.items = items;
    }

    public List<ShipmentEventDto> getShipmentEvents() {
        return shipmentEvents;
    }

    public void setShipmentEvents(List<ShipmentEventDto> shipmentEvents) {
        this.shipmentEvents = shipmentEvents;
    }
}
