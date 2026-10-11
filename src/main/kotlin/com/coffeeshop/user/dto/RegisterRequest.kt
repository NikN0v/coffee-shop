package com.coffeeshop.user.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(

    @field:NotBlank(message = "Email обязателен")
    @field:Email(message = "Некорректный email")
    @field:Size(max = 255)
    val email: String,

    @field:NotBlank(message = "Пароль обязателен")
    @field:Size(
        min = 8,
        max = 72,
        message = "Пароль должен содержать от 8 до 72 символов"
    )
    val password: String
)