package com.shopsmart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsmart.ai.dto.ChatMessageRequestDto;
import com.shopsmart.ai.dto.ChatResponseDto;
import com.shopsmart.ai.dto.RecommendationResponseDto;
import com.shopsmart.knowledge.dto.KnowledgeQueryRequestDto;
import com.shopsmart.knowledge.dto.KnowledgeResponseDto;
import com.shopsmart.orders.model.Customer;
import com.shopsmart.orders.repository.CustomerRepository;
import com.shopsmart.security.JwtTokenProvider;
import com.shopsmart.security.dto.LoginRequest;
import com.shopsmart.shopping.dto.CartItemRequestDto;
import com.shopsmart.shopping.model.Product;
import com.shopsmart.shopping.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ShopSmartApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String aliceToken;
    private Customer alice;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        alice = customerRepository.findByEmail("alice@example.com").orElseThrow();
        aliceToken = "Bearer " + jwtTokenProvider.generateToken(alice.getId(), alice.getEmail(), alice.getName(), alice.getMembershipTier());
        testProduct = productRepository.findAll().get(0);
    }

    @Test
    @DisplayName("Stage 1 - Authentication: Login with demo credentials returns JWT and membership profile")
    void testLoginSuccess() throws Exception {
        LoginRequest req = new LoginRequest("alice@example.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.customer.email").value("alice@example.com"))
                .andExpect(jsonPath("$.customer.membershipTier").value("Prime"));
    }

    @Test
    @DisplayName("Stage 1 - Catalog: Retrieve category hierarchy and product listing with pagination")
    void testCategoryHierarchyAndProducts() throws Exception {
        // Categories
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].name").value("Electronics"))
                .andExpect(jsonPath("$[0].subcategories", not(empty())));

        // Products
        mockMvc.perform(get("/api/products?page=0&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].availability").isString());
    }

    @Test
    @DisplayName("Stage 1 - Compare: Retrieve side-by-side comparison details")
    void testProductComparison() throws Exception {
        Product p2 = productRepository.findAll().get(1);
        mockMvc.perform(get("/api/products/compare?ids=" + testProduct.getId() + "," + p2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products", hasSize(2)))
                .andExpect(jsonPath("$.commonAttributeKeys", not(empty())))
                .andExpect(jsonPath("$.comparisonMatrix").isMap());
    }

    @Test
    @DisplayName("Stage 2 - Cart: Add item, validate quantity > 0 error response contract")
    void testCartValidation() throws Exception {
        // Valid add
        CartItemRequestDto validReq = new CartItemRequestDto(testProduct.getId(), 2);
        mockMvc.perform(post("/api/cart/items")
                        .header("Authorization", aliceToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", not(empty())));

        // Invalid quantity <= 0 (PDF Page 5 contract check)
        CartItemRequestDto invalidReq = new CartItemRequestDto(testProduct.getId(), 0);
        mockMvc.perform(post("/api/cart/items")
                        .header("Authorization", aliceToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value("Quantity must be greater than zero."));
    }

    @Test
    @DisplayName("Stage 2 - Orders: Retrieve customer simulated orders and shipment events")
    void testOrdersAndShipmentEvents() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/orders")
                        .header("Authorization", aliceToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        assertTrue(json.contains("SHIPPED") || json.contains("DELIVERED"));
    }

    @Test
    @DisplayName("Stage 2 - Warehouse Inventory: Retrieve warehouse stock breakdown")
    void testWarehouseInventory() throws Exception {
        mockMvc.perform(get("/api/inventory/" + testProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.warehouses", not(empty())))
                .andExpect(jsonPath("$.totalAvailable").isNumber())
                .andExpect(jsonPath("$.availability").isString());
    }

    @Test
    @DisplayName("Stage 3 - AI Recommendations: Matches exact JSON contract from PDF Page 4")
    void testAiRecommendations() throws Exception {
        ChatMessageRequestDto req = new ChatMessageRequestDto("Laptop for Java development and AI learning under $1000");

        MvcResult result = mockMvc.perform(post("/api/ai/recommendations")
                        .header("Authorization", aliceToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interpretedRequirement").isString())
                .andExpect(jsonPath("$.recommendations", not(empty())))
                .andExpect(jsonPath("$.recommendations[0].productName").isString())
                .andExpect(jsonPath("$.recommendations[0].reason").isString())
                .andExpect(jsonPath("$.recommendations[0].availability").value("IN_STOCK"))
                .andReturn();

        RecommendationResponseDto resp = objectMapper.readValue(result.getResponse().getContentAsString(), RecommendationResponseDto.class);
        assertNotNull(resp.getInterpretedRequirement());
        assertFalse(resp.getRecommendations().isEmpty());
    }

    @Test
    @DisplayName("Stage 4 - RAG Knowledge Assistant: Cites approved document, section, and version with HIGH confidence")
    void testRagKnowledge() throws Exception {
        KnowledgeQueryRequestDto req = new KnowledgeQueryRequestDto("Opened laptops return", "Policies", "GLOBAL");

        mockMvc.perform(post("/api/ai/knowledge")
                        .header("Authorization", aliceToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value(containsString("Opened laptops can be returned within 7 days")))
                .andExpect(jsonPath("$.confidence").value("HIGH"))
                .andExpect(jsonPath("$.sources[0].document").value("Laptop Return Policy"))
                .andExpect(jsonPath("$.sources[0].section").value("Opened Products"))
                .andExpect(jsonPath("$.sources[0].version").value("2026.1"))
                .andExpect(jsonPath("$.followUpRequired").value(false));
    }

    @Test
    @DisplayName("Stage 4 - Coordinated Assistant: Combines order tracking, warehouse check, and policy citations")
    void testCoordinatedAssistant() throws Exception {
        ChatMessageRequestDto req = new ChatMessageRequestDto("Where is my order, and can I cancel after shipment?");

        MvcResult result = mockMvc.perform(post("/api/ai/chat")
                        .header("Authorization", aliceToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.intent").value("COORDINATED_ASSISTANCE"))
                .andExpect(jsonPath("$.answer").value(containsString("Order #")))
                .andExpect(jsonPath("$.answer").value(containsString("Order Cancellation Policy")))
                .andExpect(jsonPath("$.sources", not(empty())))
                .andExpect(jsonPath("$.toolActivity", not(empty())))
                .andExpect(jsonPath("$.auditId").isNotEmpty())
                .andReturn();

        ChatResponseDto chatResp = objectMapper.readValue(result.getResponse().getContentAsString(), ChatResponseDto.class);
        assertNotNull(chatResp.getAuditId());
        assertTrue(chatResp.getAnswer().contains("cannot be cancelled in transit"));

        // Verify audit record exists
        mockMvc.perform(get("/api/ai/audit")
                        .header("Authorization", aliceToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())));
    }
}
