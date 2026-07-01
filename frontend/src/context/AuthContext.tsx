import { createContext, useContext, useState, type ReactNode } from "react";
import { apiClient } from "../api/client";
import type { AuthUser } from "../types";

interface AuthContextValue {
  user: AuthUser | null;
  login: (email: string, password: string) => Promise<void>;
  register: (email: string, password: string, name: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

function loadStoredUser(): AuthUser | null {
  const raw = localStorage.getItem("auth");
  return raw ? (JSON.parse(raw) as AuthUser) : null;
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(loadStoredUser());

  const persist = (auth: AuthUser) => {
    localStorage.setItem("auth", JSON.stringify(auth));
    setUser(auth);
  };

  const login = async (email: string, password: string) => {
    const res = await apiClient.post<AuthUser>("/auth/login", { email, password });
    persist(res.data);
  };

  const register = async (email: string, password: string, name: string) => {
    const res = await apiClient.post<AuthUser>("/auth/register", { email, password, name });
    persist(res.data);
  };

  const logout = () => {
    localStorage.removeItem("auth");
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
