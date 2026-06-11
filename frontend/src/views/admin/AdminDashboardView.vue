<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import AdminLayout from '../../components/admin/AdminLayout.vue'
import api from '../../api/axios'

const router = useRouter()

const resultsCount = ref(null)
const certificatesCount = ref(null)

onMounted(async () => {
  try {
    const [results, certificates] = await Promise.all([
      api.get('/results'),
      api.get('/certificates'),
    ])
    resultsCount.value = results.data.length
    certificatesCount.value = certificates.data.length
  } catch {
    resultsCount.value = '—'
    certificatesCount.value = '—'
  }
})
</script>

<template>
  <AdminLayout>
    <h1 class="font-display text-5xl font-bold text-primary">Dashboard</h1>
    <p class="mt-2 text-muted">Mirësevini, Hasan.</p>

    <div class="mt-10 grid grid-cols-1 gap-6 md:grid-cols-2">
      <div class="rounded-2xl border border-line bg-surface2 p-8">
        <p class="text-sm text-muted">Rezultate Klientësh</p>
        <p class="mt-2 font-display text-5xl font-black text-accent">
          {{ resultsCount ?? '...' }}
        </p>
      </div>
      <div class="rounded-2xl border border-line bg-surface2 p-8">
        <p class="text-sm text-muted">Çertifikata</p>
        <p class="mt-2 font-display text-5xl font-black text-accent">
          {{ certificatesCount ?? '...' }}
        </p>
      </div>
    </div>

    <div class="mt-8 flex flex-wrap gap-4">
      <button
        class="rounded-xl bg-accent px-6 py-3 font-semibold text-cream transition-opacity hover:opacity-90"
        @click="router.push('/admin/results')"
      >
        Shto Rezultat të Ri
      </button>
      <button
        class="rounded-xl border border-accent px-6 py-3 font-semibold text-accent transition-colors hover:bg-accent/10"
        @click="router.push('/admin/certificates')"
      >
        Shto Çertifikatë
      </button>
    </div>
  </AdminLayout>
</template>
