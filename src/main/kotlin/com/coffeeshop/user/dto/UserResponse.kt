package com.coffeeshop.user.dto

data class UserResponse(
    val id: Long,
    val email: String,
    val role: String
)