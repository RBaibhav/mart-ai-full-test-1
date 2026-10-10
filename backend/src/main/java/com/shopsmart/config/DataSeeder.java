package com.shopsmart.config;

import com.shopsmart.common.JsonUtil;
import com.shopsmart.knowledge.model.DocumentChunk;
import com.shopsmart.knowledge.model.KnowledgeDocument;
import com.shopsmart.knowledge.repository.DocumentChunkRepository;
import com.shopsmart.knowledge.repository.KnowledgeDocumentRepository;
import com.shopsmart.knowledge.service.DocumentIngestionService;
import com.shopsmart.knowledge.service.EmbeddingUtil;
import com.shopsmart.orders.model.*;
import com.shopsmart.orders.repository.*;
import com.shopsmart.shopping.model.*;
import com.shopsmart.shopping.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final CustomerPreferenceRepository preferenceRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShipmentEventRepository shipmentEventRepository;
    private final KnowledgeDocumentRepository knowledgeDocumentRepository;
    private final DocumentChunkRepository documentChunkRepository;
    private final DocumentIngestionService ingestionService;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(CustomerRepository customerRepository,
                      CustomerPreferenceRepository preferenceRepository,
                      CategoryRepository categoryRepository,
                      ProductRepository productRepository,
                      WarehouseRepository warehouseRepository,
                      InventoryRepository inventoryRepository,
                      OrderRepository orderRepository,
                      OrderItemRepository orderItemRepository,
                      ShipmentEventRepository shipmentEventRepository,
                      KnowledgeDocumentRepository knowledgeDocumentRepository,
                      DocumentChunkRepository documentChunkRepository,
                      DocumentIngestionService ingestionService,
                      PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.preferenceRepository = preferenceRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.shipmentEventRepository = shipmentEventRepository;
        this.knowledgeDocumentRepository = knowledgeDocumentRepository;
        this.documentChunkRepository = documentChunkRepository;
        this.ingestionService = ingestionService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (customerRepository.count() > 0) {
            return; // Already seeded
        }

        // 1. Seed Demo Customers (Basic, Prime, Premium)
        Customer alice = customerRepository.save(new Customer(null, "Alice Walker", "alice@example.com",
                passwordEncoder.encode("password123"), "Prime"));
        Customer bob = customerRepository.save(new Customer(null, "Bob Smith", "bob@example.com",
                passwordEncoder.encode("password123"), "Premium"));
        Customer charlie = customerRepository.save(new Customer(null, "Charlie Brown", "charlie@example.com",
                passwordEncoder.encode("password123"), "Basic"));
        Customer admin = customerRepository.save(new Customer(null, "Admin Uploader", "admin@shopsmart.com",
                passwordEncoder.encode("admin123"), "Premium"));

        // Seed Customer Preferences
        CustomerPreference alicePref = new CustomerPreference(
                alice.getId(),
                JsonUtil.toJson(List.of("Laptops", "Audio")),
                JsonUtil.toJson(List.of("Dell", "Sony")),
                BigDecimal.valueOf(1200.00)
        );
        CustomerPreference bobPref = new CustomerPreference(
                bob.getId(),
                JsonUtil.toJson(List.of("Laptops", "Displays")),
                JsonUtil.toJson(List.of("Apple", "Dell")),
                BigDecimal.valueOf(2500.00)
        );
        CustomerPreference charliePref = new CustomerPreference(
                charlie.getId(),
                JsonUtil.toJson(List.of("Audio")),
                JsonUtil.toJson(List.of("Sony", "Bose")),
                BigDecimal.valueOf(400.00)
        );
        preferenceRepository.saveAll(List.of(alicePref, bobPref, charliePref));

        // 2. Seed Category Hierarchy
        Category rootElectronics = categoryRepository.save(new Category(null, "Electronics", null));
        Category laptops = categoryRepository.save(new Category(null, "Laptops & Computers", rootElectronics.getId()));
        Category audio = categoryRepository.save(new Category(null, "Audio & Headphones", rootElectronics.getId()));
        Category displays = categoryRepository.save(new Category(null, "Displays & Monitors", rootElectronics.getId()));

        // 3. Seed Products with Specifications
        Product dellLaptop = productRepository.save(new Product(
                null,
                laptops.getId(),
                "Dell Inspiron 15",
                "Dell",
                "Versatile 15.6-inch laptop for programming, productivity, and AI learning tasks.",
                BigDecimal.valueOf(899.99),
                BigDecimal.valueOf(4.6),
                JsonUtil.toJson(List.of("https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=600")),
                JsonUtil.toJson(Map.of(
                        "Processor", "Intel Core i7 13th Gen",
                        "RAM", "16GB DDR5",
                        "Storage", "512GB NVMe SSD",
                        "Display", "15.6-inch FHD (1920x1080) Anti-Glare",
                        "Battery", "Up to 8 hours",
                        "Weight", "1.65 kg"
                ))
        ));

        Product macbook = productRepository.save(new Product(
                null,
                laptops.getId(),
                "Apple MacBook Air M3",
                "Apple",
                "Strikingly thin and fast laptop with Apple M3 silicon and Liquid Retina display.",
                BigDecimal.valueOf(1099.00),
                BigDecimal.valueOf(4.8),
                JsonUtil.toJson(List.of("https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600")),
                JsonUtil.toJson(Map.of(
                        "Processor", "Apple M3 8-Core",
                        "RAM", "16GB Unified Memory",
                        "Storage", "512GB SSD",
                        "Display", "13.6-inch Liquid Retina",
                        "Battery", "Up to 18 hours",
                        "Weight", "1.24 kg"
                ))
        ));

        Product thinkpad = productRepository.save(new Product(
                null,
                laptops.getId(),
                "Lenovo ThinkPad E14",
                "Lenovo",
                "Rugged, secure enterprise laptop optimized for software engineering and multitasking.",
                BigDecimal.valueOf(749.00),
                BigDecimal.valueOf(4.4),
                JsonUtil.toJson(List.of("https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=600")),
                JsonUtil.toJson(Map.of(
                        "Processor", "AMD Ryzen 7 7730U",
                        "RAM", "16GB DDR4",
                        "Storage", "512GB NVMe SSD",
                        "Display", "14-inch IPS FHD",
                        "Battery", "Up to 10 hours",
                        "Weight", "1.41 kg"
                ))
        ));

        Product sonyHeadphones = productRepository.save(new Product(
                null,
                audio.getId(),
                "Sony WH-1000XM5 Wireless Headphones",
                "Sony",
                "Industry-leading noise canceling wireless headphones with crystal clear hands-free calling.",
                BigDecimal.valueOf(399.99),
                BigDecimal.valueOf(4.9),
                JsonUtil.toJson(List.of("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600")),
                JsonUtil.toJson(Map.of(
                        "Type", "Over-Ear Wireless",
                        "Noise Cancellation", "Integrated Processor V1 Active ANC",
                        "Battery", "30 hours with fast charge",
                        "Connectivity", "Bluetooth 5.2, 3.5mm AUX",
                        "Microphones", "8 noise-canceling mics"
                ))
        ));

        Product boseHeadphones = productRepository.save(new Product(
                null,
                audio.getId(),
                "Bose QuietComfort 45",
                "Bose",
                "Legendary quiet, lightweight comfort, and balanced sound for all-day focus.",
                BigDecimal.valueOf(329.00),
                BigDecimal.valueOf(4.7),
                JsonUtil.toJson(List.of("https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=600")),
                JsonUtil.toJson(Map.of(
                        "Type", "Over-Ear Wireless",
                        "Noise Cancellation", "Acoustic Noise Cancelling",
                        "Battery", "24 hours",
                        "Connectivity", "Bluetooth 5.1",
                        "Weight", "240 g"
                ))
        ));

        Product dellMonitor = productRepository.save(new Product(
                null,
                displays.getId(),
                "Dell UltraSharp 27 4K Monitor",
                "Dell",
                "Brilliant 4K UHD resolution with IPS Black technology and 98% DCI-P3 color coverage.",
                BigDecimal.valueOf(549.99),
                BigDecimal.valueOf(4.7),
                JsonUtil.toJson(List.of("https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=600")),
                JsonUtil.toJson(Map.of(
                        "Resolution", "3840 x 2160 (4K UHD)",
                        "Refresh Rate", "60 Hz",
                        "Panel", "IPS Black Technology",
                        "Connectivity", "USB-C Hub (90W PD), DisplayPort 1.4, HDMI",
                        "Screen Size", "27 inches"
                ))
        ));

        // 4. Seed Warehouses & Inventory
        Warehouse whWest = warehouseRepository.save(new Warehouse(null, "Warehouse Central West", "San Jose, CA"));
        Warehouse whEast = warehouseRepository.save(new Warehouse(null, "Warehouse East Hub", "Allentown, PA"));
        Warehouse whMidwest = warehouseRepository.save(new Warehouse(null, "Warehouse Midwest", "Chicago, IL"));

        inventoryRepository.saveAll(List.of(
                new Inventory(dellLaptop.getId(), whWest.getId(), 25),
                new Inventory(dellLaptop.getId(), whEast.getId(), 15),
                new Inventory(macbook.getId(), whWest.getId(), 10),
                new Inventory(macbook.getId(), whMidwest.getId(), 8),
                new Inventory(thinkpad.getId(), whEast.getId(), 30),
                new Inventory(sonyHeadphones.getId(), whWest.getId(), 40),
                new Inventory(sonyHeadphones.getId(), whEast.getId(), 20),
                new Inventory(boseHeadphones.getId(), whMidwest.getId(), 18),
                new Inventory(dellMonitor.getId(), whWest.getId(), 12)
        ));

        // 5. Seed Simulated Orders & Mocked Shipment Events for Alice
        Order aliceOrder = orderRepository.save(new Order(
                null,
                alice.getId(),
                "SHIPPED",
                dellLaptop.getPrice(),
                LocalDateTime.now().minusDays(3)
        ));

        OrderItem item1 = new OrderItem(aliceOrder.getId(), dellLaptop.getId(), 1, dellLaptop.getPrice());
        orderItemRepository.save(item1);

        ShipmentEvent event1 = new ShipmentEvent(null, aliceOrder.getId(), "ORDER_PLACED",
                LocalDateTime.now().minusDays(3), LocalDateTime.now().plusDays(1));
        ShipmentEvent event2 = new ShipmentEvent(null, aliceOrder.getId(), "PROCESSING",
                LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(1));
        ShipmentEvent event3 = new ShipmentEvent(null, aliceOrder.getId(), "CARRIER_PICKED_UP",
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        ShipmentEvent event4 = new ShipmentEvent(null, aliceOrder.getId(), "IN_TRANSIT",
                LocalDateTime.now().minusHours(8), LocalDateTime.now().plusDays(1));
        shipmentEventRepository.saveAll(List.of(event1, event2, event3, event4));

        // 6. Seed Approved Sample Documents from resources
        seedSamplePolicyDocuments();
    }

    private void seedSamplePolicyDocuments() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:sample-documents/*");

            for (Resource res : resources) {
                String filename = res.getFilename();
                if (filename == null) continue;

                String content = new String(res.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                if (filename.endsWith(".md")) {
                    KnowledgeDocument doc = knowledgeDocumentRepository.save(new KnowledgeDocument(
                            null,
                            "Laptop Return Policy",
                            "MARKDOWN",
                            "2026.1",
                            LocalDate.of(2026, 1, 1),
                            "Policies",
                            "GLOBAL",
                            "APPROVED"
                    ));

                    List<DocumentIngestionService.ChunkData> chunks = ingestionService.extractMarkdownChunks(content);
                    int idx = 0;
                    for (DocumentIngestionService.ChunkData c : chunks) {
                        List<Float> emb = EmbeddingUtil.generateEmbedding(c.text);
                        DocumentChunk chunk = new DocumentChunk(
                                null, doc.getId(), idx++, c.section, c.text, EmbeddingUtil.serializeEmbedding(emb)
                        );
                        documentChunkRepository.save(chunk);
                    }
                } else if (filename.endsWith(".json")) {
                    KnowledgeDocument doc = knowledgeDocumentRepository.save(new KnowledgeDocument(
                            null,
                            "Order Cancellation Policy",
                            "JSON",
                            "2026.1",
                            LocalDate.of(2026, 1, 1),
                            "Policies",
                            "GLOBAL",
                            "APPROVED"
                    ));

                    List<DocumentIngestionService.ChunkData> chunks = ingestionService.extractJsonChunks(content);
                    int idx = 0;
                    for (DocumentIngestionService.ChunkData c : chunks) {
                        List<Float> emb = EmbeddingUtil.generateEmbedding(c.text);
                        DocumentChunk chunk = new DocumentChunk(
                                null, doc.getId(), idx++, c.section, c.text, EmbeddingUtil.serializeEmbedding(emb)
                        );
                        documentChunkRepository.save(chunk);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Notice: Could not load sample policy documents: " + e.getMessage());
        }
    }
}
