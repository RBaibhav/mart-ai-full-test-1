import React, { useEffect, useState } from "react";
import { CheckCircle2, Info, X, XCircle } from "lucide-react";

const icons = {
  success: CheckCircle2,
  error: XCircle,
  info: Info,
};

export function ToastHost() {
  const [toasts, setToasts] = useState([]);

  useEffect(() => {
    const handleToast = (event) => {
      const toast = event.detail;
      setToasts((current) => [...current, toast]);
      window.setTimeout(() => {
        setToasts((current) => current.filter((item) => item.id !== toast.id));
      }, 4200);
    };

    window.addEventListener("shopsmart:toast", handleToast);
    return () => window.removeEventListener("shopsmart:toast", handleToast);
  }, []);

  return (
    <div className="toast-region" aria-live="polite" aria-atomic="true">
      {toasts.map((toast) => {
        const Icon = icons[toast.tone] || Info;
        return (
          <div className={`toast toast-${toast.tone}`} key={toast.id} role="status">
            <Icon size={17} />
            <span>{toast.message}</span>
            <button
              className="toast-close"
              onClick={() => setToasts((current) => current.filter((item) => item.id !== toast.id))}
              aria-label="Dismiss notification"
            >
              <X size={15} />
            </button>
          </div>
        );
      })}
    </div>
  );
}
