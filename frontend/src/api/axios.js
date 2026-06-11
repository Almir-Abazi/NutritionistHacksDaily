import axios from 'axios'

// Relative base: dev requests go through the Vite proxy (vite.config.js),
// production requests through nginx (frontend/nginx.conf) — both target the backend.
const api = axios.create({
  baseURL: '/api/v1',
})

// useAuthStore is imported inside the interceptor (not at top level)
// to avoid the Pinia circular init issue.
api.interceptors.request.use(async (config) => {
  const { useAuthStore } = await import('../stores/authStore')
  const authStore = useAuthStore()
  if (authStore.token) {
    config.headers.Authorization = `Bearer ${authStore.token}`
  }
  return config
})

export default api
