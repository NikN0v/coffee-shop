package com.coffeeshop.order

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface OrderRepository : JpaRepository<Order, Long> {
    fun findAllByUserIdOrderByCreatedAtDesc(userId: Long): List<Order>

    fun findByIdAndUserId(id: Long, userId: Long): Optional<Order>

    fun findAllByOrderByCreatedAtDesc(): List<Order>
}