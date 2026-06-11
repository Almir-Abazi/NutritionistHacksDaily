<script setup>
import { computed, onMounted, onUnmounted, reactive, ref, watch } from 'vue'
import { useResultsStore } from '../stores/resultsStore'
import ResultCard from './ResultCard.vue'

const store = useResultsStore()

const demoResults = [
  {
    id: 'demo-1',
    clientName: 'Arta M.',
    age: 34,
    diagnosis: 'Resistencë ndaj insulinës',
    weightLossKg: 28,
    durationMonths: 4,
    description: 'Pas vitesh luftë me peshën, Arta arriti rezultate të pabesueshme përmes programit të personalizuar.',
    photoFilename: null,
  },
  {
    id: 'demo-2',
    clientName: 'Erjon K.',
    age: 28,
    diagnosis: 'Testosteron i ulët',
    weightLossKg: -12,
    durationMonths: 3,
    description: 'Erjoni fitoi 12kg muskulaturë dhe normalizoi nivelet hormonale brenda 3 muajsh.',
    photoFilename: null,
  },
  {
    id: 'demo-3',
    clientName: 'Mimoza H.',
    age: 41,
    diagnosis: 'Hipotiroidizëm',
    weightLossKg: 22,
    durationMonths: 5,
    description: 'Mimoza humbi 22kg duke menaxhuar gjendjen tiroide përmes dietës dhe suplementimit.',
    photoFilename: null,
  },
]

const results = computed(() => (store.results.length > 0 ? store.results : demoResults))
const isCarousel = computed(() => results.value.length > 3)

// 3 cards per view on desktop, 1 on mobile (≤767px)
const perView = ref(3)
let mediaQuery = null

function updatePerView(query) {
  perView.value = query.matches ? 1 : 3
}

const currentIndex = ref(0)
const maxIndex = computed(() => Math.max(0, results.value.length - perView.value))
const paused = ref(false)
let autoplayTimer = null

function next() {
  currentIndex.value = currentIndex.value >= maxIndex.value ? 0 : currentIndex.value + 1
}

function prev() {
  currentIndex.value = currentIndex.value <= 0 ? maxIndex.value : currentIndex.value - 1
}

watch([perView, results], () => {
  if (currentIndex.value > maxIndex.value) currentIndex.value = maxIndex.value
})

// Scroll-reveal
const visibleCards = reactive({})
let observer = null

function observeReveal(el) {
  if (el && observer) observer.observe(el)
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

  mediaQuery = window.matchMedia('(max-width: 767px)')
  updatePerView(mediaQuery)
  mediaQuery.addEventListener('change', updatePerView)

  autoplayTimer = setInterval(() => {
    if (isCarousel.value && !paused.value) next()
  }, 5000)

  store.fetchResults()
})

onUnmounted(() => {
  observer?.disconnect()
  mediaQuery?.removeEventListener('change', updatePerView)
  if (autoplayTimer) clearInterval(autoplayTimer)
})
</script>

<template>
  <section class="bg-base py-12 md:py-24">
    <div class="mx-auto max-w-6xl px-6">
      <h2 class="text-center font-display text-4xl font-bold text-primary md:text-5xl">
        Rezultatet e Klientëve
      </h2>
      <p class="mb-16 mt-4 text-center text-muted">Histori reale, transformime reale</p>

      <!-- Loading skeletons -->
      <div v-if="store.loading" class="grid grid-cols-1 gap-8 md:grid-cols-3">
        <div v-for="n in 3" :key="n" class="animate-pulse overflow-hidden rounded-2xl border border-line bg-surface2">
          <div class="h-[220px] bg-surface"></div>
          <div class="p-6">
            <div class="h-5 w-2/3 rounded bg-surface"></div>
            <div class="mt-4 h-10 w-1/2 rounded bg-surface"></div>
            <div class="mt-4 h-4 w-full rounded bg-surface"></div>
          </div>
        </div>
      </div>

      <!-- Static grid for 3 or fewer results -->
      <div v-else-if="!isCarousel" class="grid grid-cols-1 gap-8 md:grid-cols-3">
        <div
          v-for="(result, index) in results"
          :key="result.id"
          :ref="observeReveal"
          :data-index="index"
          class="transition-all duration-[600ms] ease-out"
          :class="visibleCards[index] ? 'translate-y-0 opacity-100' : 'translate-y-10 opacity-0'"
          :style="{ transitionDelay: `${index * 150}ms` }"
        >
          <ResultCard :result="result" />
        </div>
      </div>

      <!-- Carousel for 4+ results -->
      <div
        v-else
        :ref="observeReveal"
        data-index="carousel"
        class="transition-all duration-[600ms] ease-out"
        :class="visibleCards['carousel'] ? 'translate-y-0 opacity-100' : 'translate-y-10 opacity-0'"
        @mouseenter="paused = true"
        @mouseleave="paused = false"
      >
        <div class="flex items-center gap-4">
          <button
            aria-label="Më parë"
            class="flex h-11 w-11 shrink-0 items-center justify-center rounded-full border border-line bg-surface2 text-primary transition-colors hover:bg-surface"
            @click="prev"
          >
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="h-5 w-5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 19.5L8.25 12l7.5-7.5" />
            </svg>
          </button>

          <div class="flex-1 overflow-hidden">
            <div
              class="flex transition-transform duration-500 ease-out"
              :style="{ transform: `translateX(-${currentIndex * (100 / perView)}%)` }"
            >
              <div
                v-for="result in results"
                :key="result.id"
                class="w-full shrink-0 px-3 md:w-1/3"
              >
                <ResultCard :result="result" />
              </div>
            </div>
          </div>

          <button
            aria-label="Më pas"
            class="flex h-11 w-11 shrink-0 items-center justify-center rounded-full border border-line bg-surface2 text-primary transition-colors hover:bg-surface"
            @click="next"
          >
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="h-5 w-5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M8.25 4.5l7.5 7.5-7.5 7.5" />
            </svg>
          </button>
        </div>

        <!-- Dot indicators -->
        <div class="mt-8 flex justify-center gap-2">
          <button
            v-for="dot in maxIndex + 1"
            :key="dot"
            :aria-label="`Shko te pozicioni ${dot}`"
            class="h-2 rounded-full transition-all duration-300"
            :class="currentIndex === dot - 1 ? 'w-6 bg-accent' : 'w-2 bg-muted/40 hover:bg-muted'"
            @click="currentIndex = dot - 1"
          ></button>
        </div>
      </div>
    </div>
  </section>
</template>
