<script setup>
import { onMounted, onUnmounted, ref } from 'vue'
import AdminLayout from '../../components/admin/AdminLayout.vue'
import api from '../../api/axios'

const results = ref([])
const loading = ref(false)

const modalOpen = ref(false)
const editingId = ref(null)
const saving = ref(false)
const uploading = ref(false)
const formError = ref(null)
const deleteConfirmId = ref(null)

const emptyForm = () => ({
  clientName: '',
  age: null,
  diagnosis: '',
  weightLossKg: null,
  durationMonths: null,
  displayOrder: 0,
  description: '',
  photoFilename: null,
})

const form = ref(emptyForm())
const fieldErrors = ref({})

function isEmptyNumber(value) {
  return value == null || value === ''
}

function validateResult() {
  const errors = {}
  const f = form.value
  if (!f.clientName || f.clientName.trim().length < 2) {
    errors.clientName = 'Emri është i detyrueshëm (min 2 karaktere)'
  }
  if (isEmptyNumber(f.age) || f.age < 10 || f.age > 100) {
    errors.age = 'Mosha duhet të jetë ndërmjet 10 dhe 100'
  }
  if (isEmptyNumber(f.weightLossKg) || f.weightLossKg === 0 || f.weightLossKg < -200 || f.weightLossKg > 200) {
    errors.weightLossKg = 'Vlera e peshës është e pavlefshme'
  }
  if (isEmptyNumber(f.durationMonths) || f.durationMonths < 1 || f.durationMonths > 36) {
    errors.durationMonths = 'Kohëzgjatja duhet të jetë 1-36 muaj'
  }
  if (!f.diagnosis || f.diagnosis.trim().length < 3) {
    errors.diagnosis = 'Diagnoza është e detyrueshme'
  }
  if (!f.description || f.description.trim().length < 10) {
    errors.description = 'Përshkrimi duhet të ketë min 10 karaktere'
  }
  if (isEmptyNumber(f.displayOrder) || f.displayOrder < 0) {
    errors.displayOrder = 'Renditja duhet të jetë 0 ose më shumë'
  }
  return { valid: Object.keys(errors).length === 0, errors }
}

async function fetchResults() {
  loading.value = true
  try {
    const { data } = await api.get('/results')
    results.value = data
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

function openEdit(result) {
  editingId.value = result.id
  form.value = {
    clientName: result.clientName,
    age: result.age,
    diagnosis: result.diagnosis,
    weightLossKg: result.weightLossKg,
    durationMonths: result.durationMonths,
    displayOrder: result.displayOrder ?? 0,
    description: result.description,
    photoFilename: result.photoFilename,
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
    form.value.photoFilename = data
  } catch {
    formError.value = 'Ngarkimi i fotos dështoi'
  } finally {
    uploading.value = false
    event.target.value = ''
  }
}

async function save() {
  if (saving.value) return
  const validation = validateResult()
  fieldErrors.value = validation.errors
  if (!validation.valid) return
  saving.value = true
  formError.value = null
  try {
    if (editingId.value) {
      await api.put(`/results/${editingId.value}`, form.value)
    } else {
      await api.post('/results', form.value)
    }
    modalOpen.value = false
    await fetchResults()
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

async function removeResult(id) {
  try {
    await api.delete(`/results/${id}`)
    deleteConfirmId.value = null
    await fetchResults()
  } catch {
    deleteConfirmId.value = null
    showDeleteToast()
  }
}

onUnmounted(() => {
  if (deleteToastTimer) clearTimeout(deleteToastTimer)
})

function formatWeight(weightLossKg) {
  if (weightLossKg == null) return '—'
  return weightLossKg < 0 ? `+${Math.abs(weightLossKg)}kg` : `-${weightLossKg}kg`
}

onMounted(fetchResults)
</script>

<template>
  <AdminLayout>
    <div class="flex items-center justify-between">
      <h1 class="font-display text-4xl font-bold text-primary">Rezultatet e Klientëve</h1>
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
            <th class="px-5 py-4 font-medium">Foto</th>
            <th class="px-5 py-4 font-medium">Emri</th>
            <th class="px-5 py-4 font-medium">Mosha</th>
            <th class="px-5 py-4 font-medium">Diagnoza</th>
            <th class="px-5 py-4 font-medium">Humbje Peshe</th>
            <th class="px-5 py-4 font-medium">Kohëzgjatja</th>
            <th class="px-5 py-4 font-medium">Veprimet</th>
          </tr>
        </thead>
        <tbody>
          <template v-if="loading">
            <tr v-for="n in 5" :key="`skeleton-${n}`" class="animate-pulse border-b border-line last:border-b-0">
              <td class="px-5 py-4"><div class="h-12 w-12 rounded-lg bg-surface"></div></td>
              <td v-for="c in 6" :key="c" class="px-5 py-4"><div class="h-4 w-3/4 rounded bg-surface"></div></td>
            </tr>
          </template>
          <tr v-else-if="results.length === 0">
            <td colspan="7" class="px-5 py-10 text-center text-muted">Nuk ka rezultate ende.</td>
          </tr>
          <tr v-for="result in results" v-else :key="result.id" class="border-b border-line last:border-b-0">
            <td class="px-5 py-4">
              <img
                v-if="result.photoFilename"
                :src="`/api/v1/files/${result.photoFilename}`"
                :alt="result.clientName"
                class="h-12 w-12 rounded-lg object-cover"
              />
              <div v-else class="h-12 w-12 rounded-lg border border-line bg-surface"></div>
            </td>
            <td class="px-5 py-4 font-medium text-primary">{{ result.clientName }}</td>
            <td class="px-5 py-4 text-muted">{{ result.age }}</td>
            <td class="px-5 py-4">
              <span
                v-if="result.diagnosis"
                class="inline-block rounded-full border border-accent/20 bg-accent/10 px-3 py-1 text-xs text-accent"
              >
                {{ result.diagnosis }}
              </span>
            </td>
            <td class="px-5 py-4 font-semibold text-accent">{{ formatWeight(result.weightLossKg) }}</td>
            <td class="px-5 py-4 text-muted">{{ result.durationMonths }} muaj</td>
            <td class="px-5 py-4">
              <div v-if="deleteConfirmId === result.id" class="flex items-center gap-2">
                <span class="text-xs text-muted">Jeni i sigurt?</span>
                <button
                  class="rounded-lg bg-red-700 px-3 py-1.5 text-xs font-semibold text-cream"
                  @click="removeResult(result.id)"
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
                  @click="openEdit(result)"
                >
                  Edito
                </button>
                <button
                  class="rounded-lg border border-red-700 px-3 py-1.5 text-xs text-red-700 transition-colors hover:bg-red-700/10"
                  @click="deleteConfirmId = result.id"
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
          {{ editingId ? 'Edito Rezultatin' : 'Shto Rezultat' }}
        </h2>

        <div class="mt-6 flex flex-col gap-4">
          <div>
            <label class="mb-1 block text-sm text-muted">Emri i Klientit</label>
            <input
              v-model="form.clientName"
              type="text"
              class="w-full rounded-xl border border-line bg-surface2 p-3 text-primary outline-none focus:border-accent/50"
            />
            <p v-if="fieldErrors.clientName" class="mt-1 text-sm text-red-700">{{ fieldErrors.clientName }}</p>
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="mb-1 block text-sm text-muted">Mosha</label>
              <input
                v-model.number="form.age"
                type="number"
                class="w-full rounded-xl border border-line bg-surface2 p-3 text-primary outline-none focus:border-accent/50"
              />
              <p v-if="fieldErrors.age" class="mt-1 text-sm text-red-700">{{ fieldErrors.age }}</p>
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
            <label class="mb-1 block text-sm text-muted">Diagnoza</label>
            <input
              v-model="form.diagnosis"
              type="text"
              class="w-full rounded-xl border border-line bg-surface2 p-3 text-primary outline-none focus:border-accent/50"
            />
            <p v-if="fieldErrors.diagnosis" class="mt-1 text-sm text-red-700">{{ fieldErrors.diagnosis }}</p>
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="mb-1 block text-sm text-muted">Humbje Peshe (kg)</label>
              <input
                v-model.number="form.weightLossKg"
                type="number"
                step="0.1"
                class="w-full rounded-xl border border-line bg-surface2 p-3 text-primary outline-none focus:border-accent/50"
              />
              <p class="mt-1 text-xs text-muted">Negative për humbje, pozitiv për rritje</p>
              <p v-if="fieldErrors.weightLossKg" class="mt-1 text-sm text-red-700">{{ fieldErrors.weightLossKg }}</p>
            </div>
            <div>
              <label class="mb-1 block text-sm text-muted">Kohëzgjatja (muaj)</label>
              <input
                v-model.number="form.durationMonths"
                type="number"
                class="w-full rounded-xl border border-line bg-surface2 p-3 text-primary outline-none focus:border-accent/50"
              />
              <p v-if="fieldErrors.durationMonths" class="mt-1 text-sm text-red-700">{{ fieldErrors.durationMonths }}</p>
            </div>
          </div>
          <div>
            <label class="mb-1 block text-sm text-muted">Përshkrimi</label>
            <textarea
              v-model="form.description"
              rows="4"
              class="w-full rounded-xl border border-line bg-surface2 p-3 text-primary outline-none focus:border-accent/50"
            ></textarea>
            <p v-if="fieldErrors.description" class="mt-1 text-sm text-red-700">{{ fieldErrors.description }}</p>
          </div>
          <div>
            <label class="mb-1 block text-sm text-muted">Foto</label>
            <label
              class="flex min-h-[100px] cursor-pointer flex-col items-center justify-center gap-2 rounded-xl border border-dashed border-line bg-surface2 p-4 text-sm text-muted transition-colors hover:border-accent/40"
            >
              <img
                v-if="form.photoFilename"
                :src="`/api/v1/files/${form.photoFilename}`"
                alt="Foto e ngarkuar"
                class="max-h-40 rounded-lg object-contain"
              />
              <span>{{ uploading ? 'Duke u ngarkuar...' : 'Kliko për të ngarkuar foton' }}</span>
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
