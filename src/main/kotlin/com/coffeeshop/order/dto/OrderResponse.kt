package com.coffeeshop.order.dto

import com.coffeeshop.order.OrderStatus
import java.math.BigDecimal
import java.time.Instant

data class OrderResponse(
    val id: Long,
    val status: OrderStatus,
    val totalPrice: BigDecimal,
    val createdAt: Instant?,
    val items: List<OrderItemResponse>
)

data class OrderItemResponse(
    val productId: Long,
    val productName: String,
    val unitPrice: BigDecimal,
    val quantity: Int,
    val totalPrice: BigDecimal
)