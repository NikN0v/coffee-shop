package com.coffeeshop.product.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class ProductCreateRequest(

    @field:NotNull(message = "Категория обязательна")
    val categoryId: Long?,

    @field:NotBlank(message = "Название на русском обязательно")
    @field:Size(max = 150)
    val nameRu: String,

    @field:NotBlank(message = "Название на английском обязательно")
    @field:Size(max = 150)
    val nameEn: String,

    val descriptionRu: String? = null,

    val descriptionEn: String? = null,

    @field:NotNull(message = "Цена обязательна")
    @field:DecimalMin(value = "0.01", message = "Цена должна быть больше нуля")
    val price: BigDecimal?,

    val available: Boolean = true,

    @field:Min(1)
    val weight: Int? = null,

    val isDrink: Boolean = false,

    @field:Min(1)
    val volume: Int? = null
)