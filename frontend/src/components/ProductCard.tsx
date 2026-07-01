import { Link } from "react-router-dom";
import type { Product } from "../types";

export function ProductCard({ product }: { product: Product }) {
  return (
    <Link to={`/products/${product.id}`} className="product-card">
      <div className="product-card-image-placeholder">{product.category.name}</div>
      <div className="product-card-body">
        <p className="product-manufacturer">{product.manufacturer}</p>
        <h3>{product.name}</h3>
        <p className="product-price">¥{product.price.toLocaleString()}</p>
        <p className={product.stockQuantity > 0 ? "in-stock" : "out-of-stock"}>
          {product.stockQuantity > 0 ? `在庫あり (${product.stockQuantity})` : "在庫切れ"}
        </p>
      </div>
    </Link>
  );
}
