import React from "react";
import { BrowserRouter, Navigate, useLocation, Routes, Route } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { AuthRoutes } from "./AuthRoutes";
import { WorkspaceRoutes } from "./WorkspaceRoutes";

function LoadingScreen() {
  return (
    <div className="auth-loading">
      <div className="skeleton auth-loading-mark" />
      <div className="skeleton auth-loading-line" />
    </div>
  );
}

function RoutedApplication() {
  const { user, loading } = useAuth();
  const location = useLocation();

  if (loading && !user) return <LoadingScreen />;
  if (!user) return <AuthRoutes />;
  if (location.pathname === "/signin" || location.pathname === "/signup") {
    return <Navigate to="/catalog" replace />;
  }

  return <WorkspaceRoutes />;
}

export function AppRouter() {
  return (
    <BrowserRouter>
      <RoutedApplication />
    </BrowserRouter>
  );
}
