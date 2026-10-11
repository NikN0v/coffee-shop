package com.coffeeshop.order

import com.coffeeshop.order.dto.OrderCreateRequest
import com.coffeeshop.order.dto.OrderResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/orders")
class OrderController(
    private val orderService: OrderService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @AuthenticationPrincipal jwt: Jwt,
        @Valid @RequestBody request: OrderCreateRequest
    ): OrderResponse {
        return orderService.create(jwt.subject, request)
    }

    @GetMapping
    fun getMyOrders(
        @AuthenticationPrincipal jwt: Jwt
    ): List<OrderResponse> =
        orderService.getMyOrders(jwt.subject)

    @GetMapping("/{id}")
    fun getMyOrder(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: Long
    ): OrderResponse =
        orderService.getMyOrder(jwt.subject, id)
}