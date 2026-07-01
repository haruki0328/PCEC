import { useEffect, useState } from "react";
import { useSearchParams, useParams } from "react-router-dom";
import { apiClient } from "../api/client";
import { useCart } from "../context/CartContext";
import type { Order } from "../types";

const statusLabels: Record<string, string> = {
  PENDING: "支払い待ち",
  PAID: "支払い完了",
  SHIPPED: "発送済み",
  CANCELLED: "キャンセル",
};

export function OrderDetailPage() {
  const { id } = useParams();
  const [searchParams] = useSearchParams();
  const [order, setOrder] = useState<Order | null>(null);
  const { refresh } = useCart();

  useEffect(() => {
    apiClient.get<Order>(`/orders/${id}`).then((res) => setOrder(res.data));
    if (searchParams.get("success") === "true") {
      refresh();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  if (!order) return <p>読み込み中...</p>;

  return (
    <div className="order-detail-page">
      {searchParams.get("success") === "true" && (
        <p className="success-message">お支払いが完了しました。ご注文ありがとうございます。</p>
      )}
      <h2>注文 #{order.id}</h2>
      <p>ステータス: {statusLabels[order.status]}</p>
      <p>お届け先: {order.shippingAddress}</p>
      <table className="cart-table">
        <thead>
          <tr>
            <th>商品</th>
            <th>単価</th>
            <th>数量</th>
          </tr>
        </thead>
        <tbody>
          {order.items.map((item, idx) => (
            <tr key={idx}>
              <td>{item.productName}</td>
              <td>¥{item.priceAtPurchase.toLocaleString()}</td>
              <td>{item.quantity}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <p className="cart-total">合計: ¥{order.totalAmount.toLocaleString()}</p>
    </div>
  );
}
