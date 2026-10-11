package com.coffeeshop.order.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Positive

data class OrderCreateRequest(
    @field:NotEmpty
    @field:Valid
    val items: List<OrderItemRequest>
)

data class OrderItemRequest(
    @field:Positive
    val productId: Long,

    @field:Positive
    val quantity: Int
)