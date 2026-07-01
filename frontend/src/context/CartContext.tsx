import { createContext, useContext, useState, type ReactNode } from "react";
import { apiClient } from "../api/client";
import type { CartItem } from "../types";
import { useAuth } from "./AuthContext";

interface CartContextValue {
  items: CartItem[];
  loading: boolean;
  refresh: () => Promise<void>;
  addItem: (productId: number, quantity: number) => Promise<void>;
  removeItem: (cartItemId: number) => Promise<void>;
  totalCount: number;
  totalAmount: number;
}

const CartContext = createContext<CartContextValue | undefined>(undefined);

export function CartProvider({ children }: { children: ReactNode }) {
  const { user } = useAuth();
  const [items, setItems] = useState<CartItem[]>([]);
  const [loading, setLoading] = useState(false);

  const refresh = async () => {
    if (!user) {
      setItems([]);
      return;
    }
    setLoading(true);
    try {
      const res = await apiClient.get<CartItem[]>("/cart");
      setItems(res.data);
    } finally {
      setLoading(false);
    }
  };

  const addItem = async (productId: number, quantity: number) => {
    await apiClient.post("/cart/items", { productId, quantity });
    await refresh();
  };

  const removeItem = async (cartItemId: number) => {
    await apiClient.delete(`/cart/items/${cartItemId}`);
    await refresh();
  };

  const totalCount = items.reduce((sum, item) => sum + item.quantity, 0);
  const totalAmount = items.reduce((sum, item) => sum + item.subtotal, 0);

  return (
    <CartContext.Provider value={{ items, loading, refresh, addItem, removeItem, totalCount, totalAmount }}>
      {children}
    </CartContext.Provider>
  );
}

export function useCart(): CartContextValue {
  const ctx = useContext(CartContext);
  if (!ctx) throw new Error("useCart must be used within CartProvider");
  return ctx;
}
