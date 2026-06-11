import { defineStore } from 'pinia'

// Token lives in memory only — no localStorage (ai-rules rule 12).
export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: null,
    isAuthenticated: false,
  }),
  actions: {
    login(token) {
      this.token = token
      this.isAuthenticated = true
    },
    logout() {
      this.token = null
      this.isAuthenticated = false
    },
  },
})
