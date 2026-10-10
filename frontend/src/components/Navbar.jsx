import React, { useState } from "react";
import { useAuth, DEMO_ACCOUNTS } from "../context/AuthContext";
import {
  ShoppingBag,
  Heart,
  ShoppingCart,
  Package,
  Settings,
  FileText,
  Sparkles,
  ShieldCheck,
  Menu,
  X,
  LogOut,
} from "lucide-react";

export function Navbar({
  activeTab,
  setActiveTab,
  cartCount,
  wishlistCount,
  onOpenAssistant,
  onOpenUpload,
}) {
  const { user, switchAccount, logout } = useAuth();
  const [mobileOpen, setMobileOpen] = useState(false);
  const navigation = [
    { id: "catalog", label: "Discover", icon: ShoppingBag },
    { id: "orders", label: "Orders", icon: Package },
    { id: "wishlist", label: "Wishlist", icon: Heart, count: wishlistCount },
    { id: "cart", label: "Cart", icon: ShoppingCart, count: cartCount },
    { id: "preferences", label: "Preferences", icon: Settings },
    { id: "audits", label: "AI activity", icon: ShieldCheck },
  ];

  const navigate = (tab) => {
    setActiveTab(tab);
    setMobileOpen(false);
  };

  return (
    <header className="app-header">
      <div className="container header-inner">
        <button
          className="brand"
          onClick={() => navigate("catalog")}
          aria-label="Go to catalog"
        >
          <span className="brand-mark">
            <ShoppingBag size={19} />
          </span>
          <span>
            ShopSmart <b>AI</b>
          </span>
        </button>

        <nav className={`main-nav ${mobileOpen ? "is-open" : ""}`}>
          <div className="mobile-nav-head">
            <span>Workspace</span>
            <button
              className="icon-button"
              onClick={() => setMobileOpen(false)}
            >
              <X size={18} />
            </button>
          </div>
          {navigation.map(({ id, label, icon: Icon, count }) => (
            <button
              key={id}
              className={`nav-link ${activeTab === id ? "active" : ""}`}
              onClick={() => navigate(id)}
            >
              <Icon size={16} /> <span>{label}</span>
              {count > 0 && <small>{count}</small>}
            </button>
          ))}
        </nav>

        <div className="header-actions">
          <button
            className="btn btn-primary assistant-button"
            onClick={onOpenAssistant}
          >
            <Sparkles size={16} /> Ask AI
          </button>
          <button
            className="icon-button upload-button"
            onClick={onOpenUpload}
            title="Upload knowledge document"
          >
            <FileText size={17} />
          </button>
          <div className="account-menu">
            <div className="avatar">{user?.name?.slice(0, 1) || "S"}</div>
            <div className="account-copy">
              <strong>{user?.name || "Sign In"}</strong>
              <span>{user?.membershipTier || "Customer"}</span>
            </div>
            <select
              aria-label="Switch demo account"
              value={user?.email || ""}
              onChange={(e) => {
                const target = DEMO_ACCOUNTS.find(
                  (acc) => acc.email === e.target.value,
                );
                if (target) switchAccount(target);
              }}
            >
              <option disabled value="">
                Switch account
              </option>
              {DEMO_ACCOUNTS.map((acc) => (
                <option key={acc.email} value={acc.email}>
                  {acc.name} ({acc.role})
                </option>
              ))}
            </select>
            <button
              className="icon-button logout-button"
              onClick={logout}
              title="Sign out"
            >
              <LogOut size={16} />
            </button>
          </div>
          <button
            className="icon-button mobile-menu-button"
            onClick={() => setMobileOpen((open) => !open)}
            aria-label="Open navigation"
          >
            <Menu size={19} />
          </button>
        </div>
      </div>
    </header>
  );
}
