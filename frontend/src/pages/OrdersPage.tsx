import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { apiClient } from "../api/client";
import type { Order } from "../types";

const statusLabels: Record<string, string> = {
  PENDING: "支払い待ち",
  PAID: "支払い完了",
  SHIPPED: "発送済み",
  CANCELLED: "キャンセル",
};

export function OrdersPage() {
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    apiClient
      .get<Order[]>("/orders")
      .then((res) => setOrders(res.data))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p>読み込み中...</p>;

  if (orders.length === 0) return <p>注文履歴がありません</p>;

  return (
    <div className="orders-page">
      <h2>注文履歴</h2>
      <ul className="order-list">
        {orders.map((order) => (
          <li key={order.id}>
            <Link to={`/orders/${order.id}`}>
              注文 #{order.id} - {statusLabels[order.status]} - ¥{order.totalAmount.toLocaleString()} -{" "}
              {new Date(order.createdAt).toLocaleString()}
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
}
