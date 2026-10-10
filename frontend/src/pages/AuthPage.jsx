import React, { useState } from "react";
import { ArrowRight, Check, LockKeyhole, Mail, ShoppingBag, UserRound } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { DEMO_ACCOUNTS, useAuth } from "../context/AuthContext";

export function AuthPage({ initialMode = "signin" }) {
  const { login, register, loading } = useAuth();
  const navigate = useNavigate();
  const [mode, setMode] = useState(initialMode);
  const [name, setName] = useState("");
  const [email, setEmail] = useState("alice@example.com");
  const [password, setPassword] = useState("password123");
  const [error, setError] = useState("");

  const submit = async (event) => {
    event.preventDefault();
    setError("");
    try {
      if (mode === "signup") {
        await register(name, email, password);
      } else {
        await login(email, password);
      }
      navigate("/catalog", { replace: true });
    } catch (err) {
      setError(err.message || "We could not complete that request.");
    }
  };

  const useDemoAccount = (account) => {
    setMode("signin");
    setEmail(account.email);
    setPassword(account.password);
    setError("");
  };

  return (
    <main className="auth-page">
      <section className="auth-panel auth-story">
        <div className="auth-brand">
          <span className="brand-mark">
            <ShoppingBag size={19} />
          </span>{" "}
          ShopSmart <b>AI</b>
        </div>
        <div className="auth-story-copy">
          <div className="eyebrow">Your smarter shopping workspace</div>
          <h1>Make every purchase feel considered.</h1>
          <p>
            Search the catalog, compare what matters, and get grounded help from one assistant that
            understands your orders and policies.
          </p>
          <div className="auth-proof">
            <span>
              <Check size={15} /> Verified catalog
            </span>
            <span>
              <Check size={15} /> Explainable AI
            </span>
            <span>
              <Check size={15} /> Secure sessions
            </span>
          </div>
        </div>
        <div className="auth-footer">
          ShopSmart AI <span>•</span> Enterprise demo workspace
        </div>
      </section>

      <section className="auth-panel auth-form-panel">
        <div className="auth-form-wrap">
          <div className="auth-form-heading">
            <div className="eyebrow">Welcome back</div>
            <h2>
              {mode === "signin" ? "Sign in to your workspace" : "Create your workspace account"}
            </h2>
            <p>
              {mode === "signin"
                ? "Use a demo account or your ShopSmart credentials to continue."
                : "New accounts start with Basic membership and can personalize preferences later."}
            </p>
          </div>

          <div className="auth-tabs" role="tablist">
            <button
              className={mode === "signin" ? "active" : ""}
              onClick={() => {
                setMode("signin");
                setError("");
                navigate("/signin");
              }}
              role="tab"
            >
              Sign in
            </button>
            <button
              className={mode === "signup" ? "active" : ""}
              onClick={() => {
                setMode("signup");
                setError("");
                navigate("/signup");
              }}
              role="tab"
            >
              Sign up
            </button>
          </div>

          <form className="auth-form" onSubmit={submit}>
            {mode === "signup" && (
              <label>
                <span>Full name</span>
                <div className="auth-input">
                  <UserRound size={16} />
                  <input
                    value={name}
                    onChange={(event) => setName(event.target.value)}
                    placeholder="Jordan Lee"
                    required
                  />
                </div>
              </label>
            )}
            <label>
              <span>Email address</span>
              <div className="auth-input">
                <Mail size={16} />
                <input
                  type="email"
                  value={email}
                  onChange={(event) => setEmail(event.target.value)}
                  placeholder="you@company.com"
                  required
                />
              </div>
            </label>
            <label>
              <span>Password</span>
              <div className="auth-input">
                <LockKeyhole size={16} />
                <input
                  type="password"
                  value={password}
                  onChange={(event) => setPassword(event.target.value)}
                  minLength={8}
                  placeholder="At least 8 characters"
                  required
                />
              </div>
            </label>
            {error && <div className="auth-error">{error}</div>}
            <button className="btn btn-primary auth-submit" disabled={loading} type="submit">
              {loading ? "Connecting..." : mode === "signin" ? "Sign in" : "Create account"}{" "}
              <ArrowRight size={16} />
            </button>
          </form>

          {mode === "signin" && (
            <div className="demo-accounts">
              <div className="demo-heading">
                <span>Demo accounts</span>
                <small>Ready to explore</small>
              </div>
              {DEMO_ACCOUNTS.slice(0, 3).map((account) => (
                <button key={account.email} onClick={() => useDemoAccount(account)}>
                  <span className="demo-avatar">{account.name.slice(0, 1)}</span>
                  <span>
                    <strong>{account.name}</strong>
                    <small>
                      {account.role} · {account.email}
                    </small>
                  </span>
                  <ArrowRight size={14} />
                </button>
              ))}
            </div>
          )}
        </div>
      </section>
    </main>
  );
}
