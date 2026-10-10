-- ShopSmart AI Database Schema (PostgreSQL + pgvector)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS vector;

-- Customer
CREATE TABLE IF NOT EXISTS customer (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    membership_tier VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- CustomerPreference
CREATE TABLE IF NOT EXISTS customer_preference (
    customer_id UUID PRIMARY KEY REFERENCES customer(id) ON DELETE CASCADE,
    preferred_categories JSONB,
    preferred_brands JSONB,
    budget DECIMAL(12, 2)
);

-- Category
CREATE TABLE IF NOT EXISTS category (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    parent_category_id UUID REFERENCES category(id) ON DELETE SET NULL
);

-- Product
CREATE TABLE IF NOT EXISTS product (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    category_id UUID REFERENCES category(id) ON DELETE SET NULL,
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(12, 2) NOT NULL,
    rating DECIMAL(3, 2) NOT NULL DEFAULT 0.0,
    images JSONB,
    specifications JSONB
);

-- Warehouse
CREATE TABLE IF NOT EXISTS warehouse (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL
);

-- Inventory
CREATE TABLE IF NOT EXISTS inventory (
    product_id UUID REFERENCES product(id) ON DELETE CASCADE,
    warehouse_id UUID REFERENCES warehouse(id) ON DELETE CASCADE,
    available_quantity INT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (product_id, warehouse_id)
);

-- WishlistItem
CREATE TABLE IF NOT EXISTS wishlist_item (
    customer_id UUID REFERENCES customer(id) ON DELETE CASCADE,
    product_id UUID REFERENCES product(id) ON DELETE CASCADE,
    added_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (customer_id, product_id)
);

-- CartItem
CREATE TABLE IF NOT EXISTS cart_item (
    customer_id UUID REFERENCES customer(id) ON DELETE CASCADE,
    product_id UUID REFERENCES product(id) ON DELETE CASCADE,
    quantity INT NOT NULL DEFAULT 1,
    PRIMARY KEY (customer_id, product_id)
);

-- Order
CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    customer_id UUID REFERENCES customer(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL,
    total_amount DECIMAL(12, 2) NOT NULL,
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- OrderItem
CREATE TABLE IF NOT EXISTS order_item (
    order_id UUID REFERENCES orders(id) ON DELETE CASCADE,
    product_id UUID REFERENCES product(id) ON DELETE CASCADE,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(12, 2) NOT NULL,
    PRIMARY KEY (order_id, product_id)
);

-- ShipmentEvent
CREATE TABLE IF NOT EXISTS shipment_event (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID REFERENCES orders(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estimated_delivery TIMESTAMP
);

-- KnowledgeDocument
CREATE TABLE IF NOT EXISTS knowledge_document (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    format VARCHAR(50) NOT NULL,
    version VARCHAR(50) NOT NULL,
    effective_date DATE NOT NULL,
    category VARCHAR(100) NOT NULL,
    region VARCHAR(100) NOT NULL,
    approval_status VARCHAR(50) NOT NULL
);

-- DocumentChunk
CREATE TABLE IF NOT EXISTS document_chunk (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    document_id UUID REFERENCES knowledge_document(id) ON DELETE CASCADE,
    chunk_index INT NOT NULL,
    section VARCHAR(255),
    text TEXT NOT NULL,
    embedding vector(384)
);

-- AuditRecord
CREATE TABLE IF NOT EXISTS audit_record (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    customer_id UUID REFERENCES customer(id) ON DELETE SET NULL,
    request TEXT NOT NULL,
    ai_response TEXT NOT NULL,
    model_name VARCHAR(100) NOT NULL,
    tool_activity JSONB,
    source_references JSONB,
    outcome VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
