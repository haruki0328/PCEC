import { useEffect, useState } from "react";
import { apiClient } from "../api/client";
import { ProductCard } from "../components/ProductCard";
import type { Category, ProductPage } from "../types";

export function HomePage() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [selectedCategory, setSelectedCategory] = useState("");
  const [keyword, setKeyword] = useState("");
  const [productPage, setProductPage] = useState<ProductPage | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    apiClient.get<Category[]>("/categories").then((res) => setCategories(res.data));
  }, []);

  useEffect(() => {
    setLoading(true);
    const params: Record<string, string> = {};
    if (selectedCategory) params.category = selectedCategory;
    if (keyword) params.keyword = keyword;
    apiClient
      .get<ProductPage>("/products", { params })
      .then((res) => setProductPage(res.data))
      .finally(() => setLoading(false));
  }, [selectedCategory, keyword]);

  return (
    <div className="home-page">
      <aside className="category-filter">
        <h3>カテゴリ</h3>
        <ul>
          <li>
            <button
              className={selectedCategory === "" ? "active" : ""}
              onClick={() => setSelectedCategory("")}
            >
              すべて
            </button>
          </li>
          {categories.map((c) => (
            <li key={c.id}>
              <button
                className={selectedCategory === c.slug ? "active" : ""}
                onClick={() => setSelectedCategory(c.slug)}
              >
                {c.name}
              </button>
            </li>
          ))}
        </ul>
      </aside>
      <main className="product-list-area">
        <input
          type="text"
          placeholder="商品名で検索"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          className="search-input"
        />
        {loading && <p>読み込み中...</p>}
        <div className="product-grid">
          {productPage?.content.map((p) => (
            <ProductCard key={p.id} product={p} />
          ))}
        </div>
        {productPage && productPage.content.length === 0 && !loading && <p>商品が見つかりませんでした</p>}
      </main>
    </div>
  );
}
