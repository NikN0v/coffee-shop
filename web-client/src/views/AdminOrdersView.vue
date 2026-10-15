<script setup lang="ts">
import {useI18n} from "vue-i18n";
import {computed, onMounted, ref} from "vue";
import type {OrderResponse, OrderStatus} from "@/types/order.ts";
import {orderApi} from "@/api/orderApi.ts";
import {notificationService} from "@/services/notificationService.ts";
import UiButton from "@/components/ui/UiButton.vue";

const {t, locale} = useI18n()

const orders = ref<OrderResponse[]>([])
const loading = ref(false)
const filterStatus = ref<OrderStatus | 'ALL'>('ALL')
const updatingIds = ref<number[]>([])

const statuses: OrderStatus[] = [
  'CREATED',
  'ACCEPTED',
  'PREPARING',
  'READY',
  'PICKED_UP',
  'CANCELLED'
]

const filteredOrders = computed(() => {
  if (filterStatus.value === 'ALL') return orders.value
  return orders.value.filter(order => order.status === filterStatus.value)
})

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

function statusLabel(status: OrderStatus): string {
  return t(`orders.status.${status}`)
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

function availableStatuses(status: OrderStatus): OrderStatus[] {
  const transition: Record<OrderStatus, OrderStatus[]> = {
    CREATED: ['ACCEPTED', 'CANCELLED'],
    ACCEPTED: ['PREPARING', 'CANCELLED'],
    PREPARING: ['READY', 'CANCELLED'],
    READY: ['PICKED_UP'],
    PICKED_UP: [],
    CANCELLED: []
  }

  return [status, ...transition[status]]
}

async function loadOrders(): Promise<void> {
  loading.value = true

  try {
    orders.value = await orderApi.getAll()
  } catch (err) {
    notificationService.error({
      title: t('common.error'),
      message: t('orders.loadError')
    })
  } finally {
    loading.value = false
  }
}

async function changeStatus(order: OrderResponse, nextStatus: OrderStatus): Promise<void> {
  if (nextStatus === order.status || updatingIds.value.includes(order.id)) return

  const previousStatus = order.status
  updatingIds.value.push(order.id)

  try {
    const updatedOrder = await orderApi.updateStatus(order.id, nextStatus)
    const index = orders.value.findIndex(item => item.id === order.id)
    if (index !== -1) orders.value[index] = updatedOrder

    notificationService.success({
      title: t('orders.statusUpdatedTitle'),
      message: t('orders.statusUpdatedMessage', {
        id: order.id,
        status: statusLabel(updatedOrder.status)
      })
    })
  } catch (err) {
    order.status = previousStatus
    notificationService.error({
      title: t('common.error'),
      message: t('orders.updateStatusError')
    })
  } finally {
    updatingIds.value = updatingIds.value.filter(id => id !== order.id)
  }
}

onMounted(() => {
  void loadOrders()
})
</script>

<template>
  <section class="admin-orders">
    <header class="admin-orders__header">
      <div>
        <p class="admin-orders__eyebrow">
          {{ t('orders.adminEyebrow') }}
        </p>

        <h1 class="admin-orders__title">
          {{ t('orders.adminTitle') }}
        </h1>

        <p class="admin-orders__description">
          {{ t('orders.adminDescription') }}
        </p>
      </div>

      <UiButton
          type="default"
          class="coffee-button"
          :loading="loading"
          @click="loadOrders"
      >
        {{ t('orders.refresh') }}
      </UiButton>
    </header>

    <div class="admin-orders__toolbar">
      <div class="admin-orders__count">
        {{ t('orders.count') }}:
        <strong>{{ filteredOrders.length }}</strong>
      </div>

      <el-select
          v-model="filterStatus"
          class="coffee-select"
          style="width: 200px"
          :placeholder="t('orders.filterStatus')"
      >
        <el-option
            value="ALL"
            :label="t('orders.allStatuses')"
        />

        <el-option
            v-for="status in statuses"
            :key="status"
            :value="status"
            :label="statusLabel(status)"
        />
      </el-select>
    </div>

    <div
        v-loading="loading"
        class="admin-orders__table-wrapper"
    >
      <el-table
          :data="filteredOrders"
          row-key="id"
          class="coffee-table admin-orders__table"
          :empty-text="t('orders.empty')"
      >
        <el-table-column type="expand">
          <template #default="{ row }">
            <div class="order-details">
              <h3 class="order-details__title">
                {{ t('orders.itemsTitle') }}
              </h3>

              <el-table
                  :data="row.items"
                  class="coffee-table order-details__table"
                  :empty-text="t('orders.emptyItems')"
              >
                <el-table-column
                    prop="productName"
                    :label="t('orders.product')"
                    min-width="150"
                />

                <el-table-column
                    :label="t('orders.unitPrice')"
                    width="130"
                    align="right"
                >
                  <template #default="{ row: item }">
                    {{ formatPrice(item.unitPrice) }}
                  </template>
                </el-table-column>

                <el-table-column
                    prop="quantity"
                    :label="t('orders.quantity')"
                    width="120"
                    align="center"
                />

                <el-table-column
                    :label="t('orders.itemTotal')"
                    width="140"
                    align="right"
                >
                  <template #default="{ row: item }">
                    <strong>
                      {{ formatPrice(item.totalPrice) }}
                    </strong>
                  </template>
                </el-table-column>
              </el-table>

              <div class="order-details__total">
                <span>{{ t('orders.orderTotal') }}</span>
                <strong>{{ formatPrice(row.totalPrice) }}</strong>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            prop="id"
            :label="t('orders.orderNumber')"
            width="100"
            sortable
        >
          <template #default="{ row }">
            <strong>#{{ row.id }}</strong>
          </template>
        </el-table-column>

        <el-table-column
            :label="t('orders.createdAt')"
            min-width="180"
            sortable
            prop="createdAt"
        >
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>

        <el-table-column
            :label="t('orders.statusLabel')"
            min-width="190"
        >
          <template #default="{ row }">
            <el-select
                :model-value="row.status"
                :loading="updatingIds.includes(row.id)"
                :disabled="updatingIds.includes(row.id)"
                class="coffee-select"
                style="width: 200px"
                @change="
                (value: OrderStatus) => changeStatus(row, value)
              "
            >
              <el-option
                  v-for="status in availableStatuses(row.status)"
                  :key="status"
                  :value="status"
                  :label="statusLabel(status)"
              />
            </el-select>

            <div class="admin-orders__status-preview">
              <el-tag :type="statusType(row.status)" effect="light">
                {{ statusLabel(row.status) }}
              </el-tag>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            :label="t('orders.orderTotal')"
            min-width="140"
            align="right"
            sortable
            prop="totalPrice"
        >
          <template #default="{ row }">
            <strong class="admin-orders__price">
              {{ formatPrice(row.totalPrice) }}
            </strong>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </section>
</template>

<style scoped>
.admin-orders {
  width: 100%;
  min-width: 0;
  padding: 28px;
  color: var(--coffee-text);
}

.admin-orders__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 28px;
}

.admin-orders__eyebrow {
  margin: 0 0 8px;
  color: var(--coffee-accent);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.1em;
  text-transform: uppercase;
}

.admin-orders__title {
  margin: 0;
  color: var(--coffee-text-dark);
  font-size: clamp(24px, 3vw, 32px);
  font-weight: 700;
}

.admin-orders__description {
  margin: 10px 0 0;
  color: var(--coffee-text-muted);
  font-size: 14px;
}

.admin-orders__toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

.admin-orders__count {
  color: var(--coffee-text-muted);
  font-size: 14px;
}

.admin-orders__count strong {
  color: var(--coffee-text-dark);
}

.admin-orders__filter {
  width: 220px;
}

.admin-orders__table-wrapper {
  min-height: 180px;
  overflow-x: auto;
  border: 1px solid var(--coffee-border);
  border-radius: 14px;
}

.admin-orders__table {
  width: 100%;
}

.admin-orders__status-select {
  width: 100%;
  max-width: 180px;
}

.admin-orders__status-preview {
  display: none;
}

.admin-orders__price {
  color: var(--coffee-text-dark);
  white-space: nowrap;
}

.order-details {
  padding: 12px 24px 20px;
}

.order-details__title {
  margin: 0 0 14px;
  color: var(--coffee-text-dark);
  font-size: 16px;
  font-weight: 600;
}

.order-details__table {
  width: 100%;
}

.order-details__total {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 24px;
  padding-top: 16px;
  color: var(--coffee-text);
}

.order-details__total strong {
  color: var(--coffee-text-dark);
  font-size: 18px;
}

@media (max-width: 768px) {
  .admin-orders {
    padding: 18px 12px;
  }

  .admin-orders__header {
    align-items: flex-start;
    flex-direction: column;
  }

  .admin-orders__toolbar {
    align-items: stretch;
    flex-direction: column;
  }

  .admin-orders__filter {
    width: 100%;
  }

  .order-details {
    padding: 12px 4px 16px;
  }
}

</style>