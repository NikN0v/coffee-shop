package com.coffeeshop.category.dto

import com.coffeeshop.category.CategoryType

data class CategoryResponse(
    val id: Long,
    val name: String,
    val type: CategoryType,
    val nameRu: String,
    val nameEn: String
)