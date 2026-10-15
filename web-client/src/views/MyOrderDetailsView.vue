<script setup lang="ts">
import {useRoute, useRouter} from "vue-router";
import {useI18n} from "vue-i18n";
import {computed, onMounted, ref, watch} from "vue";
import type {OrderResponse, OrderStatus} from "@/types/order.ts";
import {notificationService} from "@/services/notificationService.ts";
import {orderApi} from "@/api/orderApi.ts";

const route = useRoute()
const router = useRouter()
const {t, locale} = useI18n()

const order = ref<OrderResponse | null>(null)
const loading = ref(false)

const currencyFormatter = computed(() =>
    new Intl.NumberFormat(locale.value === 'ru' ? 'ru-RU' : 'en-IE', {
      style: 'currency',
      currency: 'RUB'
    })
)

function formatPrice(value: number): string {
  return currencyFormatter.value.format(value)
}

function formatDate(value: string): string {
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value

  return new Intl.DateTimeFormat(
      locale.value === 'ru' ? 'ru-RU' : 'en-IE', {
        dateStyle: 'medium',
        timeStyle: 'short'
      },
  ).format(date)
}

function statusType(status: OrderStatus): 'success' | 'warning' | 'danger' | 'info' {
  switch (status) {
    case "READY":
    case "PICKED_UP":
      return "success"
    case "CANCELLED":
      return "danger"
    case "CREATED":
      return "warning"
    default:
      return "info"
  }
}

async function loadOrder(): Promise<void> {
  const id = Number(route.params.id)

  if (!Number.isSafeInteger(id) || id <= 0) {
    notificationService.error({
      title: t('common.error'),
      message: t('myOrder.invalidId')
    })
    return
  }

  loading.value = true

  try {
    order.value = await orderApi.getMyOrder(id)
  } catch (e) {
    notificationService.error({
      title: t('common.error'),
      message: t('myOrder.loadError')
    })
  } finally {
    loading.value = false
  }
}

function goBack(): void {
  router.push({name: 'orders'})
}

onMounted(loadOrder)

watch(() => route.params.id, () => void loadOrder())
</script>

<template>
  <section class="order-details">
    <button class="back-button" type="button" @click="goBack">
      ← {{ t('myOrder.back') }}
    </button>

    <div v-loading="loading" class="order-details__content">
      <template v-if="order">
        <header class="order-details__header">
          <div>
            <p class="order-details__eyebrow">
              {{ t('myOrder.eyebrow') }}
            </p>

            <h1 class="order-details__title">
              {{ t('myOrder.title', {id: order.id}) }}
            </h1>

            <p class="order-details__date">
              {{ formatDate(order.createdAt) }}
            </p>
          </div>

          <el-tag
              size="large"
              effect="light"
              :type="statusType(order.status)"
          >
            {{ t(`orders.status.${order.status}`) }}
          </el-tag>
        </header>

        <el-card shadow="never" class="order-card">
          <template #header>
            <h2 class="order-card__heading">
              {{ t('myOrder.itemsTitle') }}
            </h2>
          </template>

          <div
              v-for="(item, index) in order.items"
              :key="`${item.productId}-${index}`"
              class="order-item"
          >
            <div class="order-item__info">
              <span class="order-item__name">
                {{ item.productName }}
              </span>

              <span class="order-item__quantity">
                {{ item.quantity }} × {{ formatPrice(item.unitPrice) }}
              </span>
            </div>

            <strong class="order-item__price">
              {{ formatPrice(item.totalPrice) }}
            </strong>
          </div>

          <div class="order-total">
            <span>{{ t('myOrder.total') }}</span>
            <strong>{{ formatPrice(order.totalPrice) }}</strong>
          </div>
        </el-card>
      </template>
    </div>
  </section>
</template>

<style scoped>
.order-details {
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
  padding: 28px;
  color: var(--coffee-text);
}

.back-button {
  margin-bottom: 24px;
  padding: 8px 0;
  border: 0;
  background: transparent;
  color: var(--coffee-accent);
  font: inherit;
  cursor: pointer;
}

.back-button:hover {
  color: var(--coffee-accent-hover);
}

.order-details__content {
  min-height: 180px;
}

.order-details__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 24px;
}

.order-details__eyebrow {
  margin: 0 0 8px;
  color: var(--coffee-accent);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.1em;
  text-transform: uppercase;
}

.order-details__title {
  margin: 0;
  color: var(--coffee-text-dark);
  font-size: clamp(25px, 4vw, 34px);
  overflow-wrap: anywhere;
}

.order-details__date {
  margin: 10px 0 0;
  color: var(--coffee-text-muted);
  font-size: 14px;
}

.order-card {
  border: 1px solid var(--coffee-border);
  border-radius: 16px;
  background: var(--coffee-bg);
}

.order-card :deep(.el-card__header) {
  border-bottom-color: var(--coffee-border);
}

.order-card__heading {
  margin: 0;
  color: var(--coffee-text-dark);
  font-size: 17px;
}

.order-item {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid var(--coffee-border);
}

.order-item__info {
  display: flex;
  min-width: 0;
  flex-direction: column;
  gap: 6px;
}

.order-item__name {
  color: var(--coffee-text-dark);
  font-size: 14px;
  font-weight: 600;
  overflow-wrap: anywhere;
}

.order-item__quantity {
  color: var(--coffee-text-muted);
  font-size: 13px;
}

.order-item__price {
  flex-shrink: 0;
  color: var(--coffee-text-dark);
  white-space: nowrap;
}

.order-total {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding-top: 22px;
}

.order-total strong {
  color: var(--coffee-text-dark);
  font-size: 22px;
  white-space: nowrap;
}

@media (max-width: 600px) {
  .order-details {
    padding: 20px 12px;
  }

  .order-details__header {
    flex-direction: column;
    gap: 14px;
  }

  .order-item {
    gap: 10px;
  }
}
</style>