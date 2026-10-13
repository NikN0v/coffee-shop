import {api} from "@/api/axios.ts";
import type {CategoryCreateRequest, CategoryResponse, CategoryUpdateRequest} from "@/types/category.ts";

export const categoryApi = {
    getAll() {
        return api.get<CategoryResponse[]>('/categories')
    },

    create(request: CategoryCreateRequest) {
        return api.post<CategoryResponse>('/categories', request)
    },

    update(id: number, request: CategoryUpdateRequest) {
        return api.put<CategoryResponse>(`/categories/${id}`, request)
    },

    delete(id: number) {
        return api.delete<void>(`/categories/${id}`)
    }
}