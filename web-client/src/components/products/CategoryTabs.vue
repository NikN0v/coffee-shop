<script setup lang="ts">
import type {CategoryResponse} from "@/types/category.ts";
import {useI18n} from "vue-i18n";
import {computed} from "vue";

const props = defineProps<{
  categories: CategoryResponse[]
  selectedCategoryId: number | null
}>()

const emit = defineEmits<{
  select: [categoryId: number | null]
}>()

const { t, locale } = useI18n()

const localizedCategories = computed(() =>
  props.categories.map(category => ({
    id: category.id,
    name: locale.value.startsWith('en')
      ? category.nameEn
      : category.nameRu
  }))
)
</script>

<template>
  <nav class="category-tabs" :aria-label="t('menu.categories')">
    <button
      type="button"
      class="category-tabs__item"
      :class="{ 'is-active': selectedCategoryId === null }"
      :aria-pressed="selectedCategoryId === null"
      @click="emit('select', null)"
    >
      {{ t('menu.allProducts') }}
    </button>

    <button
      v-for="category in localizedCategories"
      :key="category.id"
      type="button"
      class="category-tabs__item"
      :class="{ 'is-active': selectedCategoryId === category.id }"
      :aria-pressed="selectedCategoryId === category.id"
      @click="emit('select', category.id)"
    >
      {{ category.name }}
    </button>
  </nav>
</template>

<style scoped>
.category-tabs {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding: 4px 2px 12px;
  scrollbar-width: thin;
  scrollbar-color: var(--coffee-border) transparent;
}

.category-tabs__item {
  flex-shrink: 0;
  padding: 10px 18px;
  border: 1px solid var(--coffee-border);
  border-radius: 999px;
  background: var(--coffee-bg);
  color: var(--coffee-text);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: color .2s ease, background-color .2s ease, border-color .2s ease;
}

.category-tabs__item:hover {
  border-color: var(--coffee-border-hover);
  background: var(--coffee-bg-hover);
}

.category-tabs__item.is-active {
  border-color: var(--coffee-accent);
  background: var(--coffee-accent);
  color: #fffaf4;
}

.category-tabs__item:focus-visible {
  outline: 3px solid var(--coffee-focus-ring);
  outline-offset: 2px;
}
</style>