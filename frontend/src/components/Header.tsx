import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useCart } from "../context/CartContext";

export function Header() {
  const { user, logout } = useAuth();
  const { totalCount } = useCart();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  return (
    <header className="site-header">
      <Link to="/" className="logo">
        PC Parts EC
      </Link>
      <nav className="nav">
        <Link to="/">商品一覧</Link>
        {user && <Link to="/orders">注文履歴</Link>}
        {user?.role === "ADMIN" && <Link to="/admin/products">商品管理</Link>}
        <Link to="/cart">カート ({totalCount})</Link>
        {user ? (
          <>
            <span className="user-name">{user.name} さん</span>
            <button onClick={handleLogout}>ログアウト</button>
          </>
        ) : (
          <>
            <Link to="/login">ログイン</Link>
            <Link to="/register">会員登録</Link>
          </>
        )}
      </nav>
    </header>
  );
}
