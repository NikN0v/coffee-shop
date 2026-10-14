import type {OrderCreateRequest, OrderResponse} from "@/types/order.ts";
import {api} from "@/api/axios.ts";

export const orderApi = {
    async createOrder(request: OrderCreateRequest): Promise<OrderResponse> {
        const { data } = await api.post<OrderResponse>('/orders', request)
        return data
    }
}