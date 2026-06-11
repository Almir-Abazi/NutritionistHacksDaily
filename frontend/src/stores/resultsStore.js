import { defineStore } from 'pinia'
import api from '../api/axios'

export const useResultsStore = defineStore('results', {
  state: () => ({
    results: [],
    loading: false,
    error: null,
  }),
  actions: {
    async fetchResults() {
      this.loading = true
      this.error = null
      try {
        const { data } = await api.get('/results')
        this.results = data
      } catch (error) {
        this.error = error.response?.data?.message ?? 'Failed to load results'
      } finally {
        this.loading = false
      }
    },
  },
})
