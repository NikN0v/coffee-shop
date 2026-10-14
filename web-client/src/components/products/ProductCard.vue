<script setup lang="ts">
import {computed} from 'vue'
import {useI18n} from 'vue-i18n'
import type {ProductResponse} from '@/types/product'
import {CoffeeCup, Dessert} from "@element-plus/icons-vue";

const props = defineProps<{
  product: ProductResponse
}>()

const {t, locale} = useI18n()

const isEnglish = computed(() => locale.value.startsWith('en'))

const name = computed(() =>
    isEnglish.value ? props.product.nameEn : props.product.nameRu,
)

const description = computed(() =>
    isEnglish.value
        ? props.product.descriptionEn
        : props.product.descriptionRu,
)

const formattedPrice = computed(() =>
    new Intl.NumberFormat(isEnglish.value ? 'en-IE' : 'ru-RU', {
      style: 'currency',
      currency: 'RUB',
    }).format(props.product.price),
)

const sizeLabel = computed(() => {
  if (props.product.isDrink && props.product.volume != null) {
    return `${props.product.volume} ${t('menu.ml')}`
  }

  if (props.product.weight != null) {
    return `${props.product.weight} ${t('menu.g')}`
  }

  return ''
})
</script>

<template>
  <article class="product-card">
    <div class="product-card__image">
      <div class="product-card__illustration" aria-hidden="true">
        <component :is="product.isDrink ? CoffeeCup : Dessert" class="product-card__icon"/>
      </div>
      <span v-if="sizeLabel" class="product-card__size">
        {{ sizeLabel }}
      </span>
    </div>

    <div class="product-card__content">
      <h3 class="product-card__name">
        {{ name }}
      </h3>

      <p v-if="description" class="product-card__description">
        {{ description }}
      </p>

      <div class="product-card__footer">
        <span class="product-card__price">
          {{ formattedPrice }}
        </span>

        <span class="product-card__availability">
          <span class="product-card__availability-dot"/>
          {{ t('menu.available') }}
        </span>
      </div>
    </div>
  </article>
</template>

<style scoped>
.product-card {
  display: flex;
  min-width: 0;
  flex-direction: column;
  overflow: hidden;
  border: 1px solid var(--coffee-border);
  border-radius: 16px;
  background: var(--coffee-bg);
  transition: transform 0.2s ease,
  box-shadow 0.2s ease,
  border-color 0.2s ease;
}

.product-card:hover {
  transform: translateY(-3px);
  border-color: var(--coffee-border-hover);
  box-shadow: 0 10px 26px rgb(66 39 25 / 8%);
}

.product-card__image {
  position: relative;
  display: grid;
  min-height: 170px;
  place-items: center;
  overflow: hidden;
  background: radial-gradient(circle at 50% 45%, #fffaf4 0%, transparent 55%),
  var(--coffee-bg-hover);
}

.product-card__illustration {
  display: grid;
  width: 96px;
  height: 96px;
  place-items: center;
  border: 1px solid rgb(121 81 58 / 16%);
  border-radius: 50%;
  background: rgb(255 250 244 / 75%);
  color: var(--coffee-accent);
}

.product-card__size {
  position: absolute;
  right: 12px;
  bottom: 12px;
  padding: 5px 9px;
  border: 1px solid var(--coffee-border);
  border-radius: 999px;
  background: var(--coffee-bg);
  color: var(--coffee-text);
  font-size: 12px;
}

.product-card__content {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 10px;
  padding: 16px;
}

.product-card__name {
  margin: 0;
  color: var(--coffee-text-dark);
  font-size: 17px;
  font-weight: 600;
  line-height: 1.4;
  overflow-wrap: anywhere;
}

.product-card__description {
  display: -webkit-box;
  margin: 0;
  overflow: hidden;
  color: var(--coffee-text-muted);
  font-size: 13px;
  line-height: 1.55;
  overflow-wrap: anywhere;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
}

.product-card__footer {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-top: auto;
  padding-top: 8px;
}

.product-card__price {
  color: var(--coffee-accent);
  font-size: 18px;
  font-weight: 700;
}

.product-card__availability {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: var(--coffee-text-muted);
  font-size: 11px;
}

.product-card__availability-dot {
  width: 7px;
  height: 7px;
  flex-shrink: 0;
  border-radius: 50%;
  background: #63845b;
}

.product-card__icon {
  width: 48px;
  height: 48px;
}

@media (max-width: 480px) {
  .product-card__image {
    min-height: 135px;
  }

  .product-card__content {
    padding: 12px;
  }

  .product-card__name {
    font-size: 15px;
  }

  .product-card__price {
    font-size: 16px;
  }
}
</style>