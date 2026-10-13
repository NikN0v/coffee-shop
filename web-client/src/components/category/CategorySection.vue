<script setup lang="ts">
import {useI18n} from "vue-i18n";
import {useCategoryStore} from "@/stores/categoryStore.ts";
import {notificationService} from "@/services/notificationService.ts";
import {watch} from "vue";
import UiButton from "@/components/ui/UiButton.vue";
import {Burger, Coffee, Dessert, DishDot, Food} from "@element-plus/icons-vue";

const { locale, t } = useI18n({ useScope: 'global' })
const categoryStore = useCategoryStore()

async function loadCategories() {
  try {
    await categoryStore.fetchCategories()
  } catch {
    notificationService.error({
      title: t('categories.loadErrorTitle'),
      message: t('categories.loadErrorMessage')
    })
  }
}

const categoryTypes = {
  DRINKS: Coffee,
  DESSERTS: Dessert,
  BAKERY: Burger,
  FOOD: Food,
  OTHER: DishDot
}

watch(() => locale.value, () => {
  void loadCategories()
}, {immediate: true})
</script>

<template>
  <section class="mx-auto w-full max-w-7xl px-4 py-10 sm:px-6 lg:px-8">
    <div class="mb-6">
      <p class="mb-2 text-sm font-semibold uppercase tracking-[0.18em] text-[#a66b45]">
        CoffeeHouse
      </p>

      <h2 class="text-2xl font-bold text-[#35251f] sm:text-3xl">
        {{ t('categories.title') }}
      </h2>

      <p class="mt-2 text-sm text-[#78675c] sm:text-base">
        {{ t('categories.description') }}
      </p>
    </div>

    <div v-if="categoryStore.isLoading" class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
      <el-skeleton v-for="item in 3" :key="item" animated>
        <template #template>
          <el-skeleton-item variant="rect" class="!h-24 !rounded-2xl"/>
        </template>
      </el-skeleton>
    </div>

    <el-empty v-else-if="categoryStore.error" :description="t('categories.loadErrorMessage')">
      <UiButton @click="loadCategories">
        {{ t('categories.retry') }}
      </UiButton>
    </el-empty>

    <el-empty v-else-if="categoryStore.isEmpty" :description="t('categories.empty')"/>

    <div v-else class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
      <el-card v-for="category in categoryStore.categories" :key="category.id" shadow="hover" class="category-card">
        <div class="flex items-center justify-between gap-3">
          <h3 class="text-lg font-semibold text-[#35251f]">
            {{ category.name }}
          </h3>

          <el-icon :size="22" color="#a66b45">
            <component :is="categoryTypes[category.type]"/>
          </el-icon>
        </div>
      </el-card>
    </div>
  </section>
</template>

<style scoped>
.category-card {
  border-color: #e9dfd3;
  border-radius: 16px;
  background-color: #fffdf9;
  transition: transform .2s ease, box-shadow .2s ease;
}

.category-card:hover {
  transform: translateY(-3px);
}

.category-card :deep(.el-card__body) {
  padding: 22px;
}
</style>