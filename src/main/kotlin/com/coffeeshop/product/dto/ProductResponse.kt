package com.coffeeshop.product.dto

import java.math.BigDecimal

data class ProductResponse(
    val id: Long,
    val categoryId: Long,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val available: Boolean,
    val weight: Int?,
    val isDrink: Boolean,
    val volume: Int?
)