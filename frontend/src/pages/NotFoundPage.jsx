import React from "react";
import { ArrowLeft, Compass } from "lucide-react";
import { useNavigate } from "react-router-dom";

export function NotFoundPage() {
  const navigate = useNavigate();

  return (
    <section className="page-shell not-found-page">
      <div className="not-found-mark">
        <Compass size={26} />
      </div>
      <div className="eyebrow">404 · Route not found</div>
      <h1 className="page-title">This workspace view does not exist.</h1>
      <p className="page-description">
        The link may be outdated, or the page may have moved. Return to the catalog to continue
        browsing.
      </p>
      <button className="btn btn-primary" onClick={() => navigate("/catalog")}>
        <ArrowLeft size={16} /> Back to catalog
      </button>
    </section>
  );
}
