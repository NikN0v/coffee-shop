package com.coffeeshop.category.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CategoryUpdateRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val nameRu: String,

    @field:NotBlank
    @field:Size(max = 100)
    val nameEn: String,
)