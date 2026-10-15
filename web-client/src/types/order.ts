export type OrderStatus =
    | 'CREATED'
    | 'ACCEPTED'
    | 'PREPARING'
    | 'READY'
    | 'PICKED_UP'
    | 'CANCELLED'

export type OrderItemRequest = {
    productId: number
    quantity: number
}

export type OrderCreateRequest = {
    items: OrderItemRequest[]
}

export type OrderItemResponse = {
    productId: number
    productName: string
    unitPrice: number
    quantity: number
    totalPrice: number
}

export type OrderResponse = {
    id: number
    status: string
    totalPrice: number
    createdAt: string
    items: OrderItemResponse[]
}

export type OrderStatusRequest = {
    status: OrderStatus
}