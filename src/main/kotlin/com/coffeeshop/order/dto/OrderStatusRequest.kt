package com.coffeeshop.order.dto

import com.coffeeshop.order.OrderStatus
import org.jetbrains.annotations.NotNull

data class OrderStatusRequest(
    @field:NotNull
    val status: OrderStatus
)