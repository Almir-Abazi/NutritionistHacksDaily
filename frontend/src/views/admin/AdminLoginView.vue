<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../../api/axios'
import { useAuthStore } from '../../stores/authStore'

const router = useRouter()
const authStore = useAuthStore()

const username = ref('')
const password = ref('')
const error = ref(null)
const loading = ref(false)

async function login() {
  if (loading.value) return
  error.value = null
  loading.value = true
  try {
    const { data } = await api.post('/auth/login', {
      username: username.value,
      password: password.value,
    })
    authStore.login(data.token)
    router.push('/admin/dashboard')
  } catch {
    error.value = 'Kredencialet janë të gabuara'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="flex min-h-screen items-center justify-center bg-base px-6 font-body">
    <div class="w-full max-w-[400px] rounded-2xl border border-line bg-surface p-10">
      <p class="text-center font-display text-2xl font-bold text-accent">NutritionistHacksDaily</p>
      <p class="mt-1 text-center text-sm text-muted">Admin Panel</p>

      <div class="mt-8 flex flex-col gap-4">
        <input
          v-model="username"
          type="text"
          placeholder="Username"
          class="w-full rounded-xl border border-line bg-surface2 p-3 text-primary placeholder-muted focus:border-accent focus:outline-none"
          @keyup.enter="login"
        />
        <input
          v-model="password"
          type="password"
          placeholder="Fjalëkalimi"
          class="w-full rounded-xl border border-line bg-surface2 p-3 text-primary placeholder-muted focus:border-accent focus:outline-none"
          @keyup.enter="login"
        />
        <button
          class="w-full rounded-xl bg-accent p-3 font-semibold text-cream transition-opacity hover:opacity-90 disabled:opacity-50"
          :disabled="loading"
          @click="login"
        >
          {{ loading ? 'Duke u kyçur...' : 'Hyr →' }}
        </button>
        <p v-if="error" class="text-center text-sm text-red-700">{{ error }}</p>
      </div>
    </div>
  </div>
</template>
