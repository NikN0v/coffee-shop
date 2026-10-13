import {defineStore} from "pinia";
import {computed, ref} from "vue";
import type {CategoryCreateRequest, CategoryResponse, CategoryUpdateRequest} from "@/types/category.ts";
import {categoryApi} from "@/api/categoryApi.ts";

export const useCategoryStore = defineStore('category', () => {
    const categories = ref<CategoryResponse[]>([])
    const isLoading = ref(false)
    const isSaving = ref(false)
    const error = ref<unknown>(null)

    const isEmpty = computed(() => !isLoading.value && categories.value.length === 0)

    async function fetchCategories() {
        isLoading.value = true
        error.value = null

        try {
            const res = await categoryApi.getAll()
            categories.value = res.data
        } catch (err: unknown) {
            error.value = err
            throw err
        } finally {
            isLoading.value = false
        }
    }

    async function createCategory(request: CategoryCreateRequest) {
        isSaving.value = true

        try {
            await categoryApi.create(request)
            await fetchCategories()
        } finally {
            isSaving.value = false
        }
    }

    async function updateCategory(id: number, request: CategoryUpdateRequest) {
        isSaving.value = true

        try {
            await categoryApi.update(id, request)
            await fetchCategories()
        } finally {
            isSaving.value = false
        }
    }

    async function deleteCategory(id: number) {
        isSaving.value = true

        try {
            await categoryApi.delete(id)
            categories.value = categories.value.filter(category => category.id !== id)
        } finally {
            isSaving.value = false
        }
    }

    function clearCategories() {
        categories.value = []
        error.value = null
    }

    return {
        categories, isLoading, error, isEmpty, isSaving,
        fetchCategories, clearCategories, createCategory, updateCategory, deleteCategory
    }
})