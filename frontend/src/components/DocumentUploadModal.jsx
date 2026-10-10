import React, { useState } from "react";
import { api } from "../api/client";
import { useAuth } from "../context/AuthContext";
import { X, Upload, CheckCircle2, AlertTriangle } from "lucide-react";

export function DocumentUploadModal({ isOpen, onClose }) {
  const { user } = useAuth();
  const [file, setFile] = useState(null);
  const [version, setVersion] = useState("2026.1");
  const [effectiveDate, setEffectiveDate] = useState("2026-01-01");
  const [category, setCategory] = useState("Policies");
  const [region, setRegion] = useState("GLOBAL");
  const [loading, setLoading] = useState(false);
  const [statusMessage, setStatusMessage] = useState(null);
  const [isError, setIsError] = useState(false);

  if (!isOpen) return null;

  const isAdminUploader = user?.email?.toLowerCase() === "admin@shopsmart.com";

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!file) {
      setIsError(true);
      setStatusMessage("Please select a PDF, Markdown, or JSON document to upload.");
      return;
    }

    setLoading(true);
    setStatusMessage(null);
    setIsError(false);

    try {
      const formData = new FormData();
      formData.append("file", file);
      formData.append("version", version);
      formData.append("effectiveDate", effectiveDate);
      formData.append("category", category);
      formData.append("region", region);

      const res = await api.uploadDocument(formData);
      setIsError(false);
      setStatusMessage(
        `Document successfully ingested! Document ID: ${res.documentId.substring(0, 8)}, Chunks: ${res.chunkCount}, Status: ${res.status}`,
      );
      setFile(null);
    } catch (err) {
      setIsError(true);
      setStatusMessage(err.message || "Failed to upload document.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            marginBottom: "1.25rem",
            borderBottom: "1px solid var(--border)",
            paddingBottom: "0.75rem",
          }}
        >
          <div>
            <h2 style={{ fontSize: "1.25rem", fontWeight: 700 }}>Upload Knowledge Document</h2>
            <p style={{ fontSize: "0.8rem", color: "var(--text-muted)" }}>
              Upload approved policy or knowledge files (PDF, Markdown, JSON)
            </p>
          </div>
          <button className="btn btn-secondary btn-sm" onClick={onClose}>
            <X size={18} />
          </button>
        </div>

        {/* Safeguard Notice */}
        {!isAdminUploader && (
          <div
            style={{
              backgroundColor: "#fffbeb",
              border: "1px solid #fde68a",
              color: "#92400e",
              padding: "0.75rem",
              borderRadius: "8px",
              fontSize: "0.8rem",
              marginBottom: "1rem",
              display: "flex",
              alignItems: "center",
              gap: "0.5rem",
            }}
          >
            <AlertTriangle size={16} />
            <span>
              <strong>Safeguard Policy:</strong> Only the configured demo uploader (
              <code>admin@shopsmart.com</code>) is authorized to ingest approved documents. Switch
              to the Admin Uploader account in the navbar to test uploads.
            </span>
          </div>
        )}

        <form
          onSubmit={handleSubmit}
          style={{ display: "flex", flexDirection: "column", gap: "1rem" }}
        >
          <div>
            <label
              style={{
                display: "block",
                fontSize: "0.85rem",
                fontWeight: 600,
                marginBottom: "0.35rem",
              }}
            >
              Document File (.pdf, .md, .json)
            </label>
            <input
              type="file"
              accept=".pdf,.md,.markdown,.json"
              onChange={(e) => setFile(e.target.files[0] || null)}
              className="input"
              required
            />
          </div>

          <div
            style={{
              display: "grid",
              gridTemplateColumns: "1fr 1fr",
              gap: "1rem",
            }}
          >
            <div>
              <label
                style={{
                  display: "block",
                  fontSize: "0.85rem",
                  fontWeight: 600,
                  marginBottom: "0.35rem",
                }}
              >
                Version
              </label>
              <input
                type="text"
                value={version}
                onChange={(e) => setVersion(e.target.value)}
                className="input"
                required
              />
            </div>

            <div>
              <label
                style={{
                  display: "block",
                  fontSize: "0.85rem",
                  fontWeight: 600,
                  marginBottom: "0.35rem",
                }}
              >
                Effective Date
              </label>
              <input
                type="date"
                value={effectiveDate}
                onChange={(e) => setEffectiveDate(e.target.value)}
                className="input"
                required
              />
            </div>
          </div>

          <div
            style={{
              display: "grid",
              gridTemplateColumns: "1fr 1fr",
              gap: "1rem",
            }}
          >
            <div>
              <label
                style={{
                  display: "block",
                  fontSize: "0.85rem",
                  fontWeight: 600,
                  marginBottom: "0.35rem",
                }}
              >
                Category
              </label>
              <input
                type="text"
                value={category}
                onChange={(e) => setCategory(e.target.value)}
                className="input"
                required
              />
            </div>

            <div>
              <label
                style={{
                  display: "block",
                  fontSize: "0.85rem",
                  fontWeight: 600,
                  marginBottom: "0.35rem",
                }}
              >
                Region
              </label>
              <input
                type="text"
                value={region}
                onChange={(e) => setRegion(e.target.value)}
                className="input"
                required
              />
            </div>
          </div>

          {statusMessage && (
            <div
              style={{
                backgroundColor: isError ? "var(--danger-bg)" : "var(--success-bg)",
                color: isError ? "var(--danger)" : "var(--success)",
                border: `1px solid ${isError ? "#fecaca" : "#bbf7d0"}`,
                padding: "0.75rem",
                borderRadius: "8px",
                fontSize: "0.85rem",
                display: "flex",
                alignItems: "center",
                gap: "0.5rem",
              }}
            >
              {isError ? <AlertTriangle size={16} /> : <CheckCircle2 size={16} />}
              {statusMessage}
            </div>
          )}

          <div
            style={{
              display: "flex",
              justifyContent: "flex-end",
              gap: "0.75rem",
              marginTop: "0.5rem",
            }}
          >
            <button type="button" className="btn btn-secondary" onClick={onClose}>
              Cancel
            </button>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={loading || !isAdminUploader}
            >
              <Upload size={16} />
              {loading ? "Chunking & Ingesting..." : "Upload & Ingest"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
