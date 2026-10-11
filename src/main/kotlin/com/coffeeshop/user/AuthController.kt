package com.coffeeshop.user

import com.coffeeshop.user.dto.AuthResponse
import com.coffeeshop.user.dto.LoginRequest
import com.coffeeshop.user.dto.RegisterRequest
import com.coffeeshop.user.dto.UserResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userService: UserService,
    private val jwtService: JwtService
) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(
        @Valid @RequestBody request: RegisterRequest
    ): UserResponse {
        return userService.register(request)
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest
    ): AuthResponse {
        val user = userService.login(request)

        return AuthResponse(
            accessToken = jwtService.generateToken(user),
            user = user
        )
    }
}