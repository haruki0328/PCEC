import axios from "axios";

export const apiClient = axios.create({
  baseURL: "http://localhost:8080/api",
});

apiClient.interceptors.request.use((config) => {
  const raw = localStorage.getItem("auth");
  if (raw) {
    const auth = JSON.parse(raw);
    config.headers.Authorization = `Bearer ${auth.token}`;
  }
  return config;
});
