export function createAuthApi(request) {
  return {
    login: (email, password) =>
      request("/auth/login", {
        method: "POST",
        body: JSON.stringify({ email, password }),
      }),
    register: (name, email, password) =>
      request("/auth/register", {
        method: "POST",
        body: JSON.stringify({ name, email, password }),
      }),
  };
}
