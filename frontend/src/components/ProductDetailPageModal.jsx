import React, { useEffect, useState } from "react";
import { api } from "../api/client";
import { X, Star, ShoppingCart, Heart, Warehouse } from "lucide-react";

export function ProductDetailPageModal({
  product,
  onClose,
  onAddToCart,
  onToggleWishlist,
  isWishlisted,
}) {
  const [inventoryData, setInventoryData] = useState(null);
  const [loadingStock, setLoadingStock] = useState(true);

  useEffect(() => {
    if (product) {
      setLoadingStock(true);
      api
        .getInventory(product.id)
        .then((res) => setInventoryData(res))
        .catch((err) => console.error(err))
        .finally(() => setLoadingStock(false));
    }
  }, [product]);

  if (!product) return null;

  const isOutOfStock = product.availability === "OUT_OF_STOCK";
  const imageUrl =
    product.images?.[0] || "https://images.unsplash.com/photo-1526738549149-8e07eca6c147?w=600";
  const specs = product.specifications || {};

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div
        className="modal-content"
        style={{ maxWidth: "800px" }}
        onClick={(e) => e.stopPropagation()}
      >
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "flex-start",
            marginBottom: "1rem",
          }}
        >
          <div>
            <span
              style={{
                fontSize: "0.8rem",
                color: "var(--text-muted)",
                fontWeight: 600,
                textTransform: "uppercase",
              }}
            >
              {product.brand} • {product.categoryName || "General"}
            </span>
            <h2
              style={{
                fontSize: "1.4rem",
                fontWeight: 700,
                margin: "0.2rem 0",
              }}
            >
              {product.name}
            </h2>
            <div
              style={{
                display: "flex",
                alignItems: "center",
                gap: "0.5rem",
                marginTop: "0.25rem",
              }}
            >
              <div
                style={{
                  display: "flex",
                  alignItems: "center",
                  gap: "0.2rem",
                  fontSize: "0.85rem",
                  fontWeight: 600,
                }}
              >
                <Star size={15} fill="#eab308" color="#eab308" />
                <span>{product.rating ? Number(product.rating).toFixed(1) : "4.5"}</span>
              </div>
              <span className={`badge ${isOutOfStock ? "badge-stock-out" : "badge-stock-in"}`}>
                {isOutOfStock ? "Out of Stock" : "In Stock"}
              </span>
            </div>
          </div>
          <button className="btn btn-secondary btn-sm" onClick={onClose}>
            <X size={18} />
          </button>
        </div>

        <div
          style={{
            display: "grid",
            gridTemplateColumns: "1fr 1fr",
            gap: "1.5rem",
            marginBottom: "1.5rem",
          }}
        >
          {/* Image */}
          <div
            style={{
              height: "280px",
              borderRadius: "8px",
              overflow: "hidden",
              backgroundColor: "#f1f5f9",
            }}
          >
            <img
              src={imageUrl}
              alt={product.name}
              style={{ width: "100%", height: "100%", objectFit: "cover" }}
            />
          </div>

          {/* Details & Actions */}
          <div style={{ display: "flex", flexDirection: "column" }}>
            <p
              style={{
                fontSize: "0.9rem",
                color: "var(--text-muted)",
                marginBottom: "1rem",
              }}
            >
              {product.description}
            </p>

            <div
              style={{
                fontSize: "1.8rem",
                fontWeight: 800,
                color: "var(--text-main)",
                marginBottom: "1rem",
              }}
            >
              ${Number(product.price).toFixed(2)}
            </div>

            <div style={{ display: "flex", gap: "0.75rem", marginTop: "auto" }}>
              <button
                className="btn btn-primary"
                style={{ flex: 1 }}
                disabled={isOutOfStock}
                onClick={() => {
                  onAddToCart(product.id, 1);
                  onClose();
                }}
              >
                <ShoppingCart size={16} />
                {isOutOfStock ? "Out of Stock" : "Add to Cart"}
              </button>
              <button
                className="btn btn-secondary"
                onClick={() => onToggleWishlist(product.id)}
                style={{
                  color: isWishlisted ? "var(--danger)" : "var(--text-main)",
                }}
              >
                <Heart size={16} fill={isWishlisted ? "var(--danger)" : "none"} />
              </button>
            </div>
          </div>
        </div>

        {/* Technical Specifications */}
        <div style={{ marginBottom: "1.5rem" }}>
          <h3
            style={{
              fontSize: "1rem",
              fontWeight: 700,
              marginBottom: "0.75rem",
              borderBottom: "1px solid var(--border)",
              paddingBottom: "0.4rem",
            }}
          >
            Technical Specifications
          </h3>
          <div
            style={{
              display: "grid",
              gridTemplateColumns: "repeat(auto-fill, minmax(220px, 1fr))",
              gap: "0.5rem",
            }}
          >
            {Object.entries(specs).map(([k, v]) => (
              <div
                key={k}
                style={{
                  backgroundColor: "#f8fafc",
                  padding: "0.5rem 0.75rem",
                  borderRadius: "6px",
                  fontSize: "0.8rem",
                }}
              >
                <span
                  style={{
                    color: "var(--text-muted)",
                    display: "block",
                    fontSize: "0.75rem",
                  }}
                >
                  {k}
                </span>
                <span style={{ fontWeight: 600 }}>{String(v)}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Central Warehouse Stock Breakdown */}
        <div>
          <h3
            style={{
              fontSize: "1rem",
              fontWeight: 700,
              marginBottom: "0.75rem",
              borderBottom: "1px solid var(--border)",
              paddingBottom: "0.4rem",
              display: "flex",
              alignItems: "center",
              gap: "0.4rem",
            }}
          >
            <Warehouse size={16} /> Central Warehouse Inventory
          </h3>
          {loadingStock ? (
            <div style={{ fontSize: "0.85rem", color: "var(--text-muted)" }}>
              Querying internal warehouse API...
            </div>
          ) : inventoryData?.warehouses?.length > 0 ? (
            <div
              style={{
                display: "flex",
                flexDirection: "column",
                gap: "0.4rem",
              }}
            >
              {inventoryData.warehouses.map((wh) => (
                <div
                  key={wh.warehouseId}
                  style={{
                    display: "flex",
                    justifyContent: "space-between",
                    padding: "0.4rem 0.6rem",
                    backgroundColor: "#f8fafc",
                    borderRadius: "6px",
                    fontSize: "0.8rem",
                  }}
                >
                  <span>
                    <strong>{wh.name}</strong> ({wh.location})
                  </span>
                  <span
                    style={{
                      fontWeight: 600,
                      color: wh.availableQuantity > 0 ? "var(--success)" : "var(--danger)",
                    }}
                  >
                    {wh.availableQuantity} units available
                  </span>
                </div>
              ))}
            </div>
          ) : (
            <div style={{ fontSize: "0.85rem", color: "var(--text-muted)" }}>
              No warehouse stock records found.
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
