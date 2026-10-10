import React from "react";
import { Filter, X, ChevronRight, RotateCcw } from "lucide-react";

export function FilterSidebar({
  categories,
  selectedCategory,
  onSelectCategory,
  selectedBrand,
  onSelectBrand,
  priceMin,
  setPriceMin,
  priceMax,
  setPriceMax,
  onResetFilters,
}) {
  const brands = ["Dell", "Apple", "Lenovo", "Sony", "Bose"];

  return (
    <aside style={{ width: "260px", flexShrink: 0 }}>
      <div className="card" style={{ display: "flex", flexDirection: "column", gap: "1.25rem" }}>
        <div
          style={{
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
            borderBottom: "1px solid var(--border)",
            paddingBottom: "0.75rem",
          }}
        >
          <div
            style={{
              display: "flex",
              alignItems: "center",
              gap: "0.5rem",
              fontWeight: 600,
            }}
          >
            <Filter size={18} />
            Filters
          </div>
          <button
            className="btn btn-secondary btn-sm"
            onClick={onResetFilters}
            style={{ fontSize: "0.75rem", padding: "0.2rem 0.5rem" }}
            title="Reset all filters"
          >
            <RotateCcw size={12} />
            Reset
          </button>
        </div>

        {/* Categories Tree */}
        <div>
          <h4
            style={{
              fontSize: "0.85rem",
              fontWeight: 600,
              marginBottom: "0.5rem",
              textTransform: "uppercase",
              color: "var(--text-muted)",
            }}
          >
            Categories
          </h4>
          <div style={{ display: "flex", flexDirection: "column", gap: "0.25rem" }}>
            <button
              style={{
                textAlign: "left",
                padding: "0.35rem 0.5rem",
                borderRadius: "4px",
                fontSize: "0.85rem",
                fontWeight: !selectedCategory ? 600 : 400,
                backgroundColor: !selectedCategory ? "var(--primary-light)" : "transparent",
                color: !selectedCategory ? "var(--primary)" : "var(--text-main)",
              }}
              onClick={() => onSelectCategory(null)}
            >
              All Categories
            </button>

            {categories.map((cat) => (
              <div key={cat.id} style={{ display: "flex", flexDirection: "column" }}>
                <button
                  style={{
                    textAlign: "left",
                    padding: "0.35rem 0.5rem",
                    borderRadius: "4px",
                    fontSize: "0.85rem",
                    fontWeight: selectedCategory === cat.id ? 600 : 500,
                    backgroundColor:
                      selectedCategory === cat.id ? "var(--primary-light)" : "transparent",
                    color: selectedCategory === cat.id ? "var(--primary)" : "var(--text-main)",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "space-between",
                  }}
                  onClick={() => onSelectCategory(cat.id)}
                >
                  {cat.name}
                  {cat.subcategories?.length > 0 && (
                    <ChevronRight size={14} color="var(--text-muted)" />
                  )}
                </button>

                {cat.subcategories?.length > 0 && (
                  <div
                    style={{
                      paddingLeft: "1rem",
                      display: "flex",
                      flexDirection: "column",
                      gap: "0.2rem",
                      marginTop: "0.2rem",
                    }}
                  >
                    {cat.subcategories.map((sub) => (
                      <button
                        key={sub.id}
                        style={{
                          textAlign: "left",
                          padding: "0.25rem 0.5rem",
                          borderRadius: "4px",
                          fontSize: "0.8rem",
                          fontWeight: selectedCategory === sub.id ? 600 : 400,
                          backgroundColor:
                            selectedCategory === sub.id ? "var(--primary-light)" : "transparent",
                          color:
                            selectedCategory === sub.id ? "var(--primary)" : "var(--text-muted)",
                        }}
                        onClick={() => onSelectCategory(sub.id)}
                      >
                        {sub.name}
                      </button>
                    ))}
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>

        {/* Brand Filter */}
        <div>
          <h4
            style={{
              fontSize: "0.85rem",
              fontWeight: 600,
              marginBottom: "0.5rem",
              textTransform: "uppercase",
              color: "var(--text-muted)",
            }}
          >
            Brand
          </h4>
          <div style={{ display: "flex", flexDirection: "column", gap: "0.4rem" }}>
            {brands.map((b) => (
              <label
                key={b}
                style={{
                  display: "flex",
                  alignItems: "center",
                  gap: "0.5rem",
                  fontSize: "0.85rem",
                  cursor: "pointer",
                }}
              >
                <input
                  type="radio"
                  name="brand_filter"
                  checked={selectedBrand === b}
                  onChange={() => onSelectBrand(selectedBrand === b ? "" : b)}
                />
                {b}
              </label>
            ))}
            {selectedBrand && (
              <button
                style={{
                  fontSize: "0.75rem",
                  color: "var(--primary)",
                  textAlign: "left",
                  marginTop: "0.2rem",
                }}
                onClick={() => onSelectBrand("")}
              >
                Clear brand filter
              </button>
            )}
          </div>
        </div>

        {/* Price Range Filter */}
        <div>
          <h4
            style={{
              fontSize: "0.85rem",
              fontWeight: 600,
              marginBottom: "0.5rem",
              textTransform: "uppercase",
              color: "var(--text-muted)",
            }}
          >
            Price Range ($)
          </h4>
          <div style={{ display: "flex", alignItems: "center", gap: "0.5rem" }}>
            <input
              type="number"
              placeholder="Min"
              value={priceMin}
              onChange={(e) => setPriceMin(e.target.value)}
              className="input"
              style={{ padding: "0.35rem 0.5rem", fontSize: "0.8rem" }}
              min="0"
            />
            <span style={{ color: "var(--text-muted)" }}>-</span>
            <input
              type="number"
              placeholder="Max"
              value={priceMax}
              onChange={(e) => setPriceMax(e.target.value)}
              className="input"
              style={{ padding: "0.35rem 0.5rem", fontSize: "0.8rem" }}
              min="0"
            />
          </div>
        </div>
      </div>
    </aside>
  );
}
