package com.coffeeshop.category.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CategoryCreateRequest(

    @field:NotBlank(message = "Название на русском обязательно")
    @field:Size(max = 100)
    val nameRu: String,

    @field:NotBlank(message = "Название на английском обязательно")
    @field:Size(max = 100)
    val nameEn: String,
)