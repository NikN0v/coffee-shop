<script setup lang="ts">
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
</script>

<template>
  <footer id="about" class="bg-[#30231d] text-[#f5eee6]">
    <div class="mx-auto flex max-w-7xl flex-col gap-4 px-4 py-8 sm:px-6 md:flex-row md:items-center md:justify-between lg:px-8">
      <RouterLink to="/" class="text-lg font-bold">
        CoffeeHouse
      </RouterLink>

      <p class="text-sm text-[#c9b9ab]">
        {{ t('footer.tagline') }}
      </p>

      <p class="text-sm text-[#c9b9ab]">
        © {{ new Date().getFullYear() }} CoffeeHouse
      </p>
    </div>
  </footer>
</template>