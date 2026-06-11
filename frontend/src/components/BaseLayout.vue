<script setup>
import FloatingWhatsApp from './FloatingWhatsApp.vue'
</script>

<template>
  <div class="min-h-screen bg-base font-body text-primary">
    <slot />
    <FloatingWhatsApp />
  </div>
</template>
