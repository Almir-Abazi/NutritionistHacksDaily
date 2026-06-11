<script setup>
import { useHead } from '@unhead/vue'
import BaseLayout from '../components/BaseLayout.vue'
import HeroSection from '../components/HeroSection.vue'
import CertificatesSection from '../components/CertificatesSection.vue'
import ResultsSection from '../components/ResultsSection.vue'
import FooterSection from '../components/FooterSection.vue'

useHead({
  title: 'NutritionistHacksDaily — Hasan Abazi | Nutricionist i Çertifikuar',
  meta: [
    {
      name: 'description',
      content:
        'Program i personalizuar për humbje peshe, rritje muskujsh dhe optimizim hormonal. Mbi 500 klientë të transformuar.',
    },
  ],
})
</script>

<template>
  <BaseLayout>
    <HeroSection />
    <CertificatesSection />
    <ResultsSection />
    <FooterSection />
  </BaseLayout>
</template>
