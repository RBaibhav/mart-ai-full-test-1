import React, { useState } from "react";
import { api } from "../api/client";
import {
  X,
  Send,
  Sparkles,
  Shield,
  Wrench,
  Bookmark,
  CheckCircle2,
  AlertCircle,
  ShoppingCart,
} from "lucide-react";

export function AssistantDrawer({ isOpen, onClose, onAddToCart }) {
  const [input, setInput] = useState("");
  const [messages, setMessages] = useState([
    {
      id: "init-1",
      sender: "assistant",
      text: "Hello! I am your ShopSmart AI assistant. I can recommend catalog products matching your budget and specifications, check warehouse stock, answer policy questions from approved documents, or track your orders and explain cancellation eligibility.",
      tools: [],
      sources: [],
    },
  ]);
  const [loading, setLoading] = useState(false);

  if (!isOpen) return null;

  const quickPrompts = [
    "Where is my order, and can I cancel after shipment?",
    "Laptop for Java development and AI learning under $1000",
    "Can I return an opened laptop?",
    "Show wireless noise cancelling headphones under $400",
  ];

  const handleSend = async (messageText) => {
    const textToSend = messageText || input;
    if (!textToSend.trim() || loading) return;

    const userMsg = {
      id: String(Date.now()),
      sender: "user",
      text: textToSend,
    };
    setMessages((prev) => [...prev, userMsg]);
    setInput("");
    setLoading(true);

    try {
      // Call coordinated chat API
      const res = await api.sendChat(textToSend);

      const assistantMsg = {
        id: String(Date.now() + 1),
        sender: "assistant",
        text: res.answer,
        intent: res.intent,
        tools: res.toolActivity || [],
        sources: res.sources || [],
        recommendations: res.recommendations?.recommendations || [],
        clarifyingQuestion: res.recommendations?.clarifyingQuestion,
        interpretedRequirement: res.recommendations?.interpretedRequirement,
        budget: res.recommendations?.budget,
        auditId: res.auditId,
      };

      setMessages((prev) => [...prev, assistantMsg]);
    } catch (err) {
      setMessages((prev) => [
        ...prev,
        {
          id: String(Date.now() + 1),
          sender: "assistant",
          text: err.message || "An error occurred while contacting the assistant.",
          isError: true,
        },
      ]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <div className="drawer-overlay" onClick={onClose} />
      <div className="drawer">
        {/* Drawer Header */}
        <div
          style={{
            padding: "1.25rem",
            borderBottom: "1px solid var(--border)",
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
            background: "linear-gradient(135deg, #1e293b 0%, #0f172a 100%)",
            color: "#ffffff",
          }}
        >
          <div style={{ display: "flex", alignItems: "center", gap: "0.6rem" }}>
            <div
              style={{
                backgroundColor: "rgba(255, 255, 255, 0.1)",
                padding: "0.4rem",
                borderRadius: "8px",
              }}
            >
              <Sparkles size={20} color="#60a5fa" />
            </div>
            <div>
              <div style={{ fontWeight: 700, fontSize: "1rem" }}>ShopSmart Assistant</div>
              <div style={{ fontSize: "0.75rem", color: "#94a3b8" }}>
                Coordinated AI • Catalog • Knowledge RAG • Orders
              </div>
            </div>
          </div>
          <button onClick={onClose} style={{ color: "#94a3b8", padding: "4px" }}>
            <X size={20} />
          </button>
        </div>

        {/* Quick Prompts Bar */}
        <div
          style={{
            padding: "0.75rem 1rem",
            borderBottom: "1px solid var(--border)",
            backgroundColor: "#f8fafc",
          }}
        >
          <div
            style={{
              fontSize: "0.75rem",
              fontWeight: 600,
              color: "var(--text-muted)",
              marginBottom: "0.4rem",
            }}
          >
            Suggested Queries (From Case Study):
          </div>
          <div
            style={{
              display: "flex",
              gap: "0.4rem",
              overflowX: "auto",
              paddingBottom: "0.2rem",
            }}
          >
            {quickPrompts.map((p, idx) => (
              <button
                key={idx}
                className="btn btn-secondary btn-sm"
                style={{
                  fontSize: "0.75rem",
                  whiteSpace: "nowrap",
                  padding: "0.25rem 0.5rem",
                }}
                onClick={() => handleSend(p)}
                disabled={loading}
              >
                {p}
              </button>
            ))}
          </div>
        </div>

        {/* Messages Stream */}
        <div
          style={{
            flex: 1,
            overflowY: "auto",
            padding: "1rem",
            display: "flex",
            flexDirection: "column",
            gap: "1rem",
          }}
        >
          {messages.map((msg) => (
            <div
              key={msg.id}
              style={{
                alignSelf: msg.sender === "user" ? "flex-end" : "flex-start",
                maxWidth: "92%",
                display: "flex",
                flexDirection: "column",
                gap: "0.5rem",
              }}
            >
              <div
                style={{
                  padding: "0.85rem 1rem",
                  borderRadius: "12px",
                  fontSize: "0.875rem",
                  backgroundColor: msg.sender === "user" ? "var(--primary)" : "#f1f5f9",
                  color: msg.sender === "user" ? "#ffffff" : "var(--text-main)",
                  borderBottomRightRadius: msg.sender === "user" ? "2px" : "12px",
                  borderBottomLeftRadius: msg.sender === "assistant" ? "2px" : "12px",
                  whiteSpace: "pre-line",
                  boxShadow: "var(--shadow-sm)",
                }}
              >
                {msg.text}
              </div>

              {/* Clarifying Question Callout */}
              {msg.clarifyingQuestion && (
                <div
                  style={{
                    backgroundColor: "#fef3c7",
                    border: "1px solid #fde68a",
                    padding: "0.75rem",
                    borderRadius: "8px",
                    fontSize: "0.8rem",
                    color: "#92400e",
                  }}
                >
                  <div
                    style={{
                      fontWeight: 600,
                      display: "flex",
                      alignItems: "center",
                      gap: "0.3rem",
                      marginBottom: "0.2rem",
                    }}
                  >
                    <AlertCircle size={14} /> Clarification Needed
                  </div>
                  {msg.clarifyingQuestion}
                </div>
              )}

              {/* Recommendations Card List */}
              {msg.recommendations?.length > 0 && (
                <div
                  style={{
                    display: "flex",
                    flexDirection: "column",
                    gap: "0.5rem",
                    marginTop: "0.25rem",
                  }}
                >
                  <div
                    style={{
                      fontSize: "0.75rem",
                      fontWeight: 600,
                      color: "var(--text-muted)",
                    }}
                  >
                    Matching Catalog Recommendations:
                  </div>
                  {msg.recommendations.map((rec, rIdx) => (
                    <div
                      key={rIdx}
                      style={{
                        backgroundColor: "#ffffff",
                        border: "1px solid var(--border)",
                        borderRadius: "8px",
                        padding: "0.75rem",
                        fontSize: "0.8rem",
                      }}
                    >
                      <div
                        style={{
                          display: "flex",
                          justifyContent: "space-between",
                          alignItems: "flex-start",
                          marginBottom: "0.25rem",
                        }}
                      >
                        <span style={{ fontWeight: 600, color: "var(--primary)" }}>
                          {rec.productName}
                        </span>
                        <span
                          className={`badge ${rec.availability === "IN_STOCK" ? "badge-stock-in" : "badge-stock-out"}`}
                        >
                          {rec.availability}
                        </span>
                      </div>
                      <div
                        style={{
                          color: "var(--text-main)",
                          marginBottom: "0.5rem",
                        }}
                      >
                        <strong>Reason:</strong> {rec.reason}
                      </div>
                      {onAddToCart && (
                        <button
                          className="btn btn-primary btn-sm"
                          style={{
                            width: "100%",
                            fontSize: "0.75rem",
                            padding: "0.3rem",
                          }}
                          disabled={rec.availability === "OUT_OF_STOCK"}
                          onClick={() => onAddToCart(rec.productId, 1)}
                        >
                          <ShoppingCart size={13} /> Add to Cart
                        </button>
                      )}
                    </div>
                  ))}
                </div>
              )}

              {/* Source Citations */}
              {msg.sources?.length > 0 && (
                <div
                  style={{
                    backgroundColor: "#f8fafc",
                    border: "1px solid var(--border)",
                    borderRadius: "8px",
                    padding: "0.6rem 0.75rem",
                    fontSize: "0.75rem",
                  }}
                >
                  <div
                    style={{
                      fontWeight: 600,
                      color: "var(--text-muted)",
                      display: "flex",
                      alignItems: "center",
                      gap: "0.3rem",
                      marginBottom: "0.3rem",
                    }}
                  >
                    <Bookmark size={13} /> Grounded Document Citations
                  </div>
                  {msg.sources.map((src, sIdx) => (
                    <div key={sIdx} style={{ color: "var(--text-main)", padding: "0.2rem 0" }}>
                      • <strong>{src.document}</strong> (Section: <em>{src.section}</em>, v
                      {src.version})
                    </div>
                  ))}
                </div>
              )}

              {/* Tool Activity Logs (Explainability & Audit) */}
              {msg.tools?.length > 0 && (
                <div
                  style={{
                    backgroundColor: "#f1f5f9",
                    border: "1px dashed #cbd5e1",
                    borderRadius: "6px",
                    padding: "0.5rem 0.75rem",
                    fontSize: "0.7rem",
                    color: "var(--text-muted)",
                  }}
                >
                  <div
                    style={{
                      fontWeight: 600,
                      display: "flex",
                      alignItems: "center",
                      gap: "0.3rem",
                      marginBottom: "0.2rem",
                    }}
                  >
                    <Wrench size={12} /> Backend Tool Execution Audit:
                  </div>
                  {msg.tools.map((t, tIdx) => (
                    <div key={tIdx}>
                      ⚡ <code>{t.tool}</code>: {t.details || t.action}
                    </div>
                  ))}
                </div>
              )}
            </div>
          ))}

          {loading && (
            <div
              style={{
                alignSelf: "flex-start",
                backgroundColor: "#f1f5f9",
                padding: "0.75rem 1rem",
                borderRadius: "12px",
                fontSize: "0.85rem",
                color: "var(--text-muted)",
                display: "flex",
                alignItems: "center",
                gap: "0.5rem",
              }}
            >
              <Sparkles size={16} className="animate-spin" color="var(--primary)" />
              Evaluating catalog, warehouse stock, and approved policies...
            </div>
          )}
        </div>

        {/* Input Bar */}
        <div
          style={{
            padding: "1rem",
            borderTop: "1px solid var(--border)",
            backgroundColor: "#ffffff",
          }}
        >
          <form
            onSubmit={(e) => {
              e.preventDefault();
              handleSend();
            }}
            style={{ display: "flex", gap: "0.5rem" }}
          >
            <input
              type="text"
              placeholder="Ask about products, policies, or orders..."
              value={input}
              onChange={(e) => setInput(e.target.value)}
              className="input"
              disabled={loading}
              style={{ flex: 1 }}
            />
            <button
              type="submit"
              className="btn btn-primary"
              disabled={loading || !input.trim()}
              style={{ padding: "0.5rem 0.85rem" }}
            >
              <Send size={16} />
            </button>
          </form>
        </div>
      </div>
    </>
  );
}
