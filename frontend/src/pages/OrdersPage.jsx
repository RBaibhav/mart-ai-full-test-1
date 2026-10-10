import React, { useState, useEffect } from 'react';
import { api } from '../api/client';
import { Package, Truck, CheckCircle, Clock, ChevronDown, ChevronUp, AlertCircle } from 'lucide-react';

export function OrdersPage({ onAskAssistantAboutOrder }) {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [expandedOrderId, setExpandedOrderId] = useState(null);
  const [orderDetails, setOrderDetails] = useState({});

  useEffect(() => {
    setLoading(true);
    api.getOrders()
      .then((data) => {
        setOrders(data);
        if (data.length > 0) {
          // Auto expand first order
          loadOrderDetail(data[0].id);
        }
      })
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  const loadOrderDetail = async (orderId) => {
    if (expandedOrderId === orderId) {
      setExpandedOrderId(null);
      return;
    }
    setExpandedOrderId(orderId);
    if (!orderDetails[orderId]) {
      try {
        const detail = await api.getOrderById(orderId);
        setOrderDetails((prev) => ({ ...prev, [orderId]: detail }));
      } catch (err) {
        console.error(err);
      }
    }
  };

  const getStatusBadge = (status) => {
    switch (status) {
      case 'DELIVERED':
        return <span className="badge badge-stock-in"><CheckCircle size={12} style={{ marginRight: 3 }} /> Delivered</span>;
      case 'IN_TRANSIT':
      case 'OUT_FOR_DELIVERY':
      case 'SHIPPED':
        return <span className="badge badge-prime"><Truck size={12} style={{ marginRight: 3 }} /> {status}</span>;
      default:
        return <span className="badge badge-basic"><Clock size={12} style={{ marginRight: 3 }} /> {status}</span>;
    }
  };

  return (
    <div style={{ padding: '2rem 0' }}>
      <div className="container">
        <h1 style={{ fontSize: '1.75rem', fontWeight: 800, marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <Package size={26} color="var(--primary)" /> Simulated Orders & Shipment Tracking
        </h1>

        {loading ? (
          <div className="card" style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>
            Loading customer orders...
          </div>
        ) : orders.length === 0 ? (
          <div className="card" style={{ padding: '4rem 2rem', textAlign: 'center' }}>
            <h2 style={{ fontSize: '1.25rem', fontWeight: 700, marginBottom: '0.5rem' }}>No orders found</h2>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
              Simulated customer orders are linked to seeded customer accounts. Switch demo accounts to view simulated orders.
            </p>
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
            {orders.map((order) => {
              const isExpanded = expandedOrderId === order.id;
              const detail = orderDetails[order.id];

              return (
                <div key={order.id} className="card" style={{ padding: '1.25rem' }}>
                  {/* Order Summary Header */}
                  <div style={{ display: 'flex', flexWrap: 'wrap', alignItems: 'center', justifyContent: 'space-between', gap: '1rem' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                      <div style={{ backgroundColor: 'var(--primary-light)', color: 'var(--primary)', padding: '0.65rem', borderRadius: '8px' }}>
                        <Package size={22} />
                      </div>
                      <div>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                          <span style={{ fontWeight: 700, fontSize: '1rem' }}>Order #{order.id.substring(0, 8)}</span>
                          {getStatusBadge(order.status)}
                        </div>
                        <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginTop: '2px' }}>
                          Placed on {new Date(order.orderDate).toLocaleDateString()} • {order.itemCount} items
                        </div>
                      </div>
                    </div>

                    <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
                      <div style={{ textAlign: 'right' }}>
                        <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Total Amount</div>
                        <div style={{ fontSize: '1.2rem', fontWeight: 800 }}>${Number(order.totalAmount).toFixed(2)}</div>
                      </div>

                      <button
                        className="btn btn-secondary btn-sm"
                        onClick={() => loadOrderDetail(order.id)}
                      >
                        {isExpanded ? <ChevronUp size={16} /> : <ChevronDown size={16} />}
                        {isExpanded ? 'Hide Details' : 'View Timeline'}
                      </button>
                    </div>
                  </div>

                  {/* Expanded Details & Mocked Shipment Events Timeline */}
                  {isExpanded && detail && (
                    <div style={{ marginTop: '1.25rem', borderTop: '1px solid var(--border)', paddingTop: '1.25rem' }}>
                      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '2rem' }}>
                        {/* Items in Order */}
                        <div>
                          <h4 style={{ fontSize: '0.85rem', fontWeight: 600, textTransform: 'uppercase', color: 'var(--text-muted)', marginBottom: '0.75rem' }}>
                            Purchased Products
                          </h4>
                          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                            {detail.items?.map((item, idx) => (
                              <div key={idx} style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', padding: '0.5rem', backgroundColor: '#f8fafc', borderRadius: '6px' }}>
                                <div style={{ width: '50px', height: '50px', borderRadius: '4px', overflow: 'hidden', backgroundColor: '#e2e8f0' }}>
                                  <img
                                    src={item.images?.[0] || 'https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=600'}
                                    alt={item.productName}
                                    style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                                  />
                                </div>
                                <div style={{ flex: 1 }}>
                                  <div style={{ fontSize: '0.85rem', fontWeight: 600 }}>{item.productName}</div>
                                  <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                                    Qty: {item.quantity} × ${Number(item.unitPrice).toFixed(2)}
                                  </div>
                                </div>
                                <div style={{ fontWeight: 700, fontSize: '0.9rem' }}>
                                  ${Number(item.subtotal).toFixed(2)}
                                </div>
                              </div>
                            ))}
                          </div>
                        </div>

                        {/* Mocked Shipment Events Timeline */}
                        <div>
                          <h4 style={{ fontSize: '0.85rem', fontWeight: 600, textTransform: 'uppercase', color: 'var(--text-muted)', marginBottom: '0.75rem' }}>
                            Mocked Delivery Events Timeline
                          </h4>
                          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', position: 'relative', paddingLeft: '1.5rem', borderLeft: '2px solid #e2e8f0', marginLeft: '0.5rem' }}>
                            {detail.shipmentEvents?.map((event, eIdx) => (
                              <div key={eIdx} style={{ position: 'relative' }}>
                                <div style={{
                                  position: 'absolute', left: '-1.85rem', top: '0', width: '10px', height: '10px',
                                  borderRadius: '50%', backgroundColor: eIdx === detail.shipmentEvents.length - 1 ? 'var(--primary)' : '#94a3b8',
                                  border: '2px solid #ffffff'
                                }} />
                                <div style={{ fontSize: '0.85rem', fontWeight: 600, color: 'var(--text-main)' }}>
                                  {event.status.replace(/_/g, ' ')}
                                </div>
                                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                                  {new Date(event.timestamp).toLocaleString()}
                                </div>
                                {event.estimatedDelivery && (
                                  <div style={{ fontSize: '0.75rem', color: 'var(--primary)', fontWeight: 500 }}>
                                    Estimated Delivery: {new Date(event.estimatedDelivery).toLocaleDateString()}
                                  </div>
                                )}
                              </div>
                            ))}
                          </div>

                          {/* Ask AI assistant about order */}
                          <div style={{ marginTop: '1.25rem' }}>
                            <button
                              className="btn btn-secondary btn-sm"
                              style={{ width: '100%', fontSize: '0.8rem' }}
                              onClick={() => onAskAssistantAboutOrder("Where is my order, and can I cancel after shipment?")}
                            >
                              💬 Ask Assistant: "Where is my order, and can I cancel after shipment?"
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}
