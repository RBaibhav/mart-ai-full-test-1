import React, { useEffect, useState } from 'react';
import { api } from '../api/client';
import { X, Check, ShoppingCart, Trash2 } from 'lucide-react';

export function ComparisonModal({ productIds, onClose, onRemoveFromCompare, onAddToCart }) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (productIds.length > 0) {
      setLoading(true);
      api.compareProducts(productIds)
        .then((res) => setData(res))
        .catch((err) => console.error(err))
        .finally(() => setLoading(false));
    }
  }, [productIds]);

  if (productIds.length === 0) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" style={{ maxWidth: '950px' }} onClick={(e) => e.stopPropagation()}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1.25rem', borderBottom: '1px solid var(--border)', paddingBottom: '0.75rem' }}>
          <div>
            <h2 style={{ fontSize: '1.25rem', fontWeight: 700 }}>Side-by-Side Product Comparison</h2>
            <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Comparing {productIds.length} products</p>
          </div>
          <button className="btn btn-secondary btn-sm" onClick={onClose}>
            <X size={18} />
          </button>
        </div>

        {loading ? (
          <div style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>
            Loading comparison details...
          </div>
        ) : !data || data.products.length === 0 ? (
          <div style={{ padding: '2rem', textAlign: 'center' }}>No products found to compare.</div>
        ) : (
          <div style={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.875rem' }}>
              <thead>
                <tr style={{ borderBottom: '2px solid var(--border)' }}>
                  <th style={{ padding: '0.75rem', width: '180px', color: 'var(--text-muted)', fontWeight: 600 }}>Attribute</th>
                  {data.products.map((p) => (
                    <th key={p.id} style={{ padding: '0.75rem', minWidth: '220px', verticalAlign: 'top' }}>
                      <div style={{ display: 'flex', flexDirection: 'column', gap: '0.4rem' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                          <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontWeight: 600 }}>{p.brand}</span>
                          <button
                            onClick={() => onRemoveFromCompare(p.id)}
                            style={{ color: 'var(--danger)', padding: '2px' }}
                            title="Remove from comparison"
                          >
                            <Trash2 size={14} />
                          </button>
                        </div>
                        <span style={{ fontWeight: 600, fontSize: '0.9rem' }}>{p.name}</span>
                        <div style={{ fontSize: '1.1rem', fontWeight: 700, color: 'var(--primary)' }}>
                          ${Number(p.price).toFixed(2)}
                        </div>
                        <button
                          className="btn btn-primary btn-sm"
                          disabled={p.availability === 'OUT_OF_STOCK'}
                          onClick={() => onAddToCart(p.id, 1)}
                          style={{ marginTop: '0.25rem' }}
                        >
                          <ShoppingCart size={13} />
                          Add to Cart
                        </button>
                      </div>
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {/* Rating row */}
                <tr style={{ borderBottom: '1px solid var(--border)', backgroundColor: '#f8fafc' }}>
                  <td style={{ padding: '0.75rem', fontWeight: 600 }}>Customer Rating</td>
                  {data.products.map((p) => (
                    <td key={p.id} style={{ padding: '0.75rem' }}>
                      ⭐ {Number(p.rating).toFixed(1)} / 5.0
                    </td>
                  ))}
                </tr>

                {/* Stock row */}
                <tr style={{ borderBottom: '1px solid var(--border)' }}>
                  <td style={{ padding: '0.75rem', fontWeight: 600 }}>Availability</td>
                  {data.products.map((p) => (
                    <td key={p.id} style={{ padding: '0.75rem' }}>
                      <span className={`badge ${p.availability === 'OUT_OF_STOCK' ? 'badge-stock-out' : 'badge-stock-in'}`}>
                        {p.availability === 'OUT_OF_STOCK' ? 'Out of Stock' : 'In Stock'}
                      </span>
                    </td>
                  ))}
                </tr>

                {/* Specification rows from comparisonMatrix */}
                {data.commonAttributeKeys.map((key) => (
                  <tr key={key} style={{ borderBottom: '1px solid var(--border)' }}>
                    <td style={{ padding: '0.75rem', fontWeight: 600, color: 'var(--text-muted)' }}>{key}</td>
                    {data.products.map((p, pIdx) => {
                      const val = data.comparisonMatrix[key]?.[pIdx] || '-';
                      return (
                        <td key={p.id} style={{ padding: '0.75rem' }}>
                          {String(val)}
                        </td>
                      );
                    })}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
