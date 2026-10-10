package com.shopsmart.ai.tools;

import com.shopsmart.orders.model.Inventory;
import com.shopsmart.orders.repository.InventoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class WarehouseInventoryTool {

    private final InventoryRepository inventoryRepository;

    public WarehouseInventoryTool(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public int getAvailableQuantity(UUID productId) {
        List<Inventory> list = inventoryRepository.findByIdProductId(productId);
        return list.stream().mapToInt(Inventory::getAvailableQuantity).sum();
    }

    public String checkStockStatus(UUID productId) {
        return getAvailableQuantity(productId) > 0 ? "IN_STOCK" : "OUT_OF_STOCK";
    }
}
