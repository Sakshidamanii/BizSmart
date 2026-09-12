/**
 * BizSmart Global API Client
 * Connects the Vercel React frontend to the live Java Spring Boot cloud backend.
 */

// Dynamically read from Vite environment variable, with fallback to empty string (client-side offline mode)
export const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || '').replace(/\/+$/, '');

/**
 * Retrieve saved JWT token from browser storage
 */
export function getAuthToken() {
  try {
    return localStorage.getItem('bizsmart_jwt_token') || '';
  } catch (e) {
    return '';
  }
}

/**
 * Save JWT token upon successful authentication
 */
export function setAuthToken(token) {
  try {
    if (token) {
      localStorage.setItem('bizsmart_jwt_token', token);
    } else {
      localStorage.removeItem('bizsmart_jwt_token');
    }
  } catch (e) {}
}

/**
 * Base fetch wrapper with JWT authorization headers and timeout
 */
async function request(endpoint, options = {}) {
  if (!API_BASE_URL) {
    throw new Error('No API_BASE_URL configured. Running in offline/browser mode.');
  }

  const url = `${API_BASE_URL}${endpoint.startsWith('/') ? endpoint : `/${endpoint}`}`;
  const token = getAuthToken();

  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...(options.headers || {})
  };

  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), options.timeout || 12000);

  try {
    const res = await fetch(url, {
      ...options,
      headers,
      signal: controller.signal
    });

    clearTimeout(timeoutId);

    if (!res.ok) {
      let errorMsg = `HTTP Error ${res.status}`;
      try {
        const errorJson = await res.json();
        errorMsg = errorJson.message || errorJson.error || errorMsg;
      } catch (_) {}
      throw new Error(errorMsg);
    }

    // Try parsing JSON or return raw text
    const text = await res.text();
    return text ? JSON.parse(text) : {};
  } catch (err) {
    clearTimeout(timeoutId);
    throw err;
  }
}

export const api = {
  // 1. Health check & Cloud Connection Status
  checkHealth: async () => {
    if (!API_BASE_URL) return { connected: false, reason: 'LOCAL_MODE' };
    try {
      const data = await request('/api/health', { timeout: 4000 });
      return { connected: data?.status === 'UP', data };
    } catch (e) {
      return { connected: false, error: e.message };
    }
  },

  // 2. Authentication
  login: async (username, password) => {
    const data = await request('/api/auth/signin', {
      method: 'POST',
      body: JSON.stringify({ username, password })
    });
    if (data?.token) {
      setAuthToken(data.token);
    }
    return data;
  },

  logout: () => {
    setAuthToken(null);
  },

  // 3. Products / Inventory
  getProducts: async () => {
    return request('/api/products');
  },

  createProduct: async (productData) => {
    return request('/api/products', {
      method: 'POST',
      body: JSON.stringify(productData)
    });
  },

  updateProduct: async (id, productData) => {
    return request(`/api/products/${id}`, {
      method: 'PUT',
      body: JSON.stringify(productData)
    });
  },

  getLowStockProducts: async () => {
    return request('/api/products/low-stock');
  },

  // 4. Customers & Khata
  getCustomers: async () => {
    return request('/api/customers');
  },

  createCustomer: async (customerData) => {
    return request('/api/customers', {
      method: 'POST',
      body: JSON.stringify(customerData)
    });
  },

  // 5. Orders / Sales Billing
  createOrder: async (orderPayload) => {
    return request('/api/orders', {
      method: 'POST',
      body: JSON.stringify(orderPayload)
    });
  },

  getOrders: async () => {
    return request('/api/orders');
  },

  // 6. Analytics & Dashboard Summary
  getAnalyticsSummary: async () => {
    return request('/api/analytics/dashboard');
  }
};

export default api;
