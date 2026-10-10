import React, { useState, useEffect } from "react";
import { api } from "../api/client";
import { notify } from "../utils/notify";
import { Heart, Trash2, ShoppingCart } from "lucide-react";

export function WishlistPage({ onAddToCart, onNavigateCatalog }) {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchWishlist = () => {
    setLoading(true);
    api
      .getWishlist()
      .then((res) => setItems(res.items || []))
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchWishlist();
  }, []);

  const handleRemove = async (productId) => {
    try {
      const res = await api.removeFromWishlist(productId);
      setItems(res.items || []);
    } catch (err) {
      notify(err.message || "Failed to remove from wishlist", "error");
    }
  };

  return (
    <div style={{ padding: "2rem 0" }}>
      <div className="container">
        <h1
          style={{
            fontSize: "1.75rem",
            fontWeight: 800,
            marginBottom: "1.5rem",
            display: "flex",
            alignItems: "center",
            gap: "0.5rem",
          }}
        >
          <Heart size={26} color="var(--danger)" /> My Saved Wishlist ({items.length})
        </h1>

        {loading ? (
          <div
            className="card"
            style={{
              padding: "3rem",
              textAlign: "center",
              color: "var(--text-muted)",
            }}
          >
            Loading wishlist...
          </div>
        ) : items.length === 0 ? (
          <div className="card" style={{ padding: "4rem 2rem", textAlign: "center" }}>
            <h2
              style={{
                fontSize: "1.25rem",
                fontWeight: 700,
                marginBottom: "0.5rem",
              }}
            >
              Your wishlist is empty
            </h2>
            <p
              style={{
                color: "var(--text-muted)",
                fontSize: "0.9rem",
                marginBottom: "1.5rem",
              }}
            >
              Save favorite products to your wishlist while browsing.
            </p>
            <button className="btn btn-primary" onClick={onNavigateCatalog}>
              Explore Catalog
            </button>
          </div>
        ) : (
          <div
            style={{
              display: "grid",
              gridTemplateColumns: "repeat(auto-fill, minmax(280px, 1fr))",
              gap: "1.5rem",
            }}
          >
            {items.map((product) => (
              <div
                key={product.id}
                className="card"
                style={{ display: "flex", flexDirection: "column" }}
              >
                <div
                  style={{
                    height: "180px",
                    borderRadius: "6px",
                    overflow: "hidden",
                    backgroundColor: "#f1f5f9",
                    marginBottom: "0.75rem",
                  }}
                >
                  <img
                    src={
                      product.images?.[0] ||
                      "https://images.unsplash.com/photo-1526738549149-8e07eca6c147?w=600"
                    }
                    alt={product.name}
                    style={{
                      width: "100%",
                      height: "100%",
                      objectFit: "cover",
                    }}
                  />
                </div>

                <span
                  style={{
                    fontSize: "0.75rem",
                    color: "var(--text-muted)",
                    fontWeight: 600,
                    textTransform: "uppercase",
                  }}
                >
                  {product.brand}
                </span>
                <h3
                  style={{
                    fontSize: "1rem",
                    fontWeight: 600,
                    margin: "0.2rem 0 0.5rem",
                  }}
                >
                  {product.name}
                </h3>

                <div
                  style={{
                    fontSize: "1.2rem",
                    fontWeight: 700,
                    color: "var(--primary)",
                    marginBottom: "1rem",
                  }}
                >
                  ${Number(product.price).toFixed(2)}
                </div>

                <div style={{ display: "flex", gap: "0.5rem", marginTop: "auto" }}>
                  <button
                    className="btn btn-primary btn-sm"
                    style={{ flex: 1 }}
                    disabled={product.availability === "OUT_OF_STOCK"}
                    onClick={() => onAddToCart(product.id, 1)}
                  >
                    <ShoppingCart size={14} /> Add to Cart
                  </button>
                  <button
                    className="btn btn-danger btn-sm"
                    onClick={() => handleRemove(product.id)}
                    title="Remove from wishlist"
                  >
                    <Trash2 size={14} />
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
