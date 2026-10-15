import type {OrderCreateRequest, OrderResponse, OrderStatus, OrderStatusRequest} from "@/types/order.ts";
import {api} from "@/api/axios.ts";

export const orderApi = {
    async getAll(): Promise<OrderResponse[]> {
        const {data} = await api.get<OrderResponse[]>('/orders/admin')
        return data
    },

    async updateStatus(orderId: number, status: OrderStatus): Promise<OrderResponse> {
        const request: OrderStatusRequest = {status}
        const {data} = await api.patch<OrderResponse>(`/orders/${orderId}/status`, request)
        return data
    },

    async createOrder(request: OrderCreateRequest): Promise<OrderResponse> {
        const {data} = await api.post<OrderResponse>('/orders', request)
        return data
    },

    async getMyOrders(): Promise<OrderResponse[]> {
        const {data} = await api.get<OrderResponse[]>('/orders')
        return data
    },

    async getMyOrder(id: number): Promise<OrderResponse> {
        const {data} = await api.get<OrderResponse>(`/orders/${id}`)
        return data
    },

    async cancelMyOrder(id: number): Promise<OrderResponse> {
        const { data } = await api.patch(`/orders/${id}/cancel`)
        return data
    }
}