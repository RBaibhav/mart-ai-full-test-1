import React, { useState, useEffect } from "react";
import { api } from "../api/client";
import { notify } from "../utils/notify";
import { useAuth } from "../context/AuthContext";
import { Settings, Save, CheckCircle2 } from "lucide-react";

export function PreferencesPage() {
  const { user } = useAuth();
  const [categories, setCategories] = useState("");
  const [brands, setBrands] = useState("");
  const [budget, setBudget] = useState("");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [savedSuccess, setSavedSuccess] = useState(false);

  useEffect(() => {
    setLoading(true);
    api
      .getPreferences()
      .then((data) => {
        setCategories(data.categories ? data.categories.join(", ") : "");
        setBrands(data.brands ? data.brands.join(", ") : "");
        setBudget(data.budget ? String(data.budget) : "");
      })
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  }, [user]);

  const handleSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    setSavedSuccess(false);

    try {
      const catList = categories
        .split(",")
        .map((s) => s.trim())
        .filter(Boolean);
      const brandList = brands
        .split(",")
        .map((s) => s.trim())
        .filter(Boolean);
      const budgetVal = budget ? parseFloat(budget) : null;

      await api.updatePreferences({
        categories: catList,
        brands: brandList,
        budget: budgetVal,
      });

      setSavedSuccess(true);
      setTimeout(() => setSavedSuccess(false), 3000);
    } catch (err) {
      notify(err.message || "Failed to update preferences", "error");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div style={{ padding: "2rem 0" }}>
      <div className="container" style={{ maxWidth: "640px" }}>
        <h1
          style={{
            fontSize: "1.75rem",
            fontWeight: 800,
            marginBottom: "0.5rem",
            display: "flex",
            alignItems: "center",
            gap: "0.5rem",
          }}
        >
          <Settings size={26} color="var(--primary)" /> Shopping Preferences
        </h1>
        <p
          style={{
            color: "var(--text-muted)",
            fontSize: "0.9rem",
            marginBottom: "1.5rem",
          }}
        >
          Personalize recommendations by defining your favorite categories, brands, and target
          shopping budget.
        </p>

        <div className="card">
          {loading ? (
            <div
              style={{
                padding: "2rem",
                textAlign: "center",
                color: "var(--text-muted)",
              }}
            >
              Loading preferences...
            </div>
          ) : (
            <form
              onSubmit={handleSave}
              style={{
                display: "flex",
                flexDirection: "column",
                gap: "1.25rem",
              }}
            >
              <div>
                <label
                  style={{
                    display: "block",
                    fontSize: "0.85rem",
                    fontWeight: 600,
                    marginBottom: "0.35rem",
                  }}
                >
                  Preferred Categories (Comma-separated)
                </label>
                <input
                  type="text"
                  placeholder="e.g. Laptops, Audio, Displays"
                  value={categories}
                  onChange={(e) => setCategories(e.target.value)}
                  className="input"
                />
                <span
                  style={{
                    fontSize: "0.75rem",
                    color: "var(--text-muted)",
                    marginTop: "0.2rem",
                    display: "block",
                  }}
                >
                  Used by AI assistant when interpreting shopping requirements.
                </span>
              </div>

              <div>
                <label
                  style={{
                    display: "block",
                    fontSize: "0.85rem",
                    fontWeight: 600,
                    marginBottom: "0.35rem",
                  }}
                >
                  Preferred Brands (Comma-separated)
                </label>
                <input
                  type="text"
                  placeholder="e.g. Dell, Apple, Sony, Bose"
                  value={brands}
                  onChange={(e) => setBrands(e.target.value)}
                  className="input"
                />
              </div>

              <div>
                <label
                  style={{
                    display: "block",
                    fontSize: "0.85rem",
                    fontWeight: 600,
                    marginBottom: "0.35rem",
                  }}
                >
                  Default Budget ($ USD)
                </label>
                <input
                  type="number"
                  placeholder="e.g. 1000.00"
                  value={budget}
                  onChange={(e) => setBudget(e.target.value)}
                  className="input"
                  min="0"
                  step="0.01"
                />
                <span
                  style={{
                    fontSize: "0.75rem",
                    color: "var(--text-muted)",
                    marginTop: "0.2rem",
                    display: "block",
                  }}
                >
                  Applied as constraint when recommendations request does not specify a budget.
                </span>
              </div>

              {savedSuccess && (
                <div
                  style={{
                    backgroundColor: "var(--success-bg)",
                    color: "var(--success)",
                    border: "1px solid #bbf7d0",
                    padding: "0.75rem",
                    borderRadius: "6px",
                    display: "flex",
                    alignItems: "center",
                    gap: "0.5rem",
                    fontSize: "0.85rem",
                  }}
                >
                  <CheckCircle2 size={16} /> Preferences successfully updated!
                </div>
              )}

              <button
                type="submit"
                className="btn btn-primary"
                disabled={saving}
                style={{ marginTop: "0.5rem" }}
              >
                <Save size={16} />
                {saving ? "Saving..." : "Save Preferences"}
              </button>
            </form>
          )}
        </div>
      </div>
    </div>
  );
}
