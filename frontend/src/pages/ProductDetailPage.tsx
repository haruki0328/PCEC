import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { apiClient } from "../api/client";
import { useAuth } from "../context/AuthContext";
import { useCart } from "../context/CartContext";
import type { Product } from "../types";

export function ProductDetailPage() {
  const { id } = useParams();
  const [product, setProduct] = useState<Product | null>(null);
  const [quantity, setQuantity] = useState(1);
  const [message, setMessage] = useState("");
  const { user } = useAuth();
  const { addItem } = useCart();
  const navigate = useNavigate();

  useEffect(() => {
    apiClient.get<Product>(`/products/${id}`).then((res) => setProduct(res.data));
  }, [id]);

  if (!product) return <p>読み込み中...</p>;

  const handleAddToCart = async () => {
    if (!user) {
      navigate("/login");
      return;
    }
    await addItem(product.id, quantity);
    setMessage("カートに追加しました");
  };

  return (
    <div className="product-detail">
      <div className="product-detail-image-placeholder">{product.category.name}</div>
      <div className="product-detail-body">
        <p className="product-manufacturer">{product.manufacturer}</p>
        <h2>{product.name}</h2>
        <p className="product-price">¥{product.price.toLocaleString()}</p>
        <p>{product.description}</p>
        <pre className="product-specs">{product.specs}</pre>
        <p className={product.stockQuantity > 0 ? "in-stock" : "out-of-stock"}>
          {product.stockQuantity > 0 ? `在庫あり (${product.stockQuantity})` : "在庫切れ"}
        </p>
        {product.stockQuantity > 0 && (
          <div className="add-to-cart-form">
            <input
              type="number"
              min={1}
              max={product.stockQuantity}
              value={quantity}
              onChange={(e) => setQuantity(Number(e.target.value))}
            />
            <button onClick={handleAddToCart}>カートに追加</button>
          </div>
        )}
        {message && <p className="success-message">{message}</p>}
      </div>
    </div>
  );
}
