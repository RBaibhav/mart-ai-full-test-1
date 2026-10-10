import React, { useState, useEffect } from 'react';
import { api } from '../api/client';
import { ShieldCheck, Clock, Cpu, Wrench, Bookmark, RefreshCw } from 'lucide-react';

export function AuditPage() {
  const [audits, setAudits] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchAudits = () => {
    setLoading(true);
    api.getAudits()
      .then((data) => setAudits(data))
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchAudits();
  }, []);

  const parseJsonSafe = (str) => {
    if (!str) return null;
    try {
      return JSON.parse(str);
    } catch (e) {
      return str;
    }
  };

  return (
    <div style={{ padding: '2rem 0' }}>
      <div className="container">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
          <div>
            <h1 style={{ fontSize: '1.75rem', fontWeight: 800, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <ShieldCheck size={26} color="var(--primary)" /> Explainability & AI Audit Log
            </h1>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
              Verifiable execution records of customer requests, model name, backend tool activity, citations, and outcomes.
            </p>
          </div>
          <button className="btn btn-secondary btn-sm" onClick={fetchAudits} disabled={loading}>
            <RefreshCw size={14} className={loading ? 'animate-spin' : ''} /> Refresh
          </button>
        </div>

        {loading ? (
          <div className="card" style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>
            Loading audit records...
          </div>
        ) : audits.length === 0 ? (
          <div className="card" style={{ padding: '4rem 2rem', textAlign: 'center' }}>
            <h2 style={{ fontSize: '1.25rem', fontWeight: 700, marginBottom: '0.5rem' }}>No audit records found yet</h2>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
              Interact with the AI Assistant or ask questions to generate audit trails.
            </p>
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {audits.map((record) => {
              const toolActivity = parseJsonSafe(record.toolActivity);
              const sourceReferences = parseJsonSafe(record.sourceReferences);

              return (
                <div key={record.id} className="card" style={{ padding: '1.25rem' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.75rem' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                      <span className={`badge ${record.outcome === 'SUCCESS' ? 'badge-stock-in' : 'badge-stock-out'}`}>
                        {record.outcome}
                      </span>
                      <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                        <Clock size={12} /> {new Date(record.timestamp).toLocaleString()}
                      </span>
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', fontSize: '0.75rem', backgroundColor: '#f1f5f9', padding: '0.2rem 0.5rem', borderRadius: '4px' }}>
                      <Cpu size={12} color="var(--primary)" />
                      <span>{record.modelName}</span>
                    </div>
                  </div>

                  {/* Request */}
                  <div style={{ marginBottom: '0.75rem' }}>
                    <div style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-muted)', textTransform: 'uppercase' }}>
                      Customer Request
                    </div>
                    <div style={{ fontWeight: 600, fontSize: '0.95rem', color: 'var(--text-main)', marginTop: '0.1rem' }}>
                      "{record.request}"
                    </div>
                  </div>

                  {/* AI Response Preview */}
                  <div style={{ marginBottom: '0.75rem', backgroundColor: '#f8fafc', padding: '0.75rem', borderRadius: '6px', fontSize: '0.85rem' }}>
                    <div style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-muted)', marginBottom: '0.25rem' }}>
                      AI Response:
                    </div>
                    <div style={{ color: 'var(--text-main)', whiteSpace: 'pre-line' }}>
                      {record.aiResponse}
                    </div>
                  </div>

                  {/* Tool Activity & Source Citations Grid */}
                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                    {/* Tool Activity */}
                    <div style={{ fontSize: '0.75rem', backgroundColor: '#ffffff', border: '1px solid var(--border)', borderRadius: '6px', padding: '0.5rem' }}>
                      <div style={{ fontWeight: 600, color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '0.3rem', marginBottom: '0.25rem' }}>
                        <Wrench size={12} /> Tool Activity
                      </div>
                      <pre style={{ margin: 0, fontSize: '0.7rem', overflowX: 'auto', whiteSpace: 'pre-wrap' }}>
                        {JSON.stringify(toolActivity, null, 2)}
                      </pre>
                    </div>

                    {/* Source References */}
                    <div style={{ fontSize: '0.75rem', backgroundColor: '#ffffff', border: '1px solid var(--border)', borderRadius: '6px', padding: '0.5rem' }}>
                      <div style={{ fontWeight: 600, color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '0.3rem', marginBottom: '0.25rem' }}>
                        <Bookmark size={12} /> Source References
                      </div>
                      <pre style={{ margin: 0, fontSize: '0.7rem', overflowX: 'auto', whiteSpace: 'pre-wrap' }}>
                        {JSON.stringify(sourceReferences, null, 2)}
                      </pre>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}
