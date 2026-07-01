import { useEffect, useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { apiClient } from "../api/client";
import { useCart } from "../context/CartContext";
import type { CheckoutResponse } from "../types/checkout";

export function CartPage() {
  const { items, refresh, removeItem, totalAmount, loading } = useCart();
  const [shippingAddress, setShippingAddress] = useState("");
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    refresh();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleCheckout = async (e: FormEvent) => {
    e.preventDefault();
    setError("");
    setSubmitting(true);
    try {
      const res = await apiClient.post<CheckoutResponse>("/payments/checkout", { shippingAddress });
      window.location.href = res.data.checkoutUrl;
    } catch {
      setError("決済セッションの作成に失敗しました");
      setSubmitting(false);
    }
  };

  if (loading) return <p>読み込み中...</p>;

  if (items.length === 0) {
    return (
      <div className="cart-page">
        <h2>カート</h2>
        <p>カートは空です</p>
        <button onClick={() => navigate("/")}>商品一覧に戻る</button>
      </div>
    );
  }

  return (
    <div className="cart-page">
      <h2>カート</h2>
      <table className="cart-table">
        <thead>
          <tr>
            <th>商品</th>
            <th>単価</th>
            <th>数量</th>
            <th>小計</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {items.map((item) => (
            <tr key={item.id}>
              <td>{item.product.name}</td>
              <td>¥{item.product.price.toLocaleString()}</td>
              <td>{item.quantity}</td>
              <td>¥{item.subtotal.toLocaleString()}</td>
              <td>
                <button onClick={() => removeItem(item.id)}>削除</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      <p className="cart-total">合計: ¥{totalAmount.toLocaleString()}</p>

      <form onSubmit={handleCheckout} className="checkout-form">
        <label>
          お届け先住所
          <input
            type="text"
            value={shippingAddress}
            onChange={(e) => setShippingAddress(e.target.value)}
            required
          />
        </label>
        {error && <p className="error-message">{error}</p>}
        <button type="submit" disabled={submitting}>
          {submitting ? "処理中..." : "Stripeで決済する"}
        </button>
      </form>
    </div>
  );
}
