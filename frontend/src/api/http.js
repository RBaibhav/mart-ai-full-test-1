const API_BASE = "/api";

function getToken() {
  return localStorage.getItem("shopsmart_token");
}

export async function request(endpoint, options = {}) {
  const headers = {
    ...options.headers,
  };

  const token = getToken();
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  if (!(options.body instanceof FormData)) {
    headers["Content-Type"] = "application/json";
  }

  const response = await fetch(`${API_BASE}${endpoint}`, {
    ...options,
    headers,
  });

  const contentType = response.headers.get("content-type");
  let data;
  if (contentType && contentType.includes("application/json")) {
    data = await response.json();
  } else {
    data = await response.text();
  }

  if (!response.ok) {
    const error = new Error(data?.message || "Request failed");
    error.code = data?.code || "ERROR";
    error.status = response.status;
    error.data = data;
    throw error;
  }

  return data;
}
