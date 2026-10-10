package com.shopsmart.orders.dto;

import java.util.List;
import java.util.UUID;

public class InventoryResponseDto {
    private UUID productId;
    private String productName;
    private int totalAvailable;
    private String availability; // IN_STOCK, OUT_OF_STOCK
    private List<WarehouseStockDto> warehouses;

    public InventoryResponseDto() {}

    public InventoryResponseDto(UUID productId, String productName, int totalAvailable, String availability, List<WarehouseStockDto> warehouses) {
        this.productId = productId;
        this.productName = productName;
        this.totalAvailable = totalAvailable;
        this.availability = availability;
        this.warehouses = warehouses;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getTotalAvailable() {
        return totalAvailable;
    }

    public void setTotalAvailable(int totalAvailable) {
        this.totalAvailable = totalAvailable;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public List<WarehouseStockDto> getWarehouses() {
        return warehouses;
    }

    public void setWarehouses(List<WarehouseStockDto> warehouses) {
        this.warehouses = warehouses;
    }

    public static class WarehouseStockDto {
        private UUID warehouseId;
        private String name;
        private String location;
        private int availableQuantity;

        public WarehouseStockDto() {}

        public WarehouseStockDto(UUID warehouseId, String name, String location, int availableQuantity) {
            this.warehouseId = warehouseId;
            this.name = name;
            this.location = location;
            this.availableQuantity = availableQuantity;
        }

        public UUID getWarehouseId() {
            return warehouseId;
        }

        public void setWarehouseId(UUID warehouseId) {
            this.warehouseId = warehouseId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public int getAvailableQuantity() {
            return availableQuantity;
        }

        public void setAvailableQuantity(int availableQuantity) {
            this.availableQuantity = availableQuantity;
        }
    }
}
