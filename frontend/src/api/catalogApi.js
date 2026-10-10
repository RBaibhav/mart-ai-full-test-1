export function createCatalogApi(request) {
  return {
    getCategories: () => request("/categories"),

    getProducts: (params = {}) => {
      const query = new URLSearchParams();
      if (params.search) query.append("search", params.search);
      if (params.category) query.append("category", params.category);
      if (params.brand) query.append("brand", params.brand);
      if (params.priceMin !== undefined && params.priceMin !== "")
        query.append("priceMin", params.priceMin);
      if (params.priceMax !== undefined && params.priceMax !== "")
        query.append("priceMax", params.priceMax);
      if (params.sort) query.append("sort", params.sort);
      if (params.page !== undefined) query.append("page", params.page);
      if (params.size !== undefined) query.append("size", params.size);
      return request(`/products?${query.toString()}`);
    },

    getProductById: (id) => request(`/products/${id}`),

    compareProducts: (ids) => {
      const query = new URLSearchParams();
      ids.forEach((id) => query.append("ids", id));
      return request(`/products/compare?${query.toString()}`);
    },
  };
}
