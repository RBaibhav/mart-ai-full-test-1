export function createOperationsApi(request) {
  return {
    getOrders: () => request("/orders"),
    getOrderById: (id) => request(`/orders/${id}`),
    getInventory: (productId) => request(`/inventory/${productId}`),
    uploadDocument: (formData) =>
      request("/documents", {
        method: "POST",
        body: formData,
      }),
  };
}
