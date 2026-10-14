import type {
    PageResponse,
    ProductCreateRequest, ProductPatchRequest,
    ProductQueryParams,
    ProductResponse,
    ProductUpdateRequest
} from "@/types/product.ts";
import {api} from "@/api/axios.ts";

export const productApi  = {
    getAll(params: ProductQueryParams = {}) {
        return api.get<PageResponse<ProductResponse>>('/products', {params})
    },

    getById(id: number) {
        return api.get<ProductResponse>(`/products/${id}`)
    },

    create(request: ProductCreateRequest) {
        return api.post<ProductResponse>('/products', request)
    },

    update(id: number, request: ProductUpdateRequest) {
        return api.put<ProductResponse>(`/products/${id}`, request)
    },

    patch(id: number, request: ProductPatchRequest) {
        return api.patch<ProductResponse>(`/products/${id}`, request)
    },

    delete(id: number) {
        return api.delete<void>(`/products/${id}`)
    }
}