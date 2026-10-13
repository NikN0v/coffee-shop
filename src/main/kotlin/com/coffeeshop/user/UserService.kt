package com.coffeeshop.user

import com.coffeeshop.user.dto.LoginRequest
import com.coffeeshop.user.dto.RegisterRequest
import com.coffeeshop.user.dto.UserResponse
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun register(request: RegisterRequest): UserResponse {
        val email = request.email.trim().lowercase()

        if (userRepository.existsByEmail(email)) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "Пользователь с таким email уже существует"
            )
        }

        val user = User(
            email = email,
            passwordHash = requireNotNull(passwordEncoder.encode(request.password)),
            role = UserRole.USER,
            name = request.name.trim()
        )

        val saved = userRepository.save(user)

        return UserResponse(
            id = requireNotNull(saved.id),
            email = saved.email,
            role = saved.role.name,
            name = saved.name
        )
    }

    @Transactional(readOnly = true)
    fun login(request: LoginRequest): UserResponse {
        val email = request.email.trim().lowercase()

        val user = userRepository.findByEmail(email)
            ?: throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Неверный email или пароль"
            )

        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Неверный email или пароль"
            )
        }

        return UserResponse(
            id = requireNotNull(user.id),
            email = user.email,
            role = user.role.name,
            name = user.name
        )
    }
}