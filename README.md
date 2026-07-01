# PC Parts EC

PCパーツ専門のECサイト。Spring Boot (Java) + React (TypeScript) + MySQL構成。

## 構成

- `backend/` : Spring Boot 3 (Java 17) REST API
- `frontend/` : React + TypeScript (Vite)
- `scripts/start-mysql.ps1` : ローカルMySQLの起動スクリプト

## 起動手順

1. MySQLを起動 (サービス化していないため、再起動後は毎回実行が必要)
   ```powershell
   .\scripts\start-mysql.ps1
   ```

2. バックエンド起動 (http://localhost:8080)
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   初回起動時にカテゴリ・商品のサンプルデータと管理者アカウントが自動投入されます。

3. フロントエンド起動 (http://localhost:5173)
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

## アカウント情報

管理者アカウント(admin@pcparts.local)のパスワードとMySQLアプリ用ユーザー(ecapp)のパスワードは、
環境変数 `ADMIN_PASSWORD` / `DB_PASSWORD` で指定します（未指定時は開発用の仮パスワード `local-dev-only` が使われます）。

```bash
export DB_PASSWORD=your-local-password
export ADMIN_PASSWORD=your-admin-password
```

一般ユーザーは会員登録画面から作成できます。

## Stripeテスト決済の有効化

現状は `STRIPE_SECRET_KEY` が未設定のためチェックアウトはエラーになります。
Stripeのテスト用APIキー ( https://dashboard.stripe.com/test/apikeys ) を取得し、
バックエンド起動時に環境変数として渡してください。

```bash
export STRIPE_SECRET_KEY=sk_test_xxxxx
export STRIPE_PUBLISHABLE_KEY=pk_test_xxxxx
export STRIPE_WEBHOOK_SECRET=whsec_xxxxx   # stripe listen で発行される値
```

決済完了後のWebhook (`checkout.session.completed`) を受け取るには、Stripe CLIでローカル転送してください。

```bash
stripe listen --forward-to localhost:8080/api/payments/webhook
```

## 主な機能

- 商品一覧・検索・カテゴリ絞り込み・商品詳細
- 会員登録・ログイン (JWT + Spring Security)
- カート機能
- Stripe Checkoutによる決済
- 注文履歴
- 管理者用の商品・カテゴリCRUD (ADMINロールのみ)
