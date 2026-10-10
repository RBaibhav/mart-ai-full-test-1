import React, { createContext, useContext, useState } from "react";
import { api } from "../api/client";

const AuthContext = createContext(null);

export const DEMO_ACCOUNTS = [
  {
    email: "alice@example.com",
    name: "Alice Walker",
    role: "Prime",
    password: "password123",
  },
  {
    email: "bob@example.com",
    name: "Bob Smith",
    role: "Premium",
    password: "password123",
  },
  {
    email: "charlie@example.com",
    name: "Charlie Brown",
    role: "Basic",
    password: "password123",
  },
  {
    email: "admin@shopsmart.com",
    name: "Admin Uploader",
    role: "Admin (Uploader)",
    password: "admin123",
  },
];

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem("shopsmart_user");
    return saved ? JSON.parse(saved) : null;
  });
  const [token, setToken] = useState(() => localStorage.getItem("shopsmart_token"));
  const [loading, setLoading] = useState(false);

  const login = async (email, password) => {
    setLoading(true);
    try {
      const res = await api.login(email, password);
      localStorage.setItem("shopsmart_token", res.token);
      localStorage.setItem("shopsmart_user", JSON.stringify(res.customer));
      setToken(res.token);
      setUser(res.customer);
      return res.customer;
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    localStorage.removeItem("shopsmart_token");
    localStorage.removeItem("shopsmart_user");
    setToken(null);
    setUser(null);
  };

  const switchAccount = async (account) => {
    return login(account.email, account.password);
  };

  const register = async (name, email, password) => {
    setLoading(true);
    try {
      const res = await api.register(name, email, password);
      localStorage.setItem("shopsmart_token", res.token);
      localStorage.setItem("shopsmart_user", JSON.stringify(res.customer));
      setToken(res.token);
      setUser(res.customer);
      return res.customer;
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthContext.Provider value={{ user, token, loading, login, register, logout, switchAccount }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
