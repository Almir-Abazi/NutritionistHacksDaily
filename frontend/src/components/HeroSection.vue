<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const orbOffset = ref(0)
const decoOffset = ref(0)

function onScroll() {
  orbOffset.value = window.scrollY * 0.4
  decoOffset.value = window.scrollY * 0.2
}

const tiltCard = ref(null)
const tiltTransform = ref('perspective(800px) rotateX(0deg) rotateY(0deg)')

function onTiltMove(event) {
  const rect = tiltCard.value.getBoundingClientRect()
  const offsetX = event.clientX - rect.left - rect.width / 2
  const offsetY = event.clientY - rect.top - rect.height / 2
  const rotateY = (offsetX / (rect.width / 2)) * 12
  const rotateX = -(offsetY / (rect.height / 2)) * 12
  tiltTransform.value = `perspective(800px) rotateX(${rotateX}deg) rotateY(${rotateY}deg)`
}

function onTiltLeave() {
  tiltTransform.value = 'perspective(800px) rotateX(0deg) rotateY(0deg)'
}

const entered = ref(false)
let enterTimer = null

onMounted(() => {
  window.addEventListener('scroll', onScroll, { passive: true })
  enterTimer = setTimeout(() => {
    entered.value = true
  }, 50)
})

onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
  if (enterTimer) clearTimeout(enterTimer)
})
</script>

<template>
  <section
    class="relative h-screen overflow-hidden bg-[linear-gradient(180deg,#f3efe2_0%,#e9e2c9_100%)]"
    :class="{ animate: entered }"
  >
    <!-- Parallax orb (0.4x) -->
    <div
      class="pointer-events-none absolute -top-40 left-1/2 -ml-[300px]"
      :style="{ transform: `translateY(${orbOffset}px)` }"
    >
      <div class="orb-float h-[600px] w-[600px] rounded-full bg-[radial-gradient(circle,#7a7257_0%,transparent_70%)] opacity-15 blur-3xl"></div>
    </div>

    <!-- Secondary decorative element (0.2x) -->
    <div
      class="pointer-events-none absolute right-[10%] top-[55%]"
      :style="{ transform: `translateY(${decoOffset}px)` }"
    >
      <div class="h-64 w-64 rounded-full border border-accent/10 bg-[radial-gradient(circle,#7a725733_0%,transparent_70%)] blur-2xl"></div>
    </div>

    <div class="relative z-10 mx-auto flex h-full max-w-6xl flex-col items-center justify-center gap-12 px-6 lg:flex-row lg:gap-20">
      <div class="flex max-w-2xl flex-col items-center text-center">
        <p class="entrance entrance-eyebrow mb-6 text-center text-xs font-medium uppercase tracking-[0.3em] text-accent md:text-sm">
          Nutricion &bull; Transformim &bull; Rezultate
        </p>
        <h1 class="font-display text-5xl font-black leading-[1.05] text-primary md:text-8xl">
          <span class="entrance entrance-line1 block">Transformo</span>
          <span class="entrance entrance-line2 block text-accent">Trupin Tënd</span>
        </h1>
        <p class="entrance entrance-sub mt-8 max-w-[560px] text-lg text-muted">
          Hasan Abazi — Nutricionist i Çertifikuar. Program i personalizuar për humbje peshe,
          rritje muskujsh dhe optimizim hormonal.
        </p>
        <a
          href="https://wa.me/355000000000"
          target="_blank"
          rel="noopener noreferrer"
          class="entrance entrance-cta mt-10 rounded-full bg-accent px-8 py-4 font-semibold text-cream transition-transform duration-300 hover:scale-105"
        >
          Fillo Sot &rarr;
        </a>

        <!-- Mobile-only stats row (tilt card is hidden below md) -->
        <div class="entrance entrance-stats mt-10 grid w-full max-w-xs grid-cols-3 gap-4 md:hidden">
          <div>
            <p class="text-xl font-bold text-accent">500+</p>
            <p class="mt-1 text-xs text-muted">Klientë</p>
          </div>
          <div>
            <p class="text-xl font-bold text-accent">8+</p>
            <p class="mt-1 text-xs text-muted">Vite</p>
          </div>
          <div>
            <p class="text-xl font-bold text-accent">95%</p>
            <p class="mt-1 text-xs text-muted">Sukses</p>
          </div>
        </div>
      </div>

      <!-- 3D tilt card (entrance animation on the wrapper so fill-mode
           doesn't lock the card's own tilt transform) -->
      <div class="entrance entrance-card hidden shrink-0 md:block">
        <div
          ref="tiltCard"
          class="w-[280px] rounded-2xl border border-line bg-surface2 p-6 transition-transform duration-150 ease-out will-change-transform"
          :style="{ transform: tiltTransform }"
          @mousemove="onTiltMove"
          @mouseleave="onTiltLeave"
        >
          <div class="mx-auto flex h-20 w-20 items-center justify-center rounded-full border border-line bg-surface">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="h-10 w-10 text-accent">
              <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 6a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0zM4.501 20.118a7.5 7.5 0 0114.998 0A17.933 17.933 0 0112 21.75c-2.676 0-5.216-.584-7.499-1.632z" />
            </svg>
          </div>
          <p class="mt-4 text-center text-lg font-semibold text-primary">Hasan Abazi</p>
          <p class="mt-1 text-center text-sm text-muted">Nutricionist i Çertifikuar</p>
          <div class="mt-6 grid grid-cols-3 gap-2 border-t border-line pt-5 text-center">
            <div>
              <p class="text-xl font-bold text-accent">500+</p>
              <p class="mt-1 text-xs text-muted">Klientë</p>
            </div>
            <div>
              <p class="text-xl font-bold text-accent">8+</p>
              <p class="mt-1 text-xs text-muted">Vite</p>
            </div>
            <div>
              <p class="text-xl font-bold text-accent">95%</p>
              <p class="mt-1 text-xs text-muted">Sukses</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Scroll indicator (entrance on wrapper so fadeIn doesn't replace the bounce) -->
    <div class="entrance entrance-scroll absolute bottom-8 left-1/2 z-10 -translate-x-1/2">
      <div class="animate-bounce">
        <svg viewBox="0 0 24 24" fill="none" stroke="#737373" stroke-width="2" class="h-6 w-6">
          <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 8.25l-7.5 7.5-7.5-7.5" />
        </svg>
      </div>
    </div>
  </section>
</template>

<style scoped>
.orb-float {
  animation: orb-float 8s ease-in-out infinite;
}

@keyframes orb-float {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-30px);
  }
}

/* Entrance animations — elements start hidden, the section-level
   .animate class (set on mount) triggers the staggered sequence. */
.entrance {
  opacity: 0;
  visibility: hidden;
}

.animate .entrance {
  visibility: visible;
}

.animate .entrance-eyebrow {
  animation: slideInLeft 0.7s ease 0.1s forwards;
}

.animate .entrance-line1 {
  animation: slideInRight 0.7s ease 0.3s forwards;
}

.animate .entrance-line2 {
  animation: slideInLeft 0.7s ease 0.5s forwards;
}

.animate .entrance-sub {
  animation: slideInRight 0.6s ease 0.7s forwards;
}

.animate .entrance-cta {
  animation: slideInUp 0.6s ease 0.9s forwards;
}

.animate .entrance-card,
.animate .entrance-stats {
  animation: slideInRight 0.8s ease 1.1s forwards;
}

.animate .entrance-scroll {
  animation: fadeIn 0.6s ease 1.4s forwards;
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-60px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(60px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@media (prefers-reduced-motion: reduce) {
  .animate .entrance {
    animation: fadeIn 0.01ms 0s forwards !important;
  }
}
</style>
