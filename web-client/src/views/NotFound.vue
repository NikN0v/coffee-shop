<script setup lang="ts">
import {useI18n} from "vue-i18n";

const {t} = useI18n()
</script>

<template>
  <section class="mx-auto max-w-7xl px-4 py-20 text-center">
    <h1 class="text-4xl font-bold text-[#593a2b]">404</h1>
    <p class="mt-3 text-[#78675c]">
      {{ t('home.notFound') }}
    </p>
    <RouterLink
        to="/"
        class="mt-6 inline-flex text-[#a66b45] hover:underline"
    >
      {{ t('nav.home') }}
    </RouterLink>
  </section>
</template>