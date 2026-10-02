/**
 * MetaPro Enterprises - Spring Boot REST API Client & Adapter
 * 
 * This file is pre-configured to connect this React frontend to your Spring Boot
 * REST API and PostgreSQL database running locally or in production.
 * 
 * Default local URL: http://localhost:8080/api/v1
 * Configure via .env: VITE_API_BASE_URL="http://localhost:8080/api/v1"
 */

import { Product, ProductFormData, Order, OrderCustomerDetails } from '../types/product';

export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';

// Toggle between localStorage MVP and Spring Boot REST API
export const USE_SPRING_BOOT_API = import.meta.env.VITE_USE_SPRING_BOOT === 'true';

// Helper for standard JSON REST requests with Bearer token support
async function request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const token = localStorage.getItem('metapro_admin_token') || localStorage.getItem('metapro_customer_token');
  
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string>),
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    const errorBody = await response.text();
    throw new Error(`API error ${response.status}: ${errorBody || response.statusText}`);
  }

  return response.json();
}

/**
 * Spring Boot REST API Endpoints
 */
export const springBootApi = {
  // --- Products (Public & Admin) ---
  products: {
    // GET /api/v1/products
    getAll: () => request<Product[]>('/products'),

    // GET /api/v1/products/{id}
    getById: (id: string) => request<Product>(`/products/${id}`),

    // POST /api/v1/products (Admin only)
    create: (data: ProductFormData) => 
      request<Product>('/products', {
        method: 'POST',
        body: JSON.stringify(data),
      }),

    // PUT /api/v1/products/{id} (Admin only)
    update: (id: string, data: Partial<ProductFormData>) => 
      request<Product>(`/products/${id}`, {
        method: 'PUT',
        body: JSON.stringify(data),
      }),

    // DELETE /api/v1/products/{id} (Admin only)
    delete: (id: string) => 
      request<{ success: boolean }>(`/products/${id}`, {
        method: 'DELETE',
      }),

    // PATCH /api/v1/products/{id}/availability (Admin only)
    toggleAvailability: (id: string) => 
      request<Product>(`/products/${id}/availability`, {
        method: 'PATCH',
      }),
  },

  // --- Orders & Invoicing ---
  orders: {
    // POST /api/v1/orders
    create: (orderPayload: { customer: OrderCustomerDetails; items: { productId: string; quantity: number }[] }) =>
      request<Order>('/orders', {
        method: 'POST',
        body: JSON.stringify(orderPayload),
      }),

    // GET /api/v1/orders/my-orders (Customer only)
    getCustomerOrders: () => request<Order[]>('/orders/my-orders'),

    // GET /api/v1/orders (Admin only)
    getAllOrders: () => request<Order[]>('/orders'),

    // PATCH /api/v1/orders/{id}/status (Admin only)
    updateStatus: (id: string, status: string) =>
      request<Order>(`/orders/${id}/status`, {
        method: 'PATCH',
        body: JSON.stringify({ status }),
      }),
  },

  // --- Authentication ---
  auth: {
    // POST /api/v1/auth/admin/login
    adminLogin: (credentials: { username: string; password: string }) =>
      request<{ token: string; username: string }>('/auth/admin/login', {
        method: 'POST',
        body: JSON.stringify(credentials),
      }),

    // POST /api/v1/auth/customer/login
    customerLogin: (credentials: { phoneOrEmail: string; password: string }) =>
      request<{ token: string; user: any }>('/auth/customer/login', {
        method: 'POST',
        body: JSON.stringify(credentials),
      }),

    // POST /api/v1/auth/customer/register
    customerRegister: (userData: any) =>
      request<{ token: string; user: any }>('/auth/customer/register', {
        method: 'POST',
        body: JSON.stringify(userData),
      }),
  }
};
