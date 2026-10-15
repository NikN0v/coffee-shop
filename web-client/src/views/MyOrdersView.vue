<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'

import { orderApi } from '@/api/orderApi'
import { notificationService } from '@/services/notificationService'
import type { OrderResponse, OrderStatus } from '@/types/order'
import {ArrowRight, Close} from "@element-plus/icons-vue";
import UiButton from "@/components/ui/UiButton.vue";
import {ElMessageBox} from "element-plus";

const { t, locale } = useI18n()
const router = useRouter()

const orders = ref<OrderResponse[]>([])
const loading = ref(false)

function formatPrice(price: number): string {
  return new Intl.NumberFormat(
      locale.value === 'ru' ? 'ru-RU' : 'en-IE',
      {
        style: 'currency',
        currency: 'RUB',
      },
  ).format(price)
}

function formatDate(value: string): string {
  const date = new Date(value)

  if (Number.isNaN(date.getTime())) {
    return value
  }

  return new Intl.DateTimeFormat(
      locale.value === 'ru' ? 'ru-RU' : 'en-IE',
      {
        dateStyle: 'medium',
        timeStyle: 'short',
      },
  ).format(date)
}

function statusType(
    status: OrderStatus,
): 'success' | 'warning' | 'danger' | 'info' {
  switch (status) {
    case 'READY':
    case 'PICKED_UP':
      return 'success'

    case 'CANCELLED':
      return 'danger'

    case 'CREATED':
      return 'warning'

    default:
      return 'info'
  }
}

async function loadOrders(): Promise<void> {
  loading.value = true

  try {
    const result = await orderApi.getMyOrders()

    orders.value = [...result].sort(
        (a, b) =>
            new Date(b.createdAt).getTime() -
            new Date(a.createdAt).getTime(),
    )
  } catch (error) {
    console.error('Failed to load user orders:', error)

    notificationService.error({
      title: t('common.error'),
      message: t('myOrders.loadError'),
    })
  } finally {
    loading.value = false
  }
}

async function cancelOrder(id: number): Promise<void> {
  loading.value = true

  try {
    await ElMessageBox.confirm(
        t('myOrders.cancelConfirmation'),
        t('myOrders.confirmTitle'),
        {
          confirmButtonText: t('myOrders.cancel'),
          cancelButtonText: t('common.cancel'),
          type: 'error',
        },
    )

    await orderApi.cancelMyOrder(id)
    await loadOrders()
    notificationService.success({
      title: 'common.success',
      message: 'myOrders.cancelSuccess'
    })
  } catch {
    notificationService.error({
      title: t('common.error'),
      message: t('myOrders.loadError'),
    })
  } finally {
    loading.value = false
  }
}

function openOrder(id: number): void {
  router.push({
    name: 'my-order-detail',
    params: { id },
  })
}

onMounted(() => {
  void loadOrders()
})
</script>

<template>
  <section class="my-orders">
    <header class="my-orders__header">
      <div>
        <p class="my-orders__eyebrow">
          {{ t('myOrders.eyebrow') }}
        </p>

        <h1 class="my-orders__title">
          {{ t('myOrders.title') }}
        </h1>

        <p class="my-orders__description">
          {{ t('myOrders.description') }}
        </p>
      </div>

      <el-button
          class="refresh-button"
          :loading="loading"
          @click="loadOrders"
      >
        {{ t('myOrders.refresh') }}
      </el-button>
    </header>

    <div v-loading="loading" class="my-orders__content">
      <el-empty
          v-if="!loading && orders.length === 0"
          :description="t('myOrders.empty')"
      />

      <div v-else-if="orders.length > 0" class="my-orders__list">
        <el-card
            v-for="order in orders"
            :key="order.id"
            class="order-card"
            shadow="never"
        >
          <div class="order-card__header">
            <div>
              <h2 class="order-card__number">
                {{ t('myOrders.orderNumber', { id: order.id }) }}
              </h2>

              <p class="order-card__date">
                {{ formatDate(order.createdAt) }}
              </p>
            </div>

            <el-tag
                :type="statusType(order.status)"
                effect="light"
            >
              {{ t(`orders.status.${order.status}`) }}
            </el-tag>
          </div>

          <div class="order-card__items">
            <div
                v-for="(item, index) in order.items.slice(0, 3)"
                :key="`${item.productId}-${index}`"
                class="order-card__item"
            >
              <span class="order-card__product">
                {{ item.productName }}
                <span class="order-card__quantity">
                  × {{ item.quantity }}
                </span>
              </span>

              <span class="order-card__price">
                {{ formatPrice(item.totalPrice) }}
              </span>
            </div>

            <p
                v-if="order.items.length > 3"
                class="order-card__more"
            >
              {{
                t('myOrders.moreItems', {
                  count: order.items.length - 3,
                })
              }}
            </p>
          </div>

          <footer class="order-card__footer">
            <div>
              <span class="order-card__total-label">
                {{ t('myOrders.total') }}
              </span>

              <strong class="order-card__total">
                {{ formatPrice(order.totalPrice) }}
              </strong>
            </div>

            <div style="display: flex; align-items: center; flex-direction: row">
              <UiButton
                  class="details-button"
                  @click="openOrder(order.id)"
              >
                {{ t('myOrders.details') }}
                <el-icon class="details-button__icon">
                  <ArrowRight />
                </el-icon>
              </UiButton>

              <UiButton
                  type="default"
                  class="details-button"
                  @click="cancelOrder(order.id)"
              >
                {{ t('myOrders.cancel') }}
                <el-icon class="details-button__icon">
                  <Close />
                </el-icon>
              </UiButton>
            </div>
          </footer>
        </el-card>
      </div>
    </div>
  </section>
</template>

<script lang="ts">
export default {
  name: 'MyOrdersView',
}
</script>

<style scoped>
.my-orders {
  width: 100%;
  max-width: 1100px;
  margin: 0 auto;
  padding: 28px;
  color: var(--coffee-text);
}

.my-orders__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 28px;
}

.my-orders__eyebrow {
  margin: 0 0 8px;
  color: var(--coffee-accent);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.1em;
  text-transform: uppercase;
}

.my-orders__title {
  margin: 0;
  color: var(--coffee-text-dark);
  font-size: clamp(26px, 4vw, 34px);
  font-weight: 700;
}

.my-orders__description {
  margin: 10px 0 0;
  color: var(--coffee-text-muted);
  font-size: 14px;
  line-height: 1.6;
}

.my-orders__content {
  min-height: 180px;
}

.my-orders__list {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.order-card {
  overflow: hidden;
  border: 1px solid var(--coffee-border);
  border-radius: 16px;
  background: var(--coffee-bg);
}

.order-card__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 20px;
}

.order-card__number {
  margin: 0;
  color: var(--coffee-text-dark);
  font-size: 17px;
  font-weight: 700;
}

.order-card__date {
  margin: 6px 0 0;
  color: var(--coffee-text-muted);
  font-size: 13px;
}

.order-card__items {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px 0;
  border-top: 1px solid var(--coffee-border);
  border-bottom: 1px solid var(--coffee-border);
}

.order-card__item {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.order-card__product {
  min-width: 0;
  color: var(--coffee-text-dark);
  font-size: 14px;
  overflow-wrap: anywhere;
}

.order-card__quantity {
  color: var(--coffee-text-muted);
  white-space: nowrap;
}

.order-card__price {
  flex-shrink: 0;
  color: var(--coffee-text-dark);
  font-size: 14px;
  white-space: nowrap;
}

.order-card__more {
  margin: 0;
  color: var(--coffee-text-muted);
  font-size: 13px;
}

.order-card__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding-top: 20px;
}

.order-card__footer > div {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.order-card__total-label {
  color: var(--coffee-text-muted);
  font-size: 12px;
}

.order-card__total {
  color: var(--coffee-text-dark);
  font-size: 20px;
}

.refresh-button,
.details-button {
  --el-button-bg-color: var(--coffee-accent);
  --el-button-border-color: var(--coffee-accent);
  --el-button-text-color: #fffaf4;
  --el-button-hover-bg-color: var(--coffee-accent-hover);
  --el-button-hover-border-color: var(--coffee-accent-hover);
  --el-button-hover-text-color: #fffaf4;
}

.details-button__icon {
  margin-left: 6px;
}

@media (max-width: 600px) {
  .my-orders {
    padding: 20px 12px;
  }

  .my-orders__header {
    align-items: flex-start;
    flex-direction: column;
  }

  .order-card__header {
    align-items: flex-start;
  }

  .order-card__footer {
    align-items: flex-start;
    flex-direction: column;
  }

  .details-button {
    width: 100%;
  }
}
</style>