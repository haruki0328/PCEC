export interface Category {
  id: number;
  name: string;
  slug: string;
}

export interface Product {
  id: number;
  name: string;
  manufacturer: string;
  description: string;
  specs: string;
  price: number;
  stockQuantity: number;
  imageUrl: string;
  category: Category;
}

export interface ProductPage {
  content: Product[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
}

export interface CartItem {
  id: number;
  product: Product;
  quantity: number;
  subtotal: number;
}

export type OrderStatus = "PENDING" | "PAID" | "SHIPPED" | "CANCELLED";

export interface OrderItem {
  productId: number;
  productName: string;
  priceAtPurchase: number;
  quantity: number;
}

export interface Order {
  id: number;
  status: OrderStatus;
  totalAmount: number;
  shippingAddress: string;
  items: OrderItem[];
  createdAt: string;
}

export type Role = "USER" | "ADMIN";

export interface AuthUser {
  token: string;
  userId: number;
  email: string;
  name: string;
  role: Role;
}
