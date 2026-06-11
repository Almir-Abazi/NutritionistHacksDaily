<script setup>
import { onMounted, onUnmounted, ref } from 'vue'
import AdminLayout from '../../components/admin/AdminLayout.vue'
import api from '../../api/axios'

const certificates = ref([])
const loading = ref(false)

const modalOpen = ref(false)
const editingId = ref(null)
const saving = ref(false)
const uploading = ref(false)
const formError = ref(null)
const deleteConfirmId = ref(null)

const emptyForm = () => ({
  title: '',
  issuer: '',
  year: null,
  displayOrder: 0,
  imageFilename: null,
})

const form = ref(emptyForm())
const fieldErrors = ref({})

function isEmptyNumber(value) {
  return value == null || value === ''
}

function validateCertificate() {
  const errors = {}
  const f = form.value
  if (!f.title || f.title.trim().length < 3) {
    errors.title = 'Titulli është i detyrueshëm (min 3 karaktere)'
  }
  if (!f.issuer || f.issuer.trim().length < 3) {
    errors.issuer = 'Institucioni është i detyrueshëm'
  }
  const currentYear = new Date().getFullYear()
  if (isEmptyNumber(f.year) || f.year < 1990 || f.year > currentYear) {
    errors.year = 'Viti duhet të jetë ndërmjet 1990 dhe vitit aktual'
  }
  if (isEmptyNumber(f.displayOrder) || f.displayOrder < 0) {
    errors.displayOrder = 'Renditja duhet të jetë 0 ose më shumë'
  }
  return { valid: Object.keys(errors).length === 0, errors }
}

async function fetchCertificates() {
  loading.value = true
  try {
    const { data } = await api.get('/certificates')
    certificates.value = data
  } finally {
    loading.value = false
  }
}

function openAdd() {
  editingId.value = null
  form.value = emptyForm()
  formError.value = null
  fieldErrors.value = {}
  modalOpen.value = true
}

function openEdit(certificate) {
  editingId.value = certificate.id
  form.value = {
    title: certificate.title,
    issuer: certificate.issuer,
    year: certificate.year,
    displayOrder: certificate.displayOrder ?? 0,
    imageFilename: certificate.imageFilename,
  }
  formError.value = null
  fieldErrors.value = {}
  modalOpen.value = true
}

async function onFileSelected(event) {
  const file = event.target.files[0]
  if (!file) return
  uploading.value = true
  formError.value = null
  try {
    const formData = new FormData()
    formData.append('file', file)
    const { data } = await api.post('/upload', formData)
    form.value.imageFilename = data
  } catch {
    formError.value = 'Ngarkimi i imazhit dështoi'
  } finally {
    uploading.value = false
    event.target.value = ''
  }
}

async function save() {
  if (saving.value) return
  const validation = validateCertificate()
  fieldErrors.value = validation.errors
  if (!validation.valid) return
  saving.value = true
  formError.value = null
  try {
    if (editingId.value) {
      await api.put(`/certificates/${editingId.value}`, form.value)
    } else {
      await api.post('/certificates', form.value)
    }
    modalOpen.value = false
    await fetchCertificates()
  } catch {
    formError.value = 'Ndodhi një gabim, provo përsëri'
  } finally {
    saving.value = false
  }
}

const deleteToastVisible = ref(false)
let deleteToastTimer = null

function showDeleteToast() {
  deleteToastVisible.value = true
  if (deleteToastTimer) clearTimeout(deleteToastTimer)
  deleteToastTimer = setTimeout(() => {
    deleteToastVisible.value = false
  }, 3000)
}

async function removeCertificate(id) {
  try {
    await api.delete(`/certificates/${id}`)
    deleteConfirmId.value = null
    await fetchCertificates()
  } catch {
    deleteConfirmId.value = null
    showDeleteToast()
  }
}

onUnmounted(() => {
  if (deleteToastTimer) clearTimeout(deleteToastTimer)
})

onMounted(fetchCertificates)
</script>

<template>
  <AdminLayout>
    <div class="flex items-center justify-between">
      <h1 class="font-display text-4xl font-bold text-primary">Çertifikatat</h1>
      <button
        class="rounded-xl bg-accent px-6 py-3 font-semibold text-cream transition-opacity hover:opacity-90"
        @click="openAdd"
      >
        Shto të Ri +
      </button>
    </div>

    <div class="mt-8 overflow-hidden rounded-2xl border border-line bg-surface2">
      <table class="w-full text-left text-sm">
        <thead>
          <tr class="border-b border-line text-muted">
            <th class="px-5 py-4 font-medium">Imazhi</th>
            <th class="px-5 py-4 font-medium">Titulli</th>
            <th class="px-5 py-4 font-medium">Institucioni</th>
            <th class="px-5 py-4 font-medium">Viti</th>
            <th class="px-5 py-4 font-medium">Renditja</th>
            <th class="px-5 py-4 font-medium">Veprimet</th>
          </tr>
        </thead>
        <tbody>
          <template v-if="loading">
            <tr v-for="n in 5" :key="`skeleton-${n}`" class="animate-pulse border-b border-line last:border-b-0">
              <td class="px-5 py-4"><div class="h-12 w-12 rounded-lg bg-surface"></div></td>
              <td v-for="c in 5" :key="c" class="px-5 py-4"><div class="h-4 w-3/4 rounded bg-surface"></div></td>
            </tr>
          </template>
          <tr v-else-if="certificates.length === 0">
            <td colspan="6" class="px-5 py-10 text-center text-muted">Nuk ka çertifikata ende.</td>
          </tr>
          <tr
            v-for="certificate in certificates"
            v-else
            :key="certificate.id"
            class="border-b border-line last:border-b-0"
          >
            <td class="px-5 py-4">
              <img
                v-if="certificate.imageFilename"
                :src="`/api/v1/files/${certificate.imageFilename}`"
                :alt="certificate.title"
                class="h-12 w-12 rounded-lg object-cover"
              />
              <div v-else class="h-12 w-12 rounded-lg border border-line bg-surface"></div>
            </td>
            <td class="px-5 py-4 font-medium text-primary">{{ certificate.title }}</td>
            <td class="px-5 py-4 text-muted">{{ certificate.issuer }}</td>
            <td class="px-5 py-4">
              <span class="inline-block rounded-full border border-accent/20 bg-accent/10 px-3 py-1 text-xs text-accent">
                {{ certificate.year }}
              </span>
            </td>
            <td class="px-5 py-4 text-muted">{{ certificate.displayOrder }}</td>
            <td class="px-5 py-4">
              <div v-if="deleteConfirmId === certificate.id" class="flex items-center gap-2">
                <span class="text-xs text-muted">Jeni i sigurt?</span>
                <button
                  class="rounded-lg bg-red-700 px-3 py-1.5 text-xs font-semibold text-cream"
                  @click="removeCertificate(certificate.id)"
                >
                  Po
                </button>
                <button
                  class="rounded-lg border border-line px-3 py-1.5 text-xs text-muted"
                  @click="deleteConfirmId = null"
                >
                  Jo
                </button>
              </div>
              <div v-else class="flex items-center gap-2">
                <button
                  class="rounded-lg border border-accent px-3 py-1.5 text-xs text-accent transition-colors hover:bg-accent/10"
                  @click="openEdit(certificate)"
                >
                  Edito
                </button>
                <button
                  class="rounded-lg border border-red-700 px-3 py-1.5 text-xs text-red-700 transition-colors hover:bg-red-700/10"
                  @click="deleteConfirmId = certificate.id"
                >
                  Fshi
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Add/Edit modal -->
    <div
      v-if="modalOpen"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/70 px-6"
      @click.self="modalOpen = false"
    >
      <div class="max-h-[90vh] w-full max-w-[560px] overflow-y-auto rounded-2xl border border-line bg-surface p-8">
        <h2 class="font-display text-2xl font-bold text-primary">
          {{ editingId ? 'Edito Çertifikatën' : 'Shto Çertifikatë' }}
        </h2>

        <div class="mt-6 flex flex-col gap-4">
          <div>
            <label class="mb-1 block text-sm text-muted">Titulli</label>
            <input
              v-model="form.title"
              type="text"
              class="w-full rounded-xl border border-line bg-surface2 p-3 text-primary outline-none focus:border-accent/50"
            />
            <p v-if="fieldErrors.title" class="mt-1 text-sm text-red-700">{{ fieldErrors.title }}</p>
          </div>
          <div>
            <label class="mb-1 block text-sm text-muted">Institucioni</label>
            <input
              v-model="form.issuer"
              type="text"
              class="w-full rounded-xl border border-line bg-surface2 p-3 text-primary outline-none focus:border-accent/50"
            />
            <p v-if="fieldErrors.issuer" class="mt-1 text-sm text-red-700">{{ fieldErrors.issuer }}</p>
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="mb-1 block text-sm text-muted">Viti</label>
              <input
                v-model.number="form.year"
                type="number"
                class="w-full rounded-xl border border-line bg-surface2 p-3 text-primary outline-none focus:border-accent/50"
              />
              <p v-if="fieldErrors.year" class="mt-1 text-sm text-red-700">{{ fieldErrors.year }}</p>
            </div>
            <div>
              <label class="mb-1 block text-sm text-muted">Renditja</label>
              <input
                v-model.number="form.displayOrder"
                type="number"
                class="w-full rounded-xl border border-line bg-surface2 p-3 text-primary outline-none focus:border-accent/50"
              />
              <p v-if="fieldErrors.displayOrder" class="mt-1 text-sm text-red-700">{{ fieldErrors.displayOrder }}</p>
            </div>
          </div>
          <div>
            <label class="mb-1 block text-sm text-muted">Imazhi</label>
            <label
              class="flex min-h-[100px] cursor-pointer flex-col items-center justify-center gap-2 rounded-xl border border-dashed border-line bg-surface2 p-4 text-sm text-muted transition-colors hover:border-accent/40"
            >
              <img
                v-if="form.imageFilename"
                :src="`/api/v1/files/${form.imageFilename}`"
                alt="Imazhi i ngarkuar"
                class="max-h-40 rounded-lg object-contain"
              />
              <span>{{ uploading ? 'Duke u ngarkuar...' : 'Kliko për të ngarkuar imazhin' }}</span>
              <input type="file" accept="image/*" class="hidden" @change="onFileSelected" />
            </label>
          </div>

          <button
            class="w-full rounded-xl bg-accent p-3 font-semibold text-cream transition-opacity hover:opacity-90 disabled:opacity-50"
            :disabled="saving || uploading"
            @click="save"
          >
            {{ saving ? 'Duke ruajtur...' : 'Ruaj' }}
          </button>
          <p v-if="formError" class="text-center text-sm text-red-700">{{ formError }}</p>
        </div>
      </div>
    </div>

    <!-- Delete failure toast -->
    <div
      v-if="deleteToastVisible"
      class="fixed bottom-6 right-6 z-50 rounded-xl border border-red-700/40 bg-surface2 px-5 py-3 text-sm text-red-700 shadow-lg"
    >
      Fshirja dështoi
    </div>
  </AdminLayout>
</template>
