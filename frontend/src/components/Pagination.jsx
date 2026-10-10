import React from "react";
import { ChevronLeft, ChevronRight } from "lucide-react";

export function Pagination({ currentPage, totalPages, onPageChange }) {
  if (totalPages <= 1) return null;

  return (
    <div
      style={{
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        gap: "0.5rem",
        marginTop: "1.5rem",
      }}
    >
      <button
        className="btn btn-secondary btn-sm"
        disabled={currentPage <= 0}
        onClick={() => onPageChange(currentPage - 1)}
      >
        <ChevronLeft size={16} />
        Prev
      </button>

      <div style={{ display: "flex", gap: "0.25rem" }}>
        {Array.from({ length: totalPages }).map((_, idx) => (
          <button
            key={idx}
            className={`btn btn-sm ${currentPage === idx ? "btn-primary" : "btn-secondary"}`}
            style={{ minWidth: "32px" }}
            onClick={() => onPageChange(idx)}
          >
            {idx + 1}
          </button>
        ))}
      </div>

      <button
        className="btn btn-secondary btn-sm"
        disabled={currentPage >= totalPages - 1}
        onClick={() => onPageChange(currentPage + 1)}
      >
        Next
        <ChevronRight size={16} />
      </button>
    </div>
  );
}
