import { useEffect, useState, type FormEvent } from "react";
import { apiClient } from "../api/client";
import type { Category, Product, ProductPage } from "../types";

interface FormState {
  id: number | null;
  name: string;
  manufacturer: string;
  description: string;
  specs: string;
  price: string;
  stockQuantity: string;
  imageUrl: string;
  categoryId: string;
}

const emptyForm: FormState = {
  id: null,
  name: "",
  manufacturer: "",
  description: "",
  specs: "",
  price: "",
  stockQuantity: "",
  imageUrl: "",
  categoryId: "",
};

export function AdminProductsPage() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [form, setForm] = useState<FormState>(emptyForm);
  const [error, setError] = useState("");

  const loadCategories = () => apiClient.get<Category[]>("/categories").then((res) => setCategories(res.data));
  const loadProducts = () =>
    apiClient.get<ProductPage>("/products", { params: { size: 100 } }).then((res) => setProducts(res.data.content));

  useEffect(() => {
    loadCategories();
    loadProducts();
  }, []);

  const handleEdit = (product: Product) => {
    setForm({
      id: product.id,
      name: product.name,
      manufacturer: product.manufacturer,
      description: product.description,
      specs: product.specs,
      price: String(product.price),
      stockQuantity: String(product.stockQuantity),
      imageUrl: product.imageUrl,
      categoryId: String(product.category.id),
    });
  };

  const handleDelete = async (id: number) => {
    await apiClient.delete(`/admin/products/${id}`);
    await loadProducts();
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError("");
    const payload = {
      name: form.name,
      manufacturer: form.manufacturer,
      description: form.description,
      specs: form.specs,
      price: Number(form.price),
      stockQuantity: Number(form.stockQuantity),
      imageUrl: form.imageUrl,
      categoryId: Number(form.categoryId),
    };
    try {
      if (form.id) {
        await apiClient.put(`/admin/products/${form.id}`, payload);
      } else {
        await apiClient.post("/admin/products", payload);
      }
      setForm(emptyForm);
      await loadProducts();
    } catch {
      setError("保存に失敗しました。入力内容を確認してください");
    }
  };

  return (
    <div className="admin-products-page">
      <h2>商品管理</h2>

      <form onSubmit={handleSubmit} className="admin-product-form">
        <h3>{form.id ? `商品を編集 (#${form.id})` : "新規商品を追加"}</h3>
        <label>
          商品名
          <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
        </label>
        <label>
          メーカー
          <input
            value={form.manufacturer}
            onChange={(e) => setForm({ ...form, manufacturer: e.target.value })}
            required
          />
        </label>
        <label>
          カテゴリ
          <select
            value={form.categoryId}
            onChange={(e) => setForm({ ...form, categoryId: e.target.value })}
            required
          >
            <option value="">選択してください</option>
            {categories.map((c) => (
              <option key={c.id} value={c.id}>
                {c.name}
              </option>
            ))}
          </select>
        </label>
        <label>
          価格
          <input
            type="number"
            value={form.price}
            onChange={(e) => setForm({ ...form, price: e.target.value })}
            required
          />
        </label>
        <label>
          在庫数
          <input
            type="number"
            value={form.stockQuantity}
            onChange={(e) => setForm({ ...form, stockQuantity: e.target.value })}
            required
          />
        </label>
        <label>
          説明
          <textarea value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
        </label>
        <label>
          スペック
          <textarea value={form.specs} onChange={(e) => setForm({ ...form, specs: e.target.value })} />
        </label>
        <label>
          画像URL
          <input value={form.imageUrl} onChange={(e) => setForm({ ...form, imageUrl: e.target.value })} />
        </label>
        {error && <p className="error-message">{error}</p>}
        <div className="form-actions">
          <button type="submit">{form.id ? "更新" : "追加"}</button>
          {form.id && (
            <button type="button" onClick={() => setForm(emptyForm)}>
              キャンセル
            </button>
          )}
        </div>
      </form>

      <table className="admin-product-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>商品名</th>
            <th>カテゴリ</th>
            <th>価格</th>
            <th>在庫</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {products.map((p) => (
            <tr key={p.id}>
              <td>{p.id}</td>
              <td>{p.name}</td>
              <td>{p.category.name}</td>
              <td>¥{p.price.toLocaleString()}</td>
              <td>{p.stockQuantity}</td>
              <td>
                <button onClick={() => handleEdit(p)}>編集</button>
                <button onClick={() => handleDelete(p.id)}>削除</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
