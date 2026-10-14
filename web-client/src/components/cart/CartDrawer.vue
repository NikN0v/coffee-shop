<script setup lang="ts">
import {useI18n} from "vue-i18n";
import {useRouter} from "vue-router";
import {useCartStore} from "@/stores/cartStore.ts";
import {useAuthStore} from "@/stores/authStore.ts";
import {computed, ref, watch} from "vue";
import type {ProductResponse} from "@/types/product.ts";
import {ElMessageBox} from "element-plus";
import {notificationService} from "@/services/notificationService.ts";
import {productApi} from "@/api/productApi.ts";
import type {OrderCreateRequest} from "@/types/order.ts";
import {orderApi} from "@/api/orderApi.ts";
import UiNumber from "@/components/ui/UiNumber.vue";
import UiButton from "@/components/ui/UiButton.vue";

const props = defineProps<{
  modelValue: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
}>()

const {t, locale} = useI18n()
const router = useRouter()
const cartStore = useCartStore()
const authStore = useAuthStore()

const products = ref<ProductResponse[]>([])
const loading = ref(false)
const submitting = ref(false)

const drawerVisible = computed({
  get: () => props.modelValue,
  set: (val: boolean) => emit('update:modelValue', val)
})

const cartProducts = computed(() =>
    cartStore.items.flatMap(item => {
      const product = products.value.find(
          product => product.id === item.productId
      )
      return product ? [{...item, product}] : []
    })
)

const subtotal = computed(() =>
    cartProducts.value.reduce(
        (sum, item) => sum + item.product.price * item.quantity, 0
    )
)

const currencyFormatter = computed(() =>
    new Intl.NumberFormat(locale.value === 'ru' ? 'ru-RU' : 'en-IE', {
      style: 'currency',
      currency: 'RUB'
    })
)

function formatPrice(price: number): string {
  return currencyFormatter.value.format(price)
}

function getProductName(product: ProductResponse): string {
  if (locale.value === 'ru') return product.nameRu || product.name
  return product.nameEn || product.name
}

async function loadProducts(): Promise<void> {
  if (cartStore.isEmpty) {
    products.value = []
    return
  }

  loading.value = true

  try {
    const results = await Promise.allSettled(
        cartStore.items.map(({productId}) =>
            productApi.getByIdForCart(productId),
        ),
    )

    products.value = results.flatMap((result) =>
        result.status === 'fulfilled' ? [result.value] : [],
    )

    const failedCount = results.filter(
        (result) => result.status === 'rejected',
    ).length

    if (failedCount > 0) {
      notificationService.error({
        title: t('common.error'),
        message: t('cart.loadProductsError'),
      })
    }
  } finally {
    loading.value = false
  }
}

function changeQuantity(productId: number, quantity: number): void {
  cartStore.setQuantity(productId, quantity)
}

async function removeProduct(productId: number): Promise<void> {
  try {
    await ElMessageBox.confirm(
        t('cart.removeConfirmation'),
        t('cart.confirmTitle'),
        {
          confirmButtonText: t('cart.remove'),
          cancelButtonText: t('common.cancel'),
          type: 'warning',
        },
    )

    cartStore.removeProduct(productId)
  } catch {
    // Пользователь отменил удаление.
  }
}

async function checkout(): Promise<void> {
  if (cartStore.isEmpty || submitting.value) return

  if (!authStore.isAuthenticated) {
    drawerVisible.value = false
    await router.push({
      name: 'login',
      query: {redirect: '/menu'},
    })
    return
  }

  if (loading.value) return

  // Нельзя оформлять заказ, если какие-то товары не удалось загрузить.
  if (cartProducts.value.length !== cartStore.items.length) {
    notificationService.error({
      title: t('common.error'),
      message: t('cart.checkProductsError'),
    })
    return
  }

  const unavailableProduct = cartProducts.value.find(
      (item) => !item.product.available,
  )

  if (unavailableProduct) {
    notificationService.warning({
      title: t('cart.unavailableTitle'),
      message: t('cart.unavailableMessage', {
        name: getProductName(unavailableProduct.product),
      }),
    })
    return
  }

  const request: OrderCreateRequest = {
    items: cartStore.items.map(({productId, quantity}) => ({
      productId, quantity
    }))
  }

  submitting.value = true

  try {
    const order = await orderApi.createOrder(request)

    cartStore.clearCart()
    products.value = []
    drawerVisible.value = false

    notificationService.success({
      title: t('cart.orderSuccessTitle'),
      message: t('cart.orderSuccessMessage', {id: order.id})
    })

    // await router.push({name: 'orders'})
  } catch {
    notificationService.error({
      title: t('common.error'),
      message: t('cart.orderError')
    })
  } finally {
    submitting.value = false
  }
}

watch(
    () => [
      props.modelValue,
      cartStore.items.map(item => `${item.productId}:${item.quantity}`).join(',')
    ],
    ([isOpen]) => {
      if (isOpen) {
        void loadProducts()
      }
    },
    {immediate: true}
)
</script>

<template>
  <el-drawer
      v-model="drawerVisible"
      :title="t('cart.title')"
      direction="rtl"
      size="min(460px, 100%)"
      class="cart-drawer"
  >
    <div v-loading="loading" class="cart-content">
      <el-empty v-if="cartStore.isEmpty" :description="t('cart.empty')"/>

      <div v-else class="cart-items">
        <article
            v-for="item in cartProducts"
            :key="item.productId"
            class="cart-item"
        >
          <div class="cart-item__details">
            <h3 class="cart-item__name">
              {{ getProductName(item.product) }}
            </h3>

            <p class="cart-item__price">
              {{ formatPrice(item.product.price) }}
              {{ t('cart.perItem') }}
            </p>

            <p class="cart-item__unavailable" v-if="!item.product.available">
              {{ t('cart.unavailable') }}
            </p>

            <UiNumber v-model="item.quantity" size="small" :min="1" :max="99"
                      @change="(val) => changeQuantity(item.productId, val ?? 1)"/>
          </div>

          <div class="cart-item__actions">
            <strong>
              {{ formatPrice(item.product.price * item.quantity) }}
            </strong>

            <UiButton type="danger" text @click="removeProduct(item.productId)">
              {{ t('cart.remove') }}
            </UiButton>
          </div>
        </article>
      </div>
    </div>

    <template #footer>
      <div class="cart-footer">
        <div class="cart-footer__total">
          <span>{{ t('cart.subtotal') }}</span>
          <strong>{{ formatPrice(subtotal) }}</strong>
        </div>

        <p class="cart-footer__note">
          {{ t('cart.priceNote') }}
        </p>

        <UiButton
            type="primary"
            size="large"
            :loading="submitting"
            :disabled="cartStore.isEmpty || loading || cartProducts.length !== cartStore.items.length || cartProducts.some(item => !item.product.available)"
            @click="checkout"
        >
          {{ t('cart.checkout') }}
        </UiButton>
      </div>
    </template>
  </el-drawer>
</template>

<style scoped>
.cart-content {
  min-height: 180px;
}

.cart-items {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.cart-item {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  padding-bottom: 18px;
  border-bottom: 1px solid var(--coffee-border);
}

.cart-item__details {
  display: flex;
  flex: 1;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
  min-width: 0;
}

.cart-item__name {
  margin: 0;
  color: var(--coffee-text-dark);
  font-size: 15px;
  font-weight: 600;
  overflow-wrap: anywhere;
}

.cart-item__price {
  margin: 0;
  color: var(--coffee-text-muted);
  font-size: 13px;
}

.cart-item__unavailable {
  margin: 0;
  color: #b54735;
  font-size: 13px;
}

.cart-item__actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: space-between;
  gap: 8px;
  flex-shrink: 0;
  color: var(--coffee-text-dark);
}

.cart-footer {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.cart-footer__total {
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: var(--coffee-text-dark);
  font-size: 18px;
}

.cart-footer__note {
  margin: 0;
  color: var(--coffee-text-muted);
  font-size: 12px;
}

.cart-footer .el-button {
  width: 100%;
}

@media (max-width: 380px) {
  .cart-item {
    flex-direction: column;
  }

  .cart-item__actions {
    flex-direction: row;
    align-items: center;
  }
}
</style>