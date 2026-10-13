<script setup lang="ts">
import LanguageSwitcher from '@/components/common/LanguageSwitcher.vue'
</script>

<template>
  <div class="relative flex min-h-screen items-center justify-center overflow-hidden bg-[#faf7f2] px-4 py-12">
    <div class="absolute -left-32 -top-32 h-80 w-80 rounded-full bg-[#e9d9c7]/60 blur-3xl" />
    <div class="absolute -bottom-32 -right-32 h-80 w-80 rounded-full bg-[#d7b89b]/40 blur-3xl" />

    <div class="absolute right-4 top-4 z-10 sm:right-8 sm:top-8">
      <LanguageSwitcher />
    </div>

    <div class="relative z-10 w-full max-w-md">
      <RouterLink
          to="/"
          class="mb-6 flex items-center justify-center gap-2 text-2xl font-bold tracking-tight"
      >
        <span aria-hidden="true">☕</span>
        <span>Coffee<span class="text-[#a66b45]">House</span></span>
      </RouterLink>

      <slot />
    </div>
  </div>
</template>