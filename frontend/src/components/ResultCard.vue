<script setup>
import { ref } from 'vue'

defineProps({
  result: { type: Object, required: true },
})

const hovered = ref(false)

function formatWeight(weightLossKg) {
  if (weightLossKg == null) return ''
  return weightLossKg < 0 ? `+${Math.abs(weightLossKg)} kg` : `-${weightLossKg} kg`
}
</script>

<template>
  <div
    class="h-full overflow-hidden rounded-2xl border border-line bg-surface2 transition-transform duration-300 will-change-transform"
    :style="{ transform: hovered ? 'perspective(1000px) rotateX(4deg) scale(1.02)' : 'perspective(1000px) rotateX(0deg) scale(1)' }"
    @mouseenter="hovered = true"
    @mouseleave="hovered = false"
  >
    <div class="flex h-[220px] items-center justify-center bg-surface">
      <img
        v-if="result.photoFilename"
        :src="`/api/v1/files/${result.photoFilename}`"
        :alt="result.clientName"
        class="h-full w-full object-cover"
      />
      <svg v-else viewBox="0 0 24 24" fill="none" stroke="#737373" stroke-width="1.5" class="h-16 w-16">
        <path stroke-linecap="round" stroke-linejoin="round" d="M17.982 18.725A7.488 7.488 0 0012 15.75a7.488 7.488 0 00-5.982 2.975m11.963 0a9 9 0 10-11.963 0m11.963 0A8.966 8.966 0 0112 21a8.966 8.966 0 01-5.982-2.275M15 9.75a3 3 0 11-6 0 3 3 0 016 0z" />
      </svg>
    </div>
    <div class="p-6">
      <div class="flex items-baseline justify-between">
        <h3 class="font-semibold text-primary">{{ result.clientName }}</h3>
        <span class="text-sm text-muted">{{ result.age }} vjeç</span>
      </div>
      <span
        v-if="result.diagnosis"
        class="mt-3 inline-block rounded-full border border-accent/20 bg-accent/10 px-3 py-1 text-xs text-accent"
      >
        {{ result.diagnosis }}
      </span>
      <p class="mt-4 font-display text-5xl font-black text-accent">
        {{ formatWeight(result.weightLossKg) }}
      </p>
      <p class="mt-2 text-sm text-muted">{{ result.durationMonths }} muaj program</p>
      <p class="mt-4 line-clamp-3 text-sm text-muted">{{ result.description }}</p>
    </div>
  </div>
</template>
