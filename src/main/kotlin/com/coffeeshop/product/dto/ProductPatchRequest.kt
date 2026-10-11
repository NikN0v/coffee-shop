package com.coffeeshop.product.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class ProductPatchRequest(

    val categoryId: Long? = null,

    @field:Size(max = 150, message = "Название на русском не может быть длиннее 150 символов")
    val nameRu: String? = null,

    @field:Size(max = 150, message = "Название на английском не может быть длиннее 150 символов")
    val nameEn: String? = null,

    val descriptionRu: String? = null,

    val descriptionEn: String? = null,

    @field:DecimalMin(value = "0.01", message = "Цена должна быть больше нуля")
    val price: BigDecimal? = null,

    val available: Boolean? = null,

    @field:Min(1, message = "Вес должен быть больше нуля")
    val weight: Int? = null,

    val isDrink: Boolean? = null,

    @field:Min(1, message = "Объём должен быть больше нуля")
    val volume: Int? = null
)
