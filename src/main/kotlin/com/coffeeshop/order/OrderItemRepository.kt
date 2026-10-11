package com.coffeeshop.order

import org.springframework.data.jpa.repository.JpaRepository

interface OrderItemRepository : JpaRepository<OrderItem, Long> {
    fun findAllByOrderId(orderId: Long): List<OrderItem>

    fun findAllByOrderIdIn(orderIds: Collection<Long>): List<OrderItem>
}