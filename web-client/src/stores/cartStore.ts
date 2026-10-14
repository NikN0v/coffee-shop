import { computed, ref, watch } from 'vue'
import { defineStore } from 'pinia'
import type { CartItem } from '@/types/cart'

const STORAGE_KEY = 'coffee-shop-cart'

function readCart(): CartItem[] {
    if (typeof localStorage === 'undefined') {
        return []
    }

    try {
        const raw = localStorage.getItem(STORAGE_KEY)

        if (!raw) return []

        const parsed: unknown = JSON.parse(raw)

        if (!Array.isArray(parsed)) return []

        return parsed.filter((value: unknown): value is CartItem => {
            if (typeof value !== 'object' || value === null) {
                return false
            }

            const item = value as Record<string, unknown>

            return (
                typeof item.productId === 'number' &&
                Number.isSafeInteger(item.productId) &&
                item.productId > 0 &&
                typeof item.quantity === 'number' &&
                Number.isSafeInteger(item.quantity) &&
                item.quantity > 0
            )
        })
    } catch {
        return []
    }
}

export const useCartStore = defineStore('cart', () => {
    const items = ref<CartItem[]>(readCart())

    const totalQuantity = computed(() =>
        items.value.reduce((sum, item) => sum + item.quantity, 0),
    )

    const isEmpty = computed(() => items.value.length === 0)

    function addProduct(productId: number, quantity = 1): void {
        if (
            !Number.isSafeInteger(productId) ||
            productId <= 0 ||
            !Number.isSafeInteger(quantity) ||
            quantity <= 0
        ) {
            return
        }

        const existing = items.value.find(
            (item) => item.productId === productId,
        )

        if (existing) {
            existing.quantity += quantity
            return
        }

        items.value.push({ productId, quantity })
    }

    function setQuantity(productId: number, quantity: number): void {
        if (!Number.isSafeInteger(quantity)) return

        if (quantity <= 0) {
            removeProduct(productId)
            return
        }

        const item = items.value.find(
            (item) => item.productId === productId,
        )

        if (item) {
            item.quantity = quantity
        }
    }

    function removeProduct(productId: number): void {
        items.value = items.value.filter(
            (item) => item.productId !== productId,
        )
    }

    function clearCart(): void {
        items.value = []
    }

    if (typeof localStorage !== 'undefined') {
        watch(
            items,
            (value) => {
                try {
                    localStorage.setItem(STORAGE_KEY, JSON.stringify(value))
                } catch {
                    // Корзина продолжает работать в памяти.
                }
            },
            { deep: true },
        )
    }

    return {
        items,
        totalQuantity,
        isEmpty,
        addProduct,
        setQuantity,
        removeProduct,
        clearCart,
    }
})