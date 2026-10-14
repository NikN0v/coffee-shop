<script setup lang="ts">
import {useI18n} from 'vue-i18n'
import type {ProductResponse} from '@/types/product'
import ProductCard from './ProductCard.vue'

defineProps<{
  products: ProductResponse[]
  loading?: boolean
}>()

const {t} = useI18n()
</script>

<template>
  <div v-loading="loading" class="product-grid-wrapper">
    <div v-if="products.length" class="product-grid">
      <ProductCard
          v-for="product in products"
          :key="product.id"
          :product="product"
      />
    </div>

    <el-empty
        v-else-if="!loading"
        :description="t('menu.empty')"
        :image-size="110"
    />
  </div>
</template>

<style scoped>
.product-grid-wrapper {
  min-height: 220px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 22px;
}

@media (max-width: 1100px) {
  .product-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 18px;
  }
}

@media (max-width: 760px) {
  .product-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 14px;
  }
}

@media (max-width: 360px) {
  .product-grid {
    grid-template-columns: minmax(0, 1fr);
  }
}
</style>