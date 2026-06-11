<script setup>
import { computed, onMounted, onUnmounted, reactive, ref } from 'vue'
import { useCertificatesStore } from '../stores/certificatesStore'

const store = useCertificatesStore()

const demoCertificates = [
  { id: 'demo-1', title: 'Precision Nutrition Level 2', issuer: 'Precision Nutrition Institute', year: 2022, imageFilename: null },
  { id: 'demo-2', title: 'Sports Nutrition Specialist', issuer: 'ISSA International', year: 2021, imageFilename: null },
  { id: 'demo-3', title: 'Hormonal Health Coach', issuer: 'Functional Medicine University', year: 2023, imageFilename: null },
]

const certificates = computed(() =>
  store.certificates.length > 0 ? store.certificates : demoCertificates,
)

const visibleCards = reactive({})
let observer = null

function observeCard(el) {
  if (el && observer) observer.observe(el)
}

// Lightbox
const selectedCertificate = ref(null)

function onKeydown(event) {
  if (event.key === 'Escape') closeLightbox()
}

function openLightbox(certificate) {
  selectedCertificate.value = certificate
  window.addEventListener('keydown', onKeydown)
}

function closeLightbox() {
  selectedCertificate.value = null
  window.removeEventListener('keydown', onKeydown)
}

onMounted(() => {
  observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          visibleCards[entry.target.dataset.index] = true
          observer.unobserve(entry.target)
        }
      })
    },
    { threshold: 0.15 },
  )
  store.fetchCertificates()
})

onUnmounted(() => {
  observer?.disconnect()
  window.removeEventListener('keydown', onKeydown)
})
</script>

<template>
  <section class="bg-surface py-12 md:py-24">
    <div class="mx-auto max-w-6xl px-6">
      <h2 class="text-center font-display text-4xl font-bold text-primary md:text-5xl">
        Çertifikatat &amp; Kualifikimet
      </h2>
      <p class="mb-16 mt-4 text-center text-muted">
        Trajnim i vazhdueshëm dhe çertifikime ndërkombëtare
      </p>

      <!-- Loading skeletons -->
      <div v-if="store.loading" class="grid grid-cols-1 gap-8 md:grid-cols-3">
        <div v-for="n in 3" :key="n" class="animate-pulse rounded-2xl border border-line bg-surface2 p-6">
          <div class="h-[200px] rounded-xl bg-surface"></div>
          <div class="mt-5 h-5 w-3/4 rounded bg-surface"></div>
          <div class="mt-3 h-4 w-1/2 rounded bg-surface"></div>
        </div>
      </div>

      <div v-else class="grid grid-cols-1 gap-8 md:grid-cols-3">
        <div
          v-for="(certificate, index) in certificates"
          :key="certificate.id"
          :ref="observeCard"
          :data-index="index"
          class="group cursor-pointer rounded-2xl border border-line bg-surface2 p-6 transition-all duration-[600ms] ease-out hover:border-accent/40"
          :class="visibleCards[index] ? 'translate-y-0 opacity-100' : 'translate-y-10 opacity-0'"
          :style="{ transitionDelay: `${index * 150}ms` }"
          @click="openLightbox(certificate)"
        >
          <div class="relative flex h-[200px] items-center justify-center overflow-hidden rounded-xl bg-surface">
            <img
              v-if="certificate.imageFilename"
              :src="`/api/v1/files/${certificate.imageFilename}`"
              :alt="certificate.title"
              class="h-full w-full object-cover"
            />
            <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="h-14 w-14 text-accent">
              <path stroke-linecap="round" stroke-linejoin="round" d="M16.5 18.75h-9m9 0a3 3 0 013 3h-15a3 3 0 013-3m9 0v-3.375c0-.621-.503-1.125-1.125-1.125h-.871M7.5 18.75v-3.375c0-.621.504-1.125 1.125-1.125h.872m5.007 0H9.497m5.007 0a7.454 7.454 0 01-.982-3.172M9.497 14.25a7.454 7.454 0 00.981-3.172M5.25 4.236c-.982.143-1.954.317-2.916.52A6.003 6.003 0 007.73 9.728M5.25 4.236V4.5c0 2.108.966 3.99 2.48 5.228M5.25 4.236V2.721C7.456 2.41 9.71 2.25 12 2.25c2.291 0 4.545.16 6.75.47v1.516M7.73 9.728a6.726 6.726 0 002.748 1.35m8.272-6.842V4.5c0 2.108-.966 3.99-2.48 5.228m2.48-5.492a46.32 46.32 0 012.916.52 6.003 6.003 0 01-5.395 4.972m0 0a6.726 6.726 0 01-2.749 1.35m0 0a6.772 6.772 0 01-3.044 0" />
            </svg>
            <span class="absolute inset-x-0 bottom-0 bg-black/60 py-1 text-center text-sm text-white opacity-0 transition-opacity duration-300 group-hover:opacity-100">
              🔍 Shiko
            </span>
          </div>
          <h3 class="mt-5 font-semibold text-primary">{{ certificate.title }}</h3>
          <p class="mt-1 text-sm text-muted">{{ certificate.issuer }}</p>
          <span class="mt-3 inline-block rounded-full border border-accent/20 bg-accent/10 px-3 py-1 text-xs font-medium text-accent">
            {{ certificate.year }}
          </span>
        </div>
      </div>
    </div>

    <!-- Lightbox -->
    <Transition name="lightbox">
      <div
        v-if="selectedCertificate"
        class="fixed inset-0 z-50 overflow-y-auto bg-black/80 backdrop-blur-sm"
        @click.self="closeLightbox"
      >
        <div class="lightbox-content relative mx-auto mt-[5vh] max-h-[90vh] w-[calc(100%-3rem)] max-w-[800px] overflow-y-auto rounded-2xl border border-line bg-surface p-6">
          <button
            aria-label="Mbyll"
            class="absolute right-4 top-4 z-10 flex h-10 w-10 items-center justify-center rounded-full bg-surface2 text-primary transition-colors hover:bg-surface"
            @click="closeLightbox"
          >
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="h-5 w-5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>

          <img
            v-if="selectedCertificate.imageFilename"
            :src="`/api/v1/files/${selectedCertificate.imageFilename}`"
            :alt="selectedCertificate.title"
            class="max-h-[60vh] w-full rounded-xl object-contain"
          />
          <div v-else class="flex min-h-[300px] flex-col items-center justify-center gap-4 rounded-xl bg-surface2">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="h-20 w-20 text-accent">
              <path stroke-linecap="round" stroke-linejoin="round" d="M16.5 18.75h-9m9 0a3 3 0 013 3h-15a3 3 0 013-3m9 0v-3.375c0-.621-.503-1.125-1.125-1.125h-.871M7.5 18.75v-3.375c0-.621.504-1.125 1.125-1.125h.872m5.007 0H9.497m5.007 0a7.454 7.454 0 01-.982-3.172M9.497 14.25a7.454 7.454 0 00.981-3.172M5.25 4.236c-.982.143-1.954.317-2.916.52A6.003 6.003 0 007.73 9.728M5.25 4.236V4.5c0 2.108.966 3.99 2.48 5.228M5.25 4.236V2.721C7.456 2.41 9.71 2.25 12 2.25c2.291 0 4.545.16 6.75.47v1.516M7.73 9.728a6.726 6.726 0 002.748 1.35m8.272-6.842V4.5c0 2.108-.966 3.99-2.48 5.228m2.48-5.492a46.32 46.32 0 012.916.52 6.003 6.003 0 01-5.395 4.972m0 0a6.726 6.726 0 01-2.749 1.35m0 0a6.772 6.772 0 01-3.044 0" />
            </svg>
            <p class="px-6 text-center font-display text-xl font-bold text-primary">{{ selectedCertificate.title }}</p>
          </div>

          <h3 class="mt-6 font-display text-[28px] font-bold text-primary">{{ selectedCertificate.title }}</h3>
          <p class="mt-1 text-base text-muted">{{ selectedCertificate.issuer }} &bull; {{ selectedCertificate.year }}</p>
          <span class="mb-2 mt-4 inline-block rounded-full border border-accent/20 bg-accent/10 px-4 py-1.5 text-sm font-medium text-accent">
            Çertifikatë e Verifikuar ✓
          </span>
        </div>
      </div>
    </Transition>
  </section>
</template>

<style scoped>
.lightbox-enter-active {
  transition: opacity 0.2s ease;
}

.lightbox-enter-active .lightbox-content {
  transition: transform 0.3s ease;
}

.lightbox-leave-active {
  transition: opacity 0.2s ease;
}

.lightbox-leave-active .lightbox-content {
  transition: transform 0.3s ease;
}

.lightbox-enter-from,
.lightbox-leave-to {
  opacity: 0;
}

.lightbox-enter-from .lightbox-content,
.lightbox-leave-to .lightbox-content {
  transform: scale(0.9);
}
</style>
