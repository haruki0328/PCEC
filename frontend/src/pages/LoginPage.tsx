import { useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError("");
    try {
      await login(email, password);
      navigate("/");
    } catch {
      setError("メールアドレスまたはパスワードが正しくありません");
    }
  };

  return (
    <div className="auth-page">
      <h2>ログイン</h2>
      <form onSubmit={handleSubmit}>
        <label>
          メールアドレス
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </label>
        <label>
          パスワード
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        </label>
        {error && <p className="error-message">{error}</p>}
        <button type="submit">ログイン</button>
      </form>
      <p className="hint">管理者アカウント: admin@pcparts.local / Admin_2026_dev</p>
    </div>
  );
}
