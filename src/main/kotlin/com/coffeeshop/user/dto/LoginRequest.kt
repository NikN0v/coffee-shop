package com.coffeeshop.user.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(

    @field:NotBlank(message = "Email обязателен")
    @field:Email(message = "Некорректный email")
    val email: String,

    @field:NotBlank(message = "Пароль обязателен")
    @field:Size(max = 72)
    val password: String
)