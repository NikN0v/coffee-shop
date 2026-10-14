import {defineStore} from "pinia";
import {ref} from "vue";
import type {
    ProductCreateRequest,
    ProductPatchRequest,
    ProductQueryParams,
    ProductResponse,
    ProductUpdateRequest
} from "@/types/product.ts";
import {productApi} from "@/api/productApi.ts";

export const useProductStore = defineStore('product', () => {
    const products = ref<ProductResponse[]>([])
    const isLoading = ref(false)
    const isSaving = ref(false)

    const page = ref(0)
    const size = ref(10)
    const totalElements = ref(0)
    const totalPages = ref(0)

    const categoryId = ref<number | undefined>()
    const available = ref<boolean | undefined>()
    const sortBy = ref('id')
    const direction = ref<'asc' | 'desc'>('asc')

    async function loadProducts(): Promise<void> {
        isLoading.value = true

        try {
            const params: ProductQueryParams = {
                page: page.value,
                size: size.value,
                sortBy: sortBy.value,
                direction: direction.value
            }

            if (categoryId.value !== undefined) params.categoryId = categoryId.value
            if (available.value !== undefined) params.available = available.value

            const {data} = await productApi.getAll(params)

            products.value = data.content
            page.value = data.page
            size.value = data.size
            totalElements.value = data.totalElements
            totalPages.value = data.totalPages
        } finally {
            isLoading.value = false
        }
    }

    async function createProduct(request: ProductCreateRequest): Promise<boolean> {
        isSaving.value = true

        try {
            await productApi.create(request)
            await loadProducts()
            return true
        } finally {
            isSaving.value = false
        }
    }

    async function updateProduct(
        id: number,
        request: ProductUpdateRequest,
    ): Promise<boolean> {
        isSaving.value = true

        try {
            await productApi.update(id, request)
            await loadProducts()
            return true
        } catch {
            return false
        } finally {
            isSaving.value = false
        }
    }

    async function patchProduct(
        id: number,
        request: ProductPatchRequest,
    ): Promise<boolean> {
        isSaving.value = true

        try {
            await productApi.patch(id, request)
            await loadProducts()
            return true
        } catch {
            return false
        } finally {
            isSaving.value = false
        }
    }

    async function deleteProduct(id: number): Promise<boolean> {
        isSaving.value = true

        try {
            await productApi.delete(id)
            await loadProducts()
            return true
        } catch {
            return false
        } finally {
            isSaving.value = false
        }
    }

    function setCategoryFilter(value?: number): void {
        categoryId.value = value
        page.value = 0
    }

    function setAvailabilityFilter(value?: boolean): void {
        available.value = value
        page.value = 0
    }

    function setPage(value: number): void {
        page.value = value
    }

    function setPageSize(value: number): void {
        size.value = value
        page.value = 0
    }

    return {
        products,
        isLoading,
        isSaving,
        page,
        size,
        totalElements,
        totalPages,
        categoryId,
        available,
        sortBy,
        direction,
        loadProducts,
        createProduct,
        updateProduct,
        patchProduct,
        deleteProduct,
        setCategoryFilter,
        setAvailabilityFilter,
        setPage,
        setPageSize,
    }
})