const API_BASE = '/api';

function getToken() {
  return localStorage.getItem('shopsmart_token');
}

async function request(endpoint, options = {}) {
  const headers = {
    ...options.headers,
  };

  const token = getToken();
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  if (!(options.body instanceof FormData)) {
    headers['Content-Type'] = 'application/json';
  }

  const response = await fetch(`${API_BASE}${endpoint}`, {
    ...options,
    headers,
  });

  const contentType = response.headers.get('content-type');
  let data;
  if (contentType && contentType.includes('application/json')) {
    data = await response.json();
  } else {
    data = await response.text();
  }

  if (!response.ok) {
    const error = new Error(data?.message || 'Request failed');
    error.code = data?.code || 'ERROR';
    error.status = response.status;
    error.data = data;
    throw error;
  }

  return data;
}

export const api = {
  // Authentication
  login: (email, password) =>
    request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    }),

  // Categories
  getCategories: () => request('/categories'),

  // Products
  getProducts: (params = {}) => {
    const query = new URLSearchParams();
    if (params.search) query.append('search', params.search);
    if (params.category) query.append('category', params.category);
    if (params.brand) query.append('brand', params.brand);
    if (params.priceMin !== undefined && params.priceMin !== '') query.append('priceMin', params.priceMin);
    if (params.priceMax !== undefined && params.priceMax !== '') query.append('priceMax', params.priceMax);
    if (params.sort) query.append('sort', params.sort);
    if (params.page !== undefined) query.append('page', params.page);
    if (params.size !== undefined) query.append('size', params.size);
    return request(`/products?${query.toString()}`);
  },

  getProductById: (id) => request(`/products/${id}`),

  compareProducts: (ids) => {
    const query = new URLSearchParams();
    ids.forEach((id) => query.append('ids', id));
    return request(`/products/compare?${query.toString()}`);
  },

  // Preferences
  getPreferences: () => request('/preferences'),
  updatePreferences: (preferences) =>
    request('/preferences', {
      method: 'PUT',
      body: JSON.stringify(preferences),
    }),

  // Wishlist
  getWishlist: () => request('/wishlist'),
  addToWishlist: (productId) =>
    request('/wishlist', {
      method: 'POST',
      body: JSON.stringify({ productId }),
    }),
  removeFromWishlist: (productId) =>
    request(`/wishlist/${productId}`, {
      method: 'DELETE',
    }),

  // Cart
  getCart: () => request('/cart'),
  addToCart: (productId, quantity = 1) =>
    request('/cart/items', {
      method: 'POST',
      body: JSON.stringify({ productId, quantity }),
    }),
  updateCartItem: (productId, quantity) =>
    request(`/cart/items/${productId}`, {
      method: 'PUT',
      body: JSON.stringify({ quantity }),
    }),
  removeFromCart: (productId) =>
    request(`/cart/items/${productId}`, {
      method: 'DELETE',
    }),

  // Orders & Inventory
  getOrders: () => request('/orders'),
  getOrderById: (id) => request(`/orders/${id}`),
  getInventory: (productId) => request(`/inventory/${productId}`),

  // Document Upload
  uploadDocument: (formData) =>
    request('/documents', {
      method: 'POST',
      body: formData,
    }),

  // AI Assistant Endpoints
  getRecommendations: (message) =>
    request('/ai/recommendations', {
      method: 'POST',
      body: JSON.stringify({ message }),
    }),

  queryKnowledge: (question, category, region) =>
    request('/ai/knowledge', {
      method: 'POST',
      body: JSON.stringify({ question, category, region }),
    }),

  sendChat: (message) =>
    request('/ai/chat', {
      method: 'POST',
      body: JSON.stringify({ message }),
    }),

  getAudits: () => request('/ai/audit'),
};
