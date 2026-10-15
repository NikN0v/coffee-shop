<script setup lang="ts">
import {computed, onMounted, ref} from 'vue'
import {useI18n} from 'vue-i18n'

import {productApi} from '@/api/productApi'
import {useCategoryStore} from '@/stores/categoryStore'
import type {ProductResponse} from '@/types/product'

import CategoryTabs from '@/components/products/CategoryTabs.vue'
import ProductGrid from '@/components/products/ProductGrid.vue'
import {CoffeeCup} from "@element-plus/icons-vue";
import {notificationService} from "@/services/notificationService.ts";

const {t} = useI18n()
const categoryStore = useCategoryStore()

const products = ref<ProductResponse[]>([])
const selectedCategoryId = ref<number | null>(null)

const isLoading = ref(false)
const page = ref(0)
const pageSize = 12
const totalElements = ref(0)

const totalPages = computed(() =>
    Math.ceil(totalElements.value / pageSize),
)

let requestId = 0

async function loadProducts(): Promise<void> {
  const currentRequestId = ++requestId
  isLoading.value = true

  try {
    const params = {
      available: true,
      page: page.value,
      size: pageSize,
      sortBy: 'id',
      direction: 'asc' as const,
      ...(selectedCategoryId.value !== null
          ? {categoryId: selectedCategoryId.value}
          : {}),
    }

    const {data} = await productApi.getAll(params)

    if (currentRequestId !== requestId) return

    products.value = data.content
    page.value = data.page
    totalElements.value = data.totalElements
  } catch {
    if (currentRequestId === requestId) {
      notificationService.error({
        title: t('common.error'),
        message: t('menu.loadProductsError')
      })
    }
  } finally {
    if (currentRequestId === requestId) {
      isLoading.value = false
    }
  }
}

async function selectCategory(categoryId: number | null): Promise<void> {
  if (selectedCategoryId.value === categoryId) return

  selectedCategoryId.value = categoryId
  page.value = 0

  await loadProducts()
}

async function changePage(currentPage: number): Promise<void> {
  page.value = currentPage - 1
  await loadProducts()
}

onMounted(async () => {
  try {
    await categoryStore.fetchCategories()
  } catch {
    notificationService.error({
      title: t('common.error'),
      message: t('menu.loadCategoriesError')
    })
  }

  await loadProducts()
})
</script>

<template>
  <main class="menu-page">
    <section class="menu-hero">
      <div class="menu-hero__content">
        <span class="menu-hero__eyebrow">
          {{ t('menu.eyebrow') }}
        </span>

        <h1 class="menu-hero__title">
          {{ t('menu.title') }}
        </h1>

        <p class="menu-hero__description">
          {{ t('menu.description') }}
        </p>
      </div>

      <div class="menu-hero__decoration" aria-hidden="true">
        <img
            src="/images/hero-coffee.jpg"
            alt="Свежеприготовленный кофе в уютной кофейне"
            class="relative w-full rounded-4xl"
            fetchpriority="high"
        />
      </div>
    </section>

    <section class="menu-catalog">
      <div class="menu-catalog__heading">
        <div>
          <h2 class="menu-catalog__title">
            {{ t('menu.catalogTitle') }}
          </h2>

          <p class="menu-catalog__subtitle">
            {{ t('menu.catalogSubtitle') }}
          </p>
        </div>

        <span class="menu-catalog__count">
          {{ t('menu.productCount', {count: totalElements}) }}
        </span>
      </div>

      <CategoryTabs
          :categories="categoryStore.categories"
          :selected-category-id="selectedCategoryId"
          @select="selectCategory"
      />

      <ProductGrid
          :products="products"
          :loading="isLoading"
      />

      <div
          v-if="totalPages > 1"
          class="menu-pagination"
      >
        <el-pagination
            :current-page="page + 1"
            :page-size="pageSize"
            :total="totalElements"
            :pager-count="5"
            layout="prev, pager, next"
            background
            @current-change="changePage"
        />
      </div>
    </section>
  </main>
</template>

<style scoped>
.menu-page {
  width: min(1240px, 100%);
  margin: 0 auto;
  padding: 28px 24px 56px;
}

.menu-hero {
  position: relative;
  display: flex;
  min-height: 220px;
  align-items: center;
  justify-content: space-between;
  overflow: hidden;
  padding: 36px 44px;
  border: 1px solid var(--coffee-border);
  border-radius: 22px;
  background: radial-gradient(circle at 85% 40%, rgb(201 169 142 / 24%), transparent 30%),
  var(--coffee-bg-hover);
}

.menu-hero__content {
  position: relative;
  z-index: 1;
  max-width: 650px;
}

.menu-hero__eyebrow {
  color: var(--coffee-accent);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.menu-hero__title {
  margin: 12px 0;
  color: var(--coffee-text-dark);
  font-size: clamp(30px, 4vw, 44px);
  font-weight: 700;
  line-height: 1.15;
}

.menu-hero__description {
  max-width: 540px;
  margin: 0;
  color: var(--coffee-text);
  font-size: 15px;
  line-height: 1.7;
}

.menu-hero__decoration {
  display: grid;
  width: 132px;
  height: 132px;
  flex-shrink: 0;
  place-items: center;
  margin-left: 24px;
  border: 1px solid rgb(121 81 58 / 16%);
  border-radius: 50%;
  background: rgb(255 250 244 / 65%);
  color: var(--coffee-accent);
}

.menu-catalog {
  padding-top: 36px;
}

.menu-catalog__heading {
  display: flex;
  align-items: end;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;
}

.menu-catalog__title {
  margin: 0;
  color: var(--coffee-text-dark);
  font-size: 26px;
  font-weight: 700;
}

.menu-catalog__subtitle {
  margin: 7px 0 0;
  color: var(--coffee-text-muted);
  font-size: 14px;
}

.menu-catalog__count {
  flex-shrink: 0;
  color: var(--coffee-text-muted);
  font-size: 13px;
}

.menu-pagination {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

@media (max-width: 640px) {
  .menu-page {
    padding: 16px 14px 36px;
  }

  .menu-hero {
    min-height: 180px;
    padding: 26px 22px;
    border-radius: 18px;
  }

  .menu-hero__decoration {
    width: 76px;
    height: 76px;
    margin-left: 12px;
  }

  .menu-hero__decoration :deep(.el-icon) {
    font-size: 44px !important;
  }

  .menu-hero__description {
    font-size: 14px;
  }

  .menu-catalog {
    padding-top: 28px;
  }

  .menu-catalog__heading {
    align-items: start;
  }

  .menu-catalog__title {
    font-size: 22px;
  }
}

@media (max-width: 380px) {
  .menu-hero__decoration {
    display: none;
  }
}
</style>