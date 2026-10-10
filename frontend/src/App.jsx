import React, { useState, useEffect } from "react";
import { useAuth } from "./context/AuthContext";
import { api } from "./api/client";
import { Navbar } from "./components/Navbar";
import { AssistantDrawer } from "./components/AssistantDrawer";
import { DocumentUploadModal } from "./components/DocumentUploadModal";
import { ComparisonModal } from "./components/ComparisonModal";
import { CatalogPage } from "./pages/CatalogPage";
import { CartPage } from "./pages/CartPage";
import { WishlistPage } from "./pages/WishlistPage";
import { OrdersPage } from "./pages/OrdersPage";
import { PreferencesPage } from "./pages/PreferencesPage";
import { AuditPage } from "./pages/AuditPage";
import { AuthPage } from "./pages/AuthPage";

export function App() {
  const { user, loading } = useAuth();
  const [activeTab, setActiveTab] = useState("catalog");

  // Badge counters & selections
  const [cartCount, setCartCount] = useState(0);
  const [wishlistIds, setWishlistIds] = useState([]);
  const [compareIds, setCompareIds] = useState([]);

  // Modals & Drawers
  const [isAssistantOpen, setIsAssistantOpen] = useState(false);
  const [isUploadOpen, setIsUploadOpen] = useState(false);
  const [isCompareOpen, setIsCompareOpen] = useState(false);

  // Load initial cart and wishlist states
  const refreshCart = () => {
    api
      .getCart()
      .then((c) => setCartCount(c.itemCount || 0))
      .catch(() => {});
  };

  const refreshWishlist = () => {
    api
      .getWishlist()
      .then((w) => setWishlistIds((w.items || []).map((i) => i.id)))
      .catch(() => {});
  };

  useEffect(() => {
    if (user) {
      refreshCart();
      refreshWishlist();
    }
  }, [user]);

  // Handlers
  const handleAddToCart = async (productId, quantity = 1) => {
    try {
      const updated = await api.addToCart(productId, quantity);
      setCartCount(updated.itemCount || 0);
      alert("Product added to cart!");
    } catch (err) {
      alert(err.message || "Failed to add product to cart");
    }
  };

  const handleToggleWishlist = async (productId) => {
    try {
      if (wishlistIds.includes(productId)) {
        const updated = await api.removeFromWishlist(productId);
        setWishlistIds((updated.items || []).map((i) => i.id));
      } else {
        const updated = await api.addToWishlist(productId);
        setWishlistIds((updated.items || []).map((i) => i.id));
      }
    } catch (err) {
      alert(err.message || "Failed to update wishlist");
    }
  };

  const handleToggleCompare = (productId) => {
    setCompareIds((prev) => {
      if (prev.includes(productId)) {
        return prev.filter((id) => id !== productId);
      }
      if (prev.length >= 4) {
        alert("You can compare up to 4 products at a time.");
        return prev;
      }
      return [...prev, productId];
    });
  };

  const handleRemoveFromCompare = (productId) => {
    setCompareIds((prev) => prev.filter((id) => id !== productId));
  };

  const handleAskAssistantAboutOrder = (prompt) => {
    setIsAssistantOpen(true);
  };

  if (loading && !user) {
    return (
      <div className="auth-loading">
        <div className="skeleton auth-loading-mark" />
        <div className="skeleton auth-loading-line" />
      </div>
    );
  }

  if (!user) {
    return <AuthPage />;
  }

  return (
    <div
      style={{ minHeight: "100vh", display: "flex", flexDirection: "column" }}
    >
      <Navbar
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        cartCount={cartCount}
        wishlistCount={wishlistIds.length}
        onOpenAssistant={() => setIsAssistantOpen(true)}
        onOpenUpload={() => setIsUploadOpen(true)}
      />

      <main style={{ flex: 1 }}>
        {activeTab === "catalog" && (
          <CatalogPage
            onAddToCart={handleAddToCart}
            onToggleWishlist={handleToggleWishlist}
            onOpenAssistant={() => setIsAssistantOpen(true)}
            wishlistIds={wishlistIds}
            compareIds={compareIds}
            onToggleCompare={handleToggleCompare}
            onOpenCompare={() => setIsCompareOpen(true)}
          />
        )}
        {activeTab === "cart" && (
          <CartPage onNavigateCatalog={() => setActiveTab("catalog")} />
        )}
        {activeTab === "wishlist" && (
          <WishlistPage
            onAddToCart={handleAddToCart}
            onNavigateCatalog={() => setActiveTab("catalog")}
          />
        )}
        {activeTab === "orders" && (
          <OrdersPage onAskAssistantAboutOrder={handleAskAssistantAboutOrder} />
        )}
        {activeTab === "preferences" && <PreferencesPage />}
        {activeTab === "audits" && <AuditPage />}
      </main>

      {/* Global AI Assistant Drawer */}
      <AssistantDrawer
        isOpen={isAssistantOpen}
        onClose={() => setIsAssistantOpen(false)}
        onAddToCart={handleAddToCart}
      />

      {/* Document Upload Modal */}
      <DocumentUploadModal
        isOpen={isUploadOpen}
        onClose={() => setIsUploadOpen(false)}
      />

      {/* Side-by-Side Comparison Modal */}
      {isCompareOpen && (
        <ComparisonModal
          productIds={compareIds}
          onClose={() => setIsCompareOpen(false)}
          onRemoveFromCompare={handleRemoveFromCompare}
          onAddToCart={handleAddToCart}
        />
      )}
    </div>
  );
}
