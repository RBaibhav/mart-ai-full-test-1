import React from "react";
import { Navigate, Route, Routes, useOutletContext } from "react-router-dom";
import { WorkspaceLayout } from "./WorkspaceLayout";
import { CatalogPage } from "../pages/CatalogPage";
import { CartPage } from "../pages/CartPage";
import { WishlistPage } from "../pages/WishlistPage";
import { OrdersPage } from "../pages/OrdersPage";
import { PreferencesPage } from "../pages/PreferencesPage";
import { AuditPage } from "../pages/AuditPage";
import { NotFoundPage } from "../pages/NotFoundPage";

export function WorkspaceRoutes() {
  return (
    <Routes>
      <Route element={<WorkspaceLayout />}>
        <Route index element={<Navigate to="/catalog" replace />} />
        <Route path="catalog" element={<CatalogRoute />} />
        <Route path="cart" element={<CartRoute />} />
        <Route path="wishlist" element={<WishlistRoute />} />
        <Route path="orders" element={<OrdersRoute />} />
        <Route path="preferences" element={<PreferencesPage />} />
        <Route path="audits" element={<AuditPage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  );
}

function CatalogRoute() {
  const {
    handleAddToCart,
    handleToggleWishlist,
    wishlistIds,
    compareIds,
    handleToggleCompare,
    openComparison,
    openAssistant,
  } = useOutletContext();
  return (
    <CatalogPage
      onAddToCart={handleAddToCart}
      onToggleWishlist={handleToggleWishlist}
      onOpenAssistant={openAssistant}
      wishlistIds={wishlistIds}
      compareIds={compareIds}
      onToggleCompare={handleToggleCompare}
      onOpenCompare={openComparison}
    />
  );
}

function CartRoute() {
  const { setActiveTab } = useOutletContext();
  return <CartPage onNavigateCatalog={() => setActiveTab("catalog")} />;
}

function WishlistRoute() {
  const { handleAddToCart, setActiveTab } = useOutletContext();
  return (
    <WishlistPage onAddToCart={handleAddToCart} onNavigateCatalog={() => setActiveTab("catalog")} />
  );
}

function OrdersRoute() {
  const { handleAskAssistantAboutOrder } = useOutletContext();
  return <OrdersPage onAskAssistantAboutOrder={handleAskAssistantAboutOrder} />;
}
