import { defineStore } from 'pinia'
import api from '../api/axios'

export const useCertificatesStore = defineStore('certificates', {
  state: () => ({
    certificates: [],
    loading: false,
    error: null,
  }),
  actions: {
    async fetchCertificates() {
      this.loading = true
      this.error = null
      try {
        const { data } = await api.get('/certificates')
        this.certificates = data
      } catch (error) {
        this.error = error.response?.data?.message ?? 'Failed to load certificates'
      } finally {
        this.loading = false
      }
    },
  },
})
