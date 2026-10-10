import React from "react";
import { Navigate, Route, Routes } from "react-router-dom";
import { AuthPage } from "../pages/AuthPage";

export function AuthRoutes() {
  return (
    <Routes>
      <Route path="/signin" element={<AuthPage initialMode="signin" />} />
      <Route path="/signup" element={<AuthPage initialMode="signup" />} />
      <Route path="*" element={<Navigate to="/signin" replace />} />
    </Routes>
  );
}
