# ShopSmart AI

ShopSmart AI is an intelligent e-commerce shopping platform and AI assistant built strictly according to the **ShopSmart AI Solution Design** specification.

---

## 1. Project Overview & Committed Scope

ShopSmart AI provides a responsive shopping interface and one AI assistant that coordinates catalog data, approved knowledge documents, and customer order information.

### Committed Scope

- **Shopping**: Login, catalog, category hierarchy, search, category/brand/price filters, price/rating sorting, pagination, comparison, wishlist, cart, preferences, and responsive layout.
- **Recommendations**: Interpret intent, extract requirements, apply product constraints, recommend catalog matches, explain reasons, check availability, and clarify incomplete requests.
- **Knowledge assistant**: Upload PDF, Markdown, and JSON; extract, chunk, embed, retrieve, and generate grounded answers with document references.
- **Order tracking**: Retrieve simulated customer orders and mocked shipment events.
- **Agent coordination**: One assistant calls backend services for product search, inventory, order status, and knowledge retrieval.
- **Explainability and audit**: Display reasons and citations; record requests, tool activity, source references, outcomes, and timestamps.

### Scope Boundaries (Strictly Excluded)

- No payment integration.
- No real carrier or external marketplace integrations.
- No stock forecasting, promotion management, or pricing automation.
- No production-scale deployment in initial demo.
- Cancellation eligibility is strictly informational; the assistant does not execute cancellations.

---

## 2. Defined Folder Structure

```
shop-ai/
├── README.md                           # Documentation, contracts, and setup instructions
├── docker-compose.yml                  # PostgreSQL 16 + pgvector container configuration
├── backend/                            # Spring Boot 3.3.4 (Java 17) backend application
│   ├── pom.xml                         # Maven dependencies & build configuration
│   └── src/
│       ├── main/
│       │   ├── java/com/shopsmart/
│       │   │   ├── ShopSmartApplication.java
│       │   │   ├── common/             # Standard error responses & exception handling
│       │   │   │   ├── ApiException.java
│       │   │   │   ├── ErrorResponse.java
│       │   │   │   ├── GlobalExceptionHandler.java
│       │   │   │   └── JsonUtil.java
│       │   │   ├── security/           # Demo authentication & JWT session management
│       │   │   │   ├── AuthController.java
│       │   │   │   ├── AuthService.java
│       │   │   │   ├── CurrentUser.java
│       │   │   │   ├── JwtAuthenticationFilter.java
│       │   │   │   ├── JwtTokenProvider.java
│       │   │   │   ├── SecurityConfig.java
│       │   │   │   ├── UserContext.java
│       │   │   │   └── dto/ (LoginRequest, LoginResponse, CustomerProfileDto)
│       │   │   ├── shopping/           # Catalog, categories, comparison, cart, wishlist, preferences
│       │   │   │   ├── controller/
│       │   │   │   │   ├── CategoryController.java
│       │   │   │   │   ├── ProductController.java
│       │   │   │   │   ├── PreferenceController.java
│       │   │   │   │   ├── WishlistController.java
│       │   │   │   │   └── CartController.java
│       │   │   │   ├── model/ (Category, Product, CustomerPreference, WishlistItem, CartItem)
│       │   │   │   ├── repository/
│       │   │   │   ├── service/ (CategoryService, ProductService, PreferenceService, WishlistService, CartService)
│       │   │   │   └── dto/
│       │   │   ├── orders/             # Customer orders, mocked shipment events, warehouse inventory
│       │   │   │   ├── controller/
│       │   │   │   │   ├── OrderController.java
│       │   │   │   │   └── InventoryController.java
│       │   │   │   ├── model/ (Customer, Warehouse, Inventory, Order, OrderItem, ShipmentEvent)
│       │   │   │   ├── repository/
│       │   │   │   ├── service/ (OrderService, InventoryService)
│       │   │   │   └── dto/
│       │   │   ├── knowledge/          # Ingestion (PDF, MD, JSON), chunking, embeddings, RAG retrieval
│       │   │   │   ├── controller/
│       │   │   │   │   └── DocumentController.java
│       │   │   │   ├── model/ (KnowledgeDocument, DocumentChunk)
│       │   │   │   ├── repository/
│       │   │   │   ├── service/ (DocumentIngestionService, RagService, EmbeddingUtil)
│       │   │   │   └── dto/ (DocumentUploadResponse, KnowledgeResponseDto, KnowledgeQueryRequestDto)
│       │   │   ├── ai/                 # Assistant coordination, recommendations, tools, audits
│       │   │   │   ├── controller/
│       │   │   │   │   └── AiController.java
│       │   │   │   ├── model/ (AuditRecord)
│       │   │   │   ├── repository/ (AuditRecordRepository)
│       │   │   │   ├── tools/
│       │   │   │   │   ├── ProductCatalogTool.java
│       │   │   │   │   ├── WarehouseInventoryTool.java
│       │   │   │   │   ├── CustomerOrderTool.java
│       │   │   │   │   └── PolicyKnowledgeTool.java
│       │   │   │   ├── service/ (RecommendationService, CoordinatedChatService, AuditService)
│       │   │   │   └── dto/ (RecommendationResponseDto, ChatResponseDto, ChatMessageRequestDto)
│       │   │   └── config/
│       │   │       └── DataSeeder.java # Auto-populates demo accounts, catalog, warehouses, orders, & docs
│       │   └── resources/
│       │       ├── application.yml
│       │       ├── application-postgres.yml
│       │       ├── schema.sql
│       │       └── sample-documents/   # Seeded approved markdown & JSON policy documents
│       └── test/
│           ├── java/com/shopsmart/
│           │   └── ShopSmartApplicationTests.java # End-to-end integration tests for all 4 stages
│           └── resources/mockito-extensions/
│
└── frontend/                           # React 19 + Vite responsive frontend application
    ├── package.json
    ├── vite.config.js
    ├── index.html
    └── src/
        ├── main.jsx
        ├── App.jsx
        ├── index.css                   # Responsive typography and layout design
        ├── api/
        │   └── client.js               # Frontend API client covering all endpoints
        ├── context/
        │   └── AuthContext.jsx         # Demo accounts switcher & JWT token storage
        ├── components/
        │   ├── Navbar.jsx              # Navigation tabs, membership badge, account switcher
        │   ├── ProductCard.jsx         # Product display, stock badge, compare & cart triggers
        │   ├── FilterSidebar.jsx       # Category tree, brand filter, price range inputs
        │   ├── Pagination.jsx          # Pagination controls
        │   ├── ComparisonModal.jsx     # Side-by-side product comparison matrix
        │   ├── AssistantDrawer.jsx     # Coordinated AI Assistant with RAG citations & tool audits
        │   ├── DocumentUploadModal.jsx # Policy uploader (PDF/MD/JSON) with demo uploader guard
        │   └── ProductDetailPageModal.jsx # Specifications & warehouse stock breakdown modal
        └── pages/
            ├── CatalogPage.jsx         # Shopping catalog with filters and floating comparison tray
            ├── CartPage.jsx            # Cart item quantity updates, subtotal, and total calculation
            ├── WishlistPage.jsx        # Saved products wishlist
            ├── OrdersPage.jsx          # Simulated customer orders with mocked shipment timeline
            ├── PreferencesPage.jsx     # Saved preferred categories, brands, and budget
            └── AuditPage.jsx           # Audit records inspector for explainability
```

---

## 3. Data Model & Entities

The relational database matches the Entity Relationship Diagram:

1. **Customer**: `id (UUID PK)`, `name`, `email`, `password_hash`, `membership_tier` (Basic, Prime, Premium), `created_at`.
2. **CustomerPreference**: `customer_id (UUID PK FK)`, `preferred_categories (JSONB)`, `preferred_brands (JSONB)`, `budget`.
3. **Category**: `id (UUID PK)`, `name`, `parent_category_id (UUID FK)`.
4. **Product**: `id (UUID PK)`, `category_id (UUID FK)`, `name`, `brand`, `description`, `price`, `rating`, `images (JSONB)`, `specifications (JSONB)`.
5. **Warehouse**: `id (UUID PK)`, `name`, `location`.
6. **Inventory**: `product_id (UUID PK FK)`, `warehouse_id (UUID PK FK)`, `available_quantity`, `updated_at`.
7. **WishlistItem**: `customer_id (UUID PK FK)`, `product_id (UUID PK FK)`, `added_at`.
8. **CartItem**: `customer_id (UUID PK FK)`, `product_id (UUID PK FK)`, `quantity`.
9. **Order**: `id (UUID PK)`, `customer_id (UUID FK)`, `status`, `total_amount`, `order_date`.
10. **OrderItem**: `order_id (UUID PK FK)`, `product_id (UUID PK FK)`, `quantity`, `unit_price`.
11. **ShipmentEvent**: `id (UUID PK)`, `order_id (UUID FK)`, `status`, `timestamp`, `estimated_delivery`.
12. **KnowledgeDocument**: `id (UUID PK)`, `name`, `format`, `version`, `effective_date`, `category`, `region`, `approval_status`.
13. **DocumentChunk**: `id (UUID PK)`, `document_id (UUID FK)`, `chunk_index`, `section`, `text`, `embedding`.
14. **AuditRecord**: `id (UUID PK)`, `customer_id (UUID FK)`, `request`, `ai_response`, `model_name`, `tool_activity (JSONB)`, `source_references (JSONB)`, `outcome`, `timestamp`.

---

## 4. API Endpoints

| Method   | Endpoint                      | Request Body / Params                                           | Description                                           |
| -------- | ----------------------------- | --------------------------------------------------------------- | ----------------------------------------------------- |
| `POST`   | `/api/auth/login`             | `{ email, password }`                                           | Authenticates demo customer and returns profile + JWT |
| `GET`    | `/api/categories`             | None                                                            | Returns category tree hierarchy                       |
| `GET`    | `/api/products`               | `search, category, brand, priceMin, priceMax, sort, page, size` | Products pagination & filters                         |
| `GET`    | `/api/products/{id}`          | Product ID                                                      | Product details, specifications, availability         |
| `GET`    | `/api/products/compare`       | `ids` (comma-separated UUIDs)                                   | Side-by-side comparison matrix                        |
| `GET`    | `/api/preferences`            | None                                                            | Authenticated customer preferences                    |
| `PUT`    | `/api/preferences`            | `{ categories, brands, budget }`                                | Updates customer preferences                          |
| `GET`    | `/api/wishlist`               | None                                                            | Authenticated customer wishlist                       |
| `POST`   | `/api/wishlist`               | `{ productId }`                                                 | Adds product to wishlist                              |
| `DELETE` | `/api/wishlist/{productId}`   | Product ID                                                      | Removes product from wishlist                         |
| `GET`    | `/api/cart`                   | None                                                            | Retrieves cart items and total                        |
| `POST`   | `/api/cart/items`             | `{ productId, quantity }`                                       | Adds product to cart                                  |
| `PUT`    | `/api/cart/items/{productId}` | `{ quantity }`                                                  | Updates item quantity                                 |
| `DELETE` | `/api/cart/items/{productId}` | Product ID                                                      | Removes item from cart                                |
| `GET`    | `/api/orders`                 | None                                                            | Customer orders list                                  |
| `GET`    | `/api/orders/{id}`            | Order ID                                                        | Order items and mocked shipment events                |
| `GET`    | `/api/inventory/{productId}`  | Product ID                                                      | Warehouse breakdown and stock availability            |
| `POST`   | `/api/documents`              | Multipart (`file, version, effectiveDate, category, region`)    | Ingests PDF/MD/JSON document                          |
| `POST`   | `/api/ai/recommendations`     | `{ message }`                                                   | Generates catalog product recommendations             |
| `POST`   | `/api/ai/knowledge`           | `{ question, category, region }`                                | Answers policy questions with grounded citations      |
| `POST`   | `/api/ai/chat`                | `{ message }`                                                   | Coordinated assistant (orders + inventory + policy)   |
| `GET`    | `/api/ai/audit`               | None                                                            | Explainability & audit trail records                  |

---

## 5. Seeded Demo Accounts

| Name               | Email                 | Password      | Membership Tier | Permissions                                  |
| ------------------ | --------------------- | ------------- | --------------- | -------------------------------------------- |
| **Alice Walker**   | `alice@example.com`   | `password123` | **Prime**       | Seeded with active order in `SHIPPED` status |
| **Bob Smith**      | `bob@example.com`     | `password123` | **Premium**     | Higher budget preference ($2500)             |
| **Charlie Brown**  | `charlie@example.com` | `password123` | **Basic**       | Audio preference ($400)                      |
| **Admin Uploader** | `admin@shopsmart.com` | `admin123`    | **Premium**     | Authorized demo uploader for knowledge docs  |

---

## 6. How to Run the Project

### Prerequisites

- Java 17+
- Node.js 18+ and npm
- (Optional) Docker for PostgreSQL + pgvector container

### Step 1: Start PostgreSQL + pgvector (Optional)

If running against PostgreSQL:

```bash
docker compose up -d
```

_(By default, the backend runs seamlessly out-of-the-box with embedded in-memory database with full PostgreSQL compatibility and seed data)._

### Step 2: Run the Backend

```bash
cd backend
mvn clean spring-boot:run
```

The Spring Boot API will start on `http://localhost:8080`.

#### Enable external AI responses with OpenRouter

The assistant uses grounded catalog, order, inventory, and policy context. To enable OpenRouter, set the key in your shell before starting Spring Boot. Never commit the key to Git:

```bash
export OPENROUTER_API_KEY="your-key"
export OPENROUTER_MODEL="openai/gpt-4o-mini"
export SHOPSMART_AI_ENABLED=true
```

The default endpoint is `https://openrouter.ai/api/v1`. You can choose any OpenRouter model slug with `OPENROUTER_MODEL`. Without a key, the deterministic grounded assistant remains available as a local fallback.

### Step 3: Run the Frontend

```bash
cd frontend
npm install
npm run dev
```

The React + Vite application will start on `http://localhost:5175` (proxied to `http://localhost:8080/api`).

### Step 4: Run Tests

```bash
cd backend
mvn test
```

All 9 end-to-end integration tests execute and verify the 4 stages.
