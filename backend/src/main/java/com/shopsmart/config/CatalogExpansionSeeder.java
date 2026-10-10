package com.shopsmart.config;

import com.shopsmart.common.JsonUtil;
import com.shopsmart.orders.model.Inventory;
import com.shopsmart.orders.model.InventoryId;
import com.shopsmart.orders.model.Warehouse;
import com.shopsmart.orders.repository.InventoryRepository;
import com.shopsmart.orders.repository.WarehouseRepository;
import com.shopsmart.shopping.model.Category;
import com.shopsmart.shopping.model.Product;
import com.shopsmart.shopping.repository.CategoryRepository;
import com.shopsmart.shopping.repository.ProductRepository;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
@Order(2)
public class CatalogExpansionSeeder implements org.springframework.boot.CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;

    public CatalogExpansionSeeder(CategoryRepository categoryRepository,
                                  ProductRepository productRepository,
                                  WarehouseRepository warehouseRepository,
                                  InventoryRepository inventoryRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Category electronics = findOrCreateCategory("Electronics", null);
        Category mobiles = findOrCreateCategory("Mobile Devices", electronics.getId());
        Category cameras = findOrCreateCategory("Cameras", electronics.getId());
        Category accessories = findOrCreateCategory("Accessories", electronics.getId());
        Category displays = findOrCreateCategory("Displays & Monitors", electronics.getId());
        Category audio = findOrCreateCategory("Audio & Headphones", electronics.getId());

        Warehouse warehouse = warehouseRepository.findAll().stream().findFirst()
                .orElseGet(() -> warehouseRepository.save(new Warehouse(null, "Central Warehouse", "New Jersey, US")));
        Warehouse westWarehouse = warehouseRepository.findAll().stream()
                .filter(item -> "West Coast Fulfillment".equals(item.getName()))
                .findFirst()
                .orElseGet(() -> warehouseRepository.save(new Warehouse(null, "West Coast Fulfillment", "Nevada, US")));

        List<ProductSeed> products = List.of(
                new ProductSeed(mobiles, "Google Pixel 9", "Google", "AI-first smartphone with a bright display and all-day battery.", "699.00", "4.6", "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=600", Map.of("Display", "6.3-inch OLED", "Storage", "128GB", "Battery", "All-day battery", "Camera", "50MP wide camera")),
                new ProductSeed(mobiles, "Samsung Galaxy S25", "Samsung", "Compact flagship smartphone for productivity, photography, and everyday use.", "799.99", "4.7", "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=600", Map.of("Display", "6.2-inch AMOLED", "Storage", "256GB", "Memory", "12GB RAM", "Connectivity", "5G")),
                new ProductSeed(cameras, "Canon EOS R10 Mirrorless Camera", "Canon", "Fast, lightweight mirrorless camera for creators learning photography and video.", "979.00", "4.8", "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=600", Map.of("Sensor", "24.2MP APS-C", "Video", "4K 30p", "Autofocus", "Dual Pixel CMOS AF", "Mount", "RF-S mount")),
                new ProductSeed(accessories, "Logitech MX Master 3S", "Logitech", "Quiet, ergonomic wireless mouse designed for multi-device productivity.", "99.99", "4.8", "https://images.unsplash.com/photo-1527814050087-3793815479db?w=600", Map.of("Connectivity", "Bluetooth and Logi Bolt", "DPI", "8000 DPI", "Battery", "70 days", "Devices", "Up to 3")),
                new ProductSeed(displays, "LG UltraGear 27 Gaming Monitor", "LG", "High-refresh QHD monitor with adaptive sync for work and play.", "329.99", "4.5", "https://images.unsplash.com/photo-1593640408182-31c70c8268f5?w=600", Map.of("Resolution", "2560 x 1440", "Refresh Rate", "165Hz", "Panel", "IPS", "Response", "1ms")),
                new ProductSeed(audio, "Anker Soundcore Space Q45", "Anker", "Comfortable wireless headphones with adaptive noise cancellation for long focus sessions.", "149.99", "4.5", "https://images.unsplash.com/photo-1484704849700-f032a568e944?w=600", Map.of("Type", "Over-Ear Wireless", "Noise Cancellation", "Adaptive ANC", "Battery", "50 hours", "Connectivity", "Bluetooth 5.3"))
        );

        for (ProductSeed seed : products) {
            Product product = productRepository.findAll().stream()
                    .filter(existing -> existing.getName().equalsIgnoreCase(seed.name()))
                    .findFirst()
                    .orElseGet(() -> productRepository.save(new Product(
                            null,
                            seed.category().getId(),
                            seed.name(),
                            seed.brand(),
                            seed.description(),
                            new BigDecimal(seed.price()),
                            new BigDecimal(seed.rating()),
                            JsonUtil.toJson(List.of(seed.image())),
                            JsonUtil.toJson(seed.specifications())
                    )));

            addInventoryIfMissing(product, warehouse, 18);
            addInventoryIfMissing(product, westWarehouse, 9);
        }
    }

    private Category findOrCreateCategory(String name, java.util.UUID parentId) {
        return categoryRepository.findAll().stream()
                .filter(category -> category.getName().equalsIgnoreCase(name)
                        && java.util.Objects.equals(category.getParentCategoryId(), parentId))
                .findFirst()
                .orElseGet(() -> categoryRepository.save(new Category(null, name, parentId)));
    }

    private void addInventoryIfMissing(Product product, Warehouse warehouse, int quantity) {
        InventoryId id = new InventoryId(product.getId(), warehouse.getId());
        if (!inventoryRepository.existsById(id)) {
            inventoryRepository.save(new Inventory(product.getId(), warehouse.getId(), quantity));
        }
    }

    private record ProductSeed(Category category, String name, String brand, String description,
                               String price, String rating, String image, Map<String, String> specifications) {}
}
