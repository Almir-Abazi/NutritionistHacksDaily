<script setup>
import { useRouter } from 'vue-router'
import FloatingWhatsApp from '../components/FloatingWhatsApp.vue'

const router = useRouter()
</script>

<template>
  <div class="relative flex min-h-screen items-center justify-center overflow-hidden bg-base font-body">
    <!-- Background orb, same family as the hero decoration -->
    <div class="pointer-events-none absolute -top-32 left-1/2 -ml-[250px]">
      <div class="h-[500px] w-[500px] rounded-full bg-[radial-gradient(circle,#7a7257_0%,transparent_70%)] opacity-[0.08] blur-3xl"></div>
    </div>

    <div class="relative z-10 flex flex-col items-center px-6 text-center">
      <p class="nf-enter nf-number font-display text-[100px] font-black leading-none text-accent md:text-[160px]">
        404
      </p>
      <h1 class="nf-enter nf-title mt-4 font-display text-[32px] font-bold text-primary">
        Faqja nuk u gjet
      </h1>
      <p class="nf-enter nf-subtext mt-2 text-base text-muted">
        Faqja që kërkoni nuk ekziston ose është zhvendosur.
      </p>
      <button
        class="nf-enter nf-button mt-8 rounded-full bg-accent px-8 py-4 font-semibold text-cream transition-transform duration-300 hover:scale-105"
        @click="router.push('/')"
      >
        Kthehu në Shtëpi &rarr;
      </button>
    </div>

    <FloatingWhatsApp />
  </div>
</template>

<style scoped>
.nf-number {
  text-shadow: 0 0 40px rgba(122, 114, 87, 0.35);
}

.nf-enter {
  opacity: 0;
}

.nf-number {
  animation: slideInUp 0.6s ease 0.1s forwards;
}

.nf-title {
  animation: slideInUp 0.6s ease 0.3s forwards;
}

.nf-subtext {
  animation: slideInUp 0.6s ease 0.4s forwards;
}

.nf-button {
  animation: slideInUp 0.6s ease 0.5s forwards;
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

@media (prefers-reduced-motion: reduce) {
  .nf-enter {
    animation-delay: 0s !important;
  }
}
</style>
