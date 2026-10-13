<script setup lang="ts">
import AppHeader from "@/components/layout/AppHeader.vue";
import AppFooter from "@/components/layout/AppFooter.vue";
</script>

<template>
  <div class="flex min-h-screen flex-col bg-[#faf7f2] text-[#35251f]">
    <AppHeader/>

    <main class="flex-1">
      <slot/>
    </main>

    <AppFooter/>
  </div>
</template>