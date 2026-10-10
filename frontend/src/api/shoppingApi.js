export function createShoppingApi(request) {
  return {
    getPreferences: () => request("/preferences"),
    updatePreferences: (preferences) =>
      request("/preferences", {
        method: "PUT",
        body: JSON.stringify(preferences),
      }),

    getWishlist: () => request("/wishlist"),
    addToWishlist: (productId) =>
      request("/wishlist", {
        method: "POST",
        body: JSON.stringify({ productId }),
      }),
    removeFromWishlist: (productId) => request(`/wishlist/${productId}`, { method: "DELETE" }),

    getCart: () => request("/cart"),
    addToCart: (productId, quantity = 1) =>
      request("/cart/items", {
        method: "POST",
        body: JSON.stringify({ productId, quantity }),
      }),
    updateCartItem: (productId, quantity) =>
      request(`/cart/items/${productId}`, {
        method: "PUT",
        body: JSON.stringify({ quantity }),
      }),
    removeFromCart: (productId) => request(`/cart/items/${productId}`, { method: "DELETE" }),
  };
}
