import React, { useEffect, useState } from "react";
import {
  BrowserRouter,
  Navigate,
  Outlet,
  Route,
  Routes,
  useOutletContext,
  useLocation,
  useNavigate,
} from "react-router-dom";
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
import { NotFoundPage } from "./pages/NotFoundPage";

const workspaceRoutes = [
  { path: "/catalog", label: "Discover" },
  { path: "/orders", label: "Orders" },
  { path: "/wishlist", label: "Wishlist" },
  { path: "/cart", label: "Cart" },
  { path: "/preferences", label: "Preferences" },
  { path: "/audits", label: "AI activity" },
];

function LoadingScreen() {
  return (
    <div className="auth-loading">
      <div className="skeleton auth-loading-mark" />
      <div className="skeleton auth-loading-line" />
    </div>
  );
}

function AuthRoutes() {
  return (
    <Routes>
      <Route path="/signin" element={<AuthPage initialMode="signin" />} />
      <Route path="/signup" element={<AuthPage initialMode="signup" />} />
      <Route path="*" element={<Navigate to="/signin" replace />} />
    </Routes>
  );
}

function WorkspaceLayout() {
  const { user } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();
  const [cartCount, setCartCount] = useState(0);
  const [wishlistIds, setWishlistIds] = useState([]);
  const [compareIds, setCompareIds] = useState([]);
  const [isAssistantOpen, setIsAssistantOpen] = useState(false);
  const [isUploadOpen, setIsUploadOpen] = useState(false);
  const [isCompareOpen, setIsCompareOpen] = useState(false);

  const activeTab = workspaceRoutes.find((route) => location.pathname.startsWith(route.path))?.path.slice(1) || "catalog";
  const setActiveTab = (tab) => navigate(`/${tab}`);

  useEffect(() => {
    if (!user) return;
    api.getCart().then((cart) => setCartCount(cart.itemCount || 0)).catch(() => {});
    api.getWishlist().then((wishlist) => setWishlistIds((wishlist.items || []).map((item) => item.id))).catch(() => {});
  }, [user]);

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
      const updated = wishlistIds.includes(productId)
        ? await api.removeFromWishlist(productId)
        : await api.addToWishlist(productId);
      setWishlistIds((updated.items || []).map((item) => item.id));
    } catch (err) {
      alert(err.message || "Failed to update wishlist");
    }
  };

  const handleToggleCompare = (productId) => {
    setCompareIds((current) => {
      if (current.includes(productId)) return current.filter((id) => id !== productId);
      if (current.length >= 4) {
        alert("You can compare up to 4 products at a time.");
        return current;
      }
      return [...current, productId];
    });
  };

  const handleAskAssistantAboutOrder = () => setIsAssistantOpen(true);

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
      <main className="app-main"><Outlet context={{ handleAddToCart, handleToggleWishlist, wishlistIds, compareIds, handleToggleCompare, setIsCompareOpen, setIsAssistantOpen, handleAskAssistantAboutOrder, setActiveTab }} /></main>
      <AssistantDrawer isOpen={isAssistantOpen} onClose={() => setIsAssistantOpen(false)} onAddToCart={handleAddToCart} />
      <DocumentUploadModal isOpen={isUploadOpen} onClose={() => setIsUploadOpen(false)} />
      {isCompareOpen && <ComparisonModal productIds={compareIds} onClose={() => setIsCompareOpen(false)} onRemoveFromCompare={(id) => setCompareIds((current) => current.filter((item) => item !== id))} onAddToCart={handleAddToCart} />}
    </div>
  );
}

function WorkspaceRoutes() {
  const { user, loading } = useAuth();
  const location = useLocation();
  if (loading && !user) return <LoadingScreen />;
  if (!user) return <AuthRoutes />;
  if (location.pathname === "/signin" || location.pathname === "/signup") {
    return <Navigate to="/catalog" replace />;
  }

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
  const { handleAddToCart, handleToggleWishlist, wishlistIds, compareIds, handleToggleCompare, setIsCompareOpen, setIsAssistantOpen } = useOutletContext();
  return <CatalogPage onAddToCart={handleAddToCart} onToggleWishlist={handleToggleWishlist} onOpenAssistant={() => setIsAssistantOpen(true)} wishlistIds={wishlistIds} compareIds={compareIds} onToggleCompare={handleToggleCompare} onOpenCompare={() => setIsCompareOpen(true)} />;
}

function CartRoute() {
  const { setActiveTab } = useOutletContext();
  return <CartPage onNavigateCatalog={() => setActiveTab("catalog")} />;
}

function WishlistRoute() {
  const { handleAddToCart, setActiveTab } = useOutletContext();
  return <WishlistPage onAddToCart={handleAddToCart} onNavigateCatalog={() => setActiveTab("catalog")} />;
}

function OrdersRoute() {
  const { handleAskAssistantAboutOrder } = useOutletContext();
  return <OrdersPage onAskAssistantAboutOrder={handleAskAssistantAboutOrder} />;
}

export function App() {
  return <BrowserRouter><WorkspaceRoutes /></BrowserRouter>;
}
