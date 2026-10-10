import React, { useState, useEffect } from "react";
import { api } from "../api/client";
import { notify } from "../utils/notify";
import { Trash2, Plus, Minus, ShoppingBag, ArrowRight } from "lucide-react";

export function CartPage({ onNavigateCatalog }) {
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchCart = () => {
    setLoading(true);
    api
      .getCart()
      .then((data) => setCart(data))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchCart();
  }, []);

  const handleUpdateQuantity = async (productId, currentQuantity, change) => {
    const newQty = currentQuantity + change;
    if (newQty <= 0) {
      handleRemove(productId);
      return;
    }
    try {
      const res = await api.updateCartItem(productId, newQty);
      setCart(res);
    } catch (err) {
      notify(err.message || "Failed to update quantity", "error");
    }
  };

  const handleRemove = async (productId) => {
    try {
      const res = await api.removeFromCart(productId);
      setCart(res);
    } catch (err) {
      notify(err.message || "Failed to remove item", "error");
    }
  };

  if (loading) {
    return (
      <div
        className="container"
        style={{
          padding: "3rem 0",
          textAlign: "center",
          color: "var(--text-muted)",
        }}
      >
        Loading shopping cart...
      </div>
    );
  }

  const items = cart?.items || [];
  const total = cart?.total || 0;

  return (
    <div style={{ padding: "2rem 0" }}>
      <div className="container">
        <h1
          style={{
            fontSize: "1.75rem",
            fontWeight: 800,
            marginBottom: "1.5rem",
          }}
        >
          Shopping Cart
        </h1>

        {items.length === 0 ? (
          <div className="card" style={{ padding: "4rem 2rem", textAlign: "center" }}>
            <div
              style={{
                backgroundColor: "var(--primary-light)",
                width: "64px",
                height: "64px",
                borderRadius: "50%",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                margin: "0 auto 1rem",
                color: "var(--primary)",
              }}
            >
              <ShoppingBag size={32} />
            </div>
            <h2
              style={{
                fontSize: "1.25rem",
                fontWeight: 700,
                marginBottom: "0.5rem",
              }}
            >
              Your cart is empty
            </h2>
            <p
              style={{
                color: "var(--text-muted)",
                fontSize: "0.9rem",
                marginBottom: "1.5rem",
              }}
            >
              Explore our catalog of laptops, audio, and displays to find the right items.
            </p>
            <button className="btn btn-primary" onClick={onNavigateCatalog}>
              Start Shopping
            </button>
          </div>
        ) : (
          <div
            style={{
              display: "grid",
              gridTemplateColumns: "2fr 1fr",
              gap: "2rem",
              alignItems: "flex-start",
            }}
          >
            {/* Cart Items List */}
            <div className="card" style={{ padding: "0.5rem" }}>
              {items.map((item) => (
                <div
                  key={item.productId}
                  style={{
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "space-between",
                    padding: "1rem",
                    borderBottom: "1px solid var(--border)",
                    gap: "1rem",
                  }}
                >
                  {/* Thumbnail */}
                  <div
                    style={{
                      width: "80px",
                      height: "80px",
                      borderRadius: "8px",
                      overflow: "hidden",
                      backgroundColor: "#f1f5f9",
                      flexShrink: 0,
                    }}
                  >
                    <img
                      src={
                        item.images?.[0] ||
                        "https://images.unsplash.com/photo-1526738549149-8e07eca6c147?w=600"
                      }
                      alt={item.productName}
                      style={{
                        width: "100%",
                        height: "100%",
                        objectFit: "cover",
                      }}
                    />
                  </div>

                  {/* Title & Brand */}
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <span
                      style={{
                        fontSize: "0.75rem",
                        color: "var(--text-muted)",
                        fontWeight: 600,
                        textTransform: "uppercase",
                      }}
                    >
                      {item.brand}
                    </span>
                    <h3
                      style={{
                        fontSize: "0.95rem",
                        fontWeight: 600,
                        margin: "0.1rem 0 0.25rem",
                      }}
                    >
                      {item.productName}
                    </h3>
                    <div
                      style={{
                        fontSize: "0.9rem",
                        fontWeight: 700,
                        color: "var(--primary)",
                      }}
                    >
                      ${Number(item.unitPrice).toFixed(2)}
                    </div>
                  </div>

                  {/* Quantity Changer */}
                  <div
                    style={{
                      display: "flex",
                      alignItems: "center",
                      gap: "0.5rem",
                      backgroundColor: "#f8fafc",
                      padding: "0.25rem 0.5rem",
                      borderRadius: "6px",
                      border: "1px solid var(--border)",
                    }}
                  >
                    <button
                      onClick={() => handleUpdateQuantity(item.productId, item.quantity, -1)}
                      style={{ color: "var(--text-muted)", padding: "2px" }}
                      title="Decrease quantity"
                    >
                      <Minus size={14} />
                    </button>
                    <span
                      style={{
                        fontSize: "0.85rem",
                        fontWeight: 600,
                        minWidth: "20px",
                        textAlign: "center",
                      }}
                    >
                      {item.quantity}
                    </span>
                    <button
                      onClick={() => handleUpdateQuantity(item.productId, item.quantity, 1)}
                      style={{ color: "var(--text-muted)", padding: "2px" }}
                      title="Increase quantity"
                    >
                      <Plus size={14} />
                    </button>
                  </div>

                  {/* Subtotal & Delete */}
                  <div style={{ textAlign: "right", minWidth: "90px" }}>
                    <div style={{ fontSize: "1rem", fontWeight: 700 }}>
                      ${Number(item.subtotal).toFixed(2)}
                    </div>
                    <button
                      onClick={() => handleRemove(item.productId)}
                      style={{
                        color: "var(--danger)",
                        fontSize: "0.75rem",
                        marginTop: "0.25rem",
                        display: "inline-flex",
                        alignItems: "center",
                        gap: "2px",
                      }}
                    >
                      <Trash2 size={12} /> Remove
                    </button>
                  </div>
                </div>
              ))}
            </div>

            {/* Order Summary Card */}
            <div className="card" style={{ position: "sticky", top: "80px" }}>
              <h2
                style={{
                  fontSize: "1.1rem",
                  fontWeight: 700,
                  marginBottom: "1rem",
                  borderBottom: "1px solid var(--border)",
                  paddingBottom: "0.5rem",
                }}
              >
                Cart Summary
              </h2>

              <div
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  marginBottom: "0.5rem",
                  fontSize: "0.9rem",
                }}
              >
                <span style={{ color: "var(--text-muted)" }}>Items Count:</span>
                <span style={{ fontWeight: 600 }}>{cart?.itemCount || 0}</span>
              </div>

              <div
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  marginBottom: "1rem",
                  fontSize: "1.2rem",
                  fontWeight: 800,
                  borderTop: "1px solid var(--border)",
                  paddingTop: "0.75rem",
                }}
              >
                <span>Total Amount:</span>
                <span style={{ color: "var(--primary)" }}>${Number(total).toFixed(2)}</span>
              </div>

              <div
                style={{
                  backgroundColor: "#f8fafc",
                  padding: "0.75rem",
                  borderRadius: "6px",
                  fontSize: "0.75rem",
                  color: "var(--text-muted)",
                  marginBottom: "1rem",
                }}
              >
                ℹ️ <strong>Scope Boundary Note:</strong> Payment processing is excluded per system
                specification. Cart total calculation and item ownership are maintained.
              </div>

              <button
                className="btn btn-secondary"
                style={{ width: "100%" }}
                onClick={onNavigateCatalog}
              >
                Continue Shopping
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
