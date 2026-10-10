import React from "react";
import { Star, Heart, ShoppingCart, ArrowUpRight } from "lucide-react";

export function ProductCard({
  product,
  onAddToCart,
  onToggleWishlist,
  isWishlisted,
  isSelectedForCompare,
  onToggleCompare,
  onSelectProduct,
}) {
  const isOutOfStock = product.availability === "OUT_OF_STOCK";
  const imageUrl =
    product.images?.[0] ||
    "https://images.unsplash.com/photo-1526738549149-8e07eca6c147?w=600";

  return (
    <article className="product-card">
      {/* Compare Checkbox & Wishlist Top Bar */}
      <div className="product-card-topline">
        <label className="compare-toggle">
          <input
            type="checkbox"
            checked={isSelectedForCompare}
            onChange={() => onToggleCompare(product.id)}
            style={{ cursor: "pointer" }}
          />
          Compare
        </label>

        <button
          className="icon-button wishlist-button"
          onClick={(e) => {
            e.stopPropagation();
            onToggleWishlist(product.id);
          }}
          title={isWishlisted ? "Remove from wishlist" : "Add to wishlist"}
        >
          <Heart size={18} fill={isWishlisted ? "var(--danger)" : "none"} />
        </button>
      </div>

      {/* Product Image */}
      <div className="product-image" onClick={() => onSelectProduct(product)}>
        <img
          src={imageUrl}
          alt={product.name}
          className="product-image-img"
          loading="lazy"
        />
      </div>

      {/* Product Info */}
      <div className="product-info">
        <div className="product-brand">{product.brand}</div>
        <h3 className="product-name" onClick={() => onSelectProduct(product)}>
          {product.name}
        </h3>

        {/* Rating & Availability */}
        <div className="product-meta">
          <div className="rating">
            <Star size={14} fill="#eab308" color="#eab308" />
            <span>
              {product.rating ? Number(product.rating).toFixed(1) : "4.5"}
            </span>
          </div>

          <span
            className={`badge ${isOutOfStock ? "badge-stock-out" : "badge-stock-in"}`}
          >
            {isOutOfStock ? "Out of Stock" : "In Stock"}
          </span>
        </div>

        {/* Price & Action */}
        <div className="product-buy-row">
          <div className="product-price">
            ${Number(product.price).toFixed(2)}
          </div>

          <button
            className={`btn btn-sm ${isOutOfStock ? "btn-secondary" : "btn-primary"}`}
            disabled={isOutOfStock}
            onClick={() => onAddToCart(product.id, 1)}
          >
            {isOutOfStock ? (
              <ArrowUpRight size={14} />
            ) : (
              <ShoppingCart size={14} />
            )}
            {isOutOfStock ? "Sold Out" : "Add"}
          </button>
        </div>
      </div>
    </article>
  );
}
