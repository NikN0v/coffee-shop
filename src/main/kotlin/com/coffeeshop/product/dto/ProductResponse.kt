package com.coffeeshop.product.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class ProductResponse(
    val id: Long,
    val categoryId: Long,
    val name: String,
    val nameRu: String,
    val nameEn: String,
    val description: String?,
    val descriptionRu: String?,
    val descriptionEn: String?,
    val price: BigDecimal,
    val available: Boolean,
    val weight: Int?,

    @JsonProperty("isDrink")
    val isDrink: Boolean,

    val volume: Int?
)