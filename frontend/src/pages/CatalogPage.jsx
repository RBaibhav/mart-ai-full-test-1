import React, { useState, useEffect } from "react";
import { api } from "../api/client";
import { FilterSidebar } from "../components/FilterSidebar";
import { ProductCard } from "../components/ProductCard";
import { Pagination } from "../components/Pagination";
import { ProductDetailPageModal } from "../components/ProductDetailPageModal";
import { Search, ArrowUpDown, Layers, Sparkles } from "lucide-react";

export function CatalogPage({
  onAddToCart,
  onToggleWishlist,
  onOpenAssistant,
  wishlistIds = [],
  compareIds = [],
  onToggleCompare,
  onOpenCompare,
}) {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);

  // Filters state
  const [search, setSearch] = useState("");
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [selectedBrand, setSelectedBrand] = useState("");
  const [priceMin, setPriceMin] = useState("");
  const [priceMax, setPriceMax] = useState("");
  const [sort, setSort] = useState("rating_desc");

  // Selected product modal
  const [selectedProduct, setSelectedProduct] = useState(null);

  // Load categories hierarchy once
  useEffect(() => {
    api
      .getCategories()
      .then((data) => setCategories(data))
      .catch((err) => console.error("Failed to load categories", err));
  }, []);

  // Fetch products with filters & pagination
  const fetchProducts = () => {
    setLoading(true);
    api
      .getProducts({
        search,
        category: selectedCategory || undefined,
        brand: selectedBrand || undefined,
        priceMin: priceMin !== "" ? priceMin : undefined,
        priceMax: priceMax !== "" ? priceMax : undefined,
        sort,
        page,
        size: 9,
      })
      .then((res) => {
        setProducts(res.content || []);
        setTotalElements(res.totalElements || 0);
        setTotalPages(res.totalPages || 0);
      })
      .catch((err) => console.error("Failed to load products", err))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchProducts();
  }, [selectedCategory, selectedBrand, sort, page]);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    setPage(0);
    fetchProducts();
  };

  const handleResetFilters = () => {
    setSearch("");
    setSelectedCategory(null);
    setSelectedBrand("");
    setPriceMin("");
    setPriceMax("");
    setSort("rating_desc");
    setPage(0);
  };

  return (
    <div className="page-shell catalog-page">
      <div className="container">
        <section className="catalog-intro">
          <div>
            <div className="eyebrow">Curated commerce, made clearer</div>
            <h1 className="page-title">Find the right tech for the work ahead.</h1>
            <p className="page-description">
              Explore a verified catalog, compare the details that matter, and ask ShopSmart AI to
              narrow the field around your needs.
            </p>
          </div>
          <button className="ai-discovery-link" onClick={onOpenAssistant}>
            <Sparkles size={16} /> Find with AI <span>↗</span>
          </button>
        </section>
        {/* Top Controls: Search and Sort */}
        <div className="catalog-toolbar card">
          <div
            style={{
              display: "flex",
              flexWrap: "wrap",
              alignItems: "center",
              justifyContent: "space-between",
              gap: "1rem",
            }}
          >
            {/* Search Input */}
            <form
              onSubmit={handleSearchSubmit}
              style={{
                display: "flex",
                gap: "0.5rem",
                flex: "1",
                minWidth: "280px",
                maxWidth: "500px",
              }}
            >
              <div style={{ position: "relative", width: "100%" }}>
                <input
                  type="text"
                  placeholder="Search laptops, audio, monitors..."
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                  className="input"
                  style={{ paddingLeft: "2.25rem" }}
                />
                <Search
                  size={16}
                  color="var(--text-muted)"
                  style={{
                    position: "absolute",
                    left: "0.75rem",
                    top: "50%",
                    transform: "translateY(-50%)",
                  }}
                />
              </div>
              <button type="submit" className="btn btn-primary btn-sm">
                Search
              </button>
            </form>

            {/* Sort & Results Count */}
            <div style={{ display: "flex", alignItems: "center", gap: "1rem" }}>
              <span style={{ fontSize: "0.85rem", color: "var(--text-muted)" }}>
                Showing <strong>{products.length}</strong> of <strong>{totalElements}</strong>{" "}
                products
              </span>

              <div style={{ display: "flex", alignItems: "center", gap: "0.5rem" }}>
                <ArrowUpDown size={15} color="var(--text-muted)" />
                <select
                  value={sort}
                  onChange={(e) => {
                    setSort(e.target.value);
                    setPage(0);
                  }}
                  className="input"
                  style={{
                    width: "auto",
                    padding: "0.35rem 0.65rem",
                    fontSize: "0.85rem",
                  }}
                >
                  <option value="rating_desc">Highest Rated</option>
                  <option value="price_asc">Price: Low to High</option>
                  <option value="price_desc">Price: High to Low</option>
                </select>
              </div>
            </div>
          </div>
        </div>

        {/* Main Content: Filter Sidebar + Products Grid */}
        <div
          className="catalog-content"
          style={{ display: "flex", gap: "1.5rem", alignItems: "flex-start" }}
        >
          <FilterSidebar
            categories={categories}
            selectedCategory={selectedCategory}
            onSelectCategory={(id) => {
              setSelectedCategory(id);
              setPage(0);
            }}
            selectedBrand={selectedBrand}
            onSelectBrand={(b) => {
              setSelectedBrand(b);
              setPage(0);
            }}
            priceMin={priceMin}
            setPriceMin={setPriceMin}
            priceMax={priceMax}
            setPriceMax={setPriceMax}
            onResetFilters={handleResetFilters}
          />

          <div style={{ flex: 1, minWidth: 0 }}>
            {loading ? (
              <div
                className="card"
                style={{
                  padding: "4rem",
                  textAlign: "center",
                  color: "var(--text-muted)",
                }}
              >
                Loading catalog products...
              </div>
            ) : products.length === 0 ? (
              <div className="card" style={{ padding: "4rem", textAlign: "center" }}>
                <h3
                  style={{
                    fontSize: "1.1rem",
                    fontWeight: 600,
                    marginBottom: "0.5rem",
                  }}
                >
                  No products found
                </h3>
                <p
                  style={{
                    color: "var(--text-muted)",
                    fontSize: "0.85rem",
                    marginBottom: "1rem",
                  }}
                >
                  Try adjusting your search criteria, category filter, or price range.
                </p>
                <button className="btn btn-secondary" onClick={handleResetFilters}>
                  Clear all filters
                </button>
              </div>
            ) : (
              <>
                <div
                  className="product-grid"
                  style={{
                    display: "grid",
                    gridTemplateColumns: "repeat(auto-fill, minmax(260px, 1fr))",
                    gap: "1.25rem",
                  }}
                >
                  {products.map((product) => (
                    <ProductCard
                      key={product.id}
                      product={product}
                      onAddToCart={onAddToCart}
                      onToggleWishlist={onToggleWishlist}
                      isWishlisted={wishlistIds.includes(product.id)}
                      isSelectedForCompare={compareIds.includes(product.id)}
                      onToggleCompare={onToggleCompare}
                      onSelectProduct={setSelectedProduct}
                    />
                  ))}
                </div>

                <Pagination
                  currentPage={page}
                  totalPages={totalPages}
                  onPageChange={(p) => setPage(p)}
                />
              </>
            )}
          </div>
        </div>
      </div>

      {/* Floating Compare Tray */}
      {compareIds.length > 0 && (
        <div
          style={{
            position: "fixed",
            bottom: "1.5rem",
            left: "50%",
            transform: "translateX(-50%)",
            backgroundColor: "#0f172a",
            color: "#ffffff",
            padding: "0.75rem 1.25rem",
            borderRadius: "999px",
            boxShadow: "var(--shadow-lg)",
            zIndex: 50,
            display: "flex",
            alignItems: "center",
            gap: "1rem",
          }}
        >
          <div
            style={{
              display: "flex",
              alignItems: "center",
              gap: "0.5rem",
              fontSize: "0.875rem",
              fontWeight: 600,
            }}
          >
            <Layers size={18} color="#60a5fa" />
            <span>{compareIds.length} products selected for comparison</span>
          </div>
          <button
            className="btn btn-primary btn-sm"
            onClick={onOpenCompare}
            style={{ borderRadius: "999px", padding: "0.35rem 0.85rem" }}
          >
            Compare Now
          </button>
        </div>
      )}

      {/* Product Detail Modal */}
      <ProductDetailPageModal
        product={selectedProduct}
        onClose={() => setSelectedProduct(null)}
        onAddToCart={onAddToCart}
        onToggleWishlist={onToggleWishlist}
        isWishlisted={selectedProduct ? wishlistIds.includes(selectedProduct.id) : false}
      />
    </div>
  );
}
