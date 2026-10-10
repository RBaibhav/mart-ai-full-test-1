import React, { useEffect, useState } from "react";
import { useLocation, useNavigate, Outlet } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { api } from "../api/client";
import { Navbar } from "../components/Navbar";
import { AssistantDrawer } from "../components/AssistantDrawer";
import { DocumentUploadModal } from "../components/DocumentUploadModal";
import { ComparisonModal } from "../components/ComparisonModal";
import { ToastHost } from "../components/ToastHost";
import { notify } from "../utils/notify";

const workspaceRoutes = ["/catalog", "/orders", "/wishlist", "/cart", "/preferences", "/audits"];

export function WorkspaceLayout() {
  const { user } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();
  const [cartCount, setCartCount] = useState(0);
  const [wishlistIds, setWishlistIds] = useState([]);
  const [compareIds, setCompareIds] = useState([]);
  const [isAssistantOpen, setIsAssistantOpen] = useState(false);
  const [isUploadOpen, setIsUploadOpen] = useState(false);
  const [isCompareOpen, setIsCompareOpen] = useState(false);

  const activeTab =
    workspaceRoutes.find((path) => location.pathname.startsWith(path))?.slice(1) || "catalog";
  const setActiveTab = (tab) => navigate(`/${tab}`);

  useEffect(() => {
    if (!user) return;
    api
      .getCart()
      .then((cart) => setCartCount(cart.itemCount || 0))
      .catch(() => {});
    api
      .getWishlist()
      .then((wishlist) => setWishlistIds((wishlist.items || []).map((item) => item.id)))
      .catch(() => {});
  }, [user]);

  const handleAddToCart = async (productId, quantity = 1) => {
    try {
      const updated = await api.addToCart(productId, quantity);
      setCartCount(updated.itemCount || 0);
      notify("Product added to cart.", "success");
    } catch (err) {
      notify(err.message || "Failed to add product to cart", "error");
    }
  };

  const handleToggleWishlist = async (productId) => {
    try {
      const updated = wishlistIds.includes(productId)
        ? await api.removeFromWishlist(productId)
        : await api.addToWishlist(productId);
      setWishlistIds((updated.items || []).map((item) => item.id));
    } catch (err) {
      notify(err.message || "Failed to update wishlist", "error");
    }
  };

  const handleToggleCompare = (productId) => {
    setCompareIds((current) => {
      if (current.includes(productId)) return current.filter((id) => id !== productId);
      if (current.length >= 4) {
        notify("You can compare up to 4 products at a time.", "info");
        return current;
      }
      return [...current, productId];
    });
  };

  const outletContext = {
    handleAddToCart,
    handleToggleWishlist,
    wishlistIds,
    compareIds,
    handleToggleCompare,
    openComparison: () => setIsCompareOpen(true),
    openAssistant: () => setIsAssistantOpen(true),
    handleAskAssistantAboutOrder: () => setIsAssistantOpen(true),
    setActiveTab,
  };

  return (
    <div className="app-frame">
      <Navbar
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        cartCount={cartCount}
        wishlistCount={wishlistIds.length}
        onOpenAssistant={() => setIsAssistantOpen(true)}
        onOpenUpload={() => setIsUploadOpen(true)}
      />
      <main className="app-main">
        <Outlet context={outletContext} />
      </main>
      <AssistantDrawer
        isOpen={isAssistantOpen}
        onClose={() => setIsAssistantOpen(false)}
        onAddToCart={handleAddToCart}
      />
      <DocumentUploadModal isOpen={isUploadOpen} onClose={() => setIsUploadOpen(false)} />
      {isCompareOpen && (
        <ComparisonModal
          productIds={compareIds}
          onClose={() => setIsCompareOpen(false)}
          onRemoveFromCompare={(id) =>
            setCompareIds((current) => current.filter((item) => item !== id))
          }
          onAddToCart={handleAddToCart}
        />
      )}
      <ToastHost />
    </div>
  );
}
