package com.shopsmart.orders.service;

import com.shopsmart.common.ApiException;
import com.shopsmart.orders.dto.InventoryResponseDto;
import com.shopsmart.orders.model.Inventory;
import com.shopsmart.orders.model.Warehouse;
import com.shopsmart.orders.repository.InventoryRepository;
import com.shopsmart.orders.repository.WarehouseRepository;
import com.shopsmart.shopping.model.Product;
import com.shopsmart.shopping.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;

    public InventoryService(InventoryRepository inventoryRepository,
                            WarehouseRepository warehouseRepository,
                            ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.warehouseRepository = warehouseRepository;
        this.productRepository = productRepository;
    }

    public InventoryResponseDto getInventoryByProductId(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "Product not found with ID: " + productId, HttpStatus.NOT_FOUND));

        List<Inventory> inventoryList = inventoryRepository.findByIdProductId(productId);
        List<InventoryResponseDto.WarehouseStockDto> warehouseStocks = new ArrayList<>();
        int totalAvailable = 0;

        for (Inventory inv : inventoryList) {
            Warehouse wh = warehouseRepository.findById(inv.getId().getWarehouseId()).orElse(null);
            String whName = wh != null ? wh.getName() : "Unknown Warehouse";
            String whLoc = wh != null ? wh.getLocation() : "Unknown";
            totalAvailable += inv.getAvailableQuantity();

            warehouseStocks.add(new InventoryResponseDto.WarehouseStockDto(
                    inv.getId().getWarehouseId(),
                    whName,
                    whLoc,
                    inv.getAvailableQuantity()
            ));
        }

        String availability = totalAvailable > 0 ? "IN_STOCK" : "OUT_OF_STOCK";
        return new InventoryResponseDto(productId, product.getName(), totalAvailable, availability, warehouseStocks);
    }
}
