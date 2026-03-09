// Frontend API helper with automatic token injection
export const API_BASE = (typeof window !== 'undefined' && window.__API_BASE__) ||
  (process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080');
// Allowed frontend origins pattern for CORS with credentials
const ALLOWED_FRONTENDS = ['http://localhost:3000'];

/**
 * Lightweight fetch wrapper that automatically supplies Authorization header
 * when a token is present in localStorage.
 * - Uses 'token' for standard frontend login flow
 * - Uses 'admin_token' for admin UI
 * - Prefixes relative URLs with API_BASE
 */
export async function apiFetch(input, init = {}) {
  let url;
  if (typeof input === 'string') url = input;
  else if (input && input.url) url = input.url;
  else url = '';

  // Resolve relative URLs to API_BASE
  if (!/^https?:\/\//i.test(url)) {
    const base = API_BASE.replace(/\\+$/, '');
    const path = url.replace(/^\//, '');
    url = base + '/' + path;
  }

  // Prepare headers
  const headers = Object.assign({}, init.headers || {});

  // Debug: log outgoing request details
  try {
    const method = (init && init.method) || 'GET'
    const representativeUrl = typeof input === 'string' ? input : (input && input.url)
    console.debug('[apiFetch] request', { url: representativeUrl, method, headers: Object.keys(headers) })
  } catch {
    // ignore logging failures
  }

  // Attach token if not explicitly provided
  if (!headers['Authorization']) {
    if (typeof window !== 'undefined') {
      const token = localStorage.getItem('token');
      const adminToken = localStorage.getItem('admin_token');
      const origin = window.location ? window.location.origin : '';
      const isAllowedFrontend = ALLOWED_FRONTENDS.includes(origin);
      // If request is admin and origin is allowed, use admin token when available
      if (url.startsWith('/admin/') && isAllowedFrontend && adminToken) {
        headers['Authorization'] = 'Bearer ' + adminToken;
      } else if (token) {
        headers['Authorization'] = 'Bearer ' + token;
      }
    }
  }

  const merged = Object.assign({}, init, { headers });
  return fetch(url, merged);
}

export default apiFetch;
